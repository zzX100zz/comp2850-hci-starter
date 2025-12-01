package routes

import data.TaskRepository
import io.ktor.http.*
import io.ktor.server.application.*
import io.ktor.server.request.*
import io.ktor.server.response.*
import io.ktor.server.routing.*
import io.pebbletemplates.pebble.PebbleEngine
import java.io.StringWriter
import utils.Logger
import utils.timed
import java.util.UUID

fun Route.taskRoutes() {
    val pebble =
        PebbleEngine
            .Builder()
            .loader(
                io.pebbletemplates.pebble.loader.ClasspathLoader().apply {
                    prefix = "templates/"
                },
            ).build()

    fun ApplicationCall.isHtmx(): Boolean = request.headers["HX-Request"]?.equals("true", ignoreCase = true) == true

    // GET /tasks - List tasks with Search & Pagination
    get("/tasks") {
        val query = call.request.queryParameters["q"] ?: ""
        val page = call.request.queryParameters["page"]?.toIntOrNull() ?: 1
        val editingId = call.request.queryParameters["editing"]?.toIntOrNull()
        val pageSize = 5 

        val (tasks, totalCount) = TaskRepository.search(query, page, pageSize)
        val totalPages = if (totalCount > 0) (totalCount + pageSize - 1) / pageSize else 1

        val model = mapOf(
            "title" to "Tasks",
            "tasks" to tasks,
            "editingId" to editingId,
            "q" to query,
            "page" to page,
            "totalPages" to totalPages,
            "totalCount" to totalCount
        )

        val template = pebble.getTemplate("tasks/index.peb")
        val writer = StringWriter()
        template.evaluate(writer, model)
        call.respondText(writer.toString(), ContentType.Text.Html)
    }

    // POST /tasks - Add new task (With Feedback Fix)
    post("/tasks") {
        val requestId = UUID.randomUUID().toString().substring(0, 8)
        val sessionId = call.request.queryParameters["sid"] ?: "anonymous"
        val isHtmx = call.isHtmx()

        val (result, duration) = timed {
            val title = call.receiveParameters()["title"].orEmpty().trim()

            if (title.isBlank()) {
                Logger.log(sessionId, requestId, "T1_Add", "validate", "validation_error", 0, 400, isHtmx)
                if (call.isHtmx()) {
                    val error = """<div id="status" hx-swap-oob="true" role="alert" aria-live="assertive">Title is required.</div>"""
                    return@timed call.respondText(error, ContentType.Text.Html, HttpStatusCode.BadRequest)
                } else {
                    call.response.headers.append("Location", "/tasks")
                    return@timed call.respond(HttpStatusCode.SeeOther)
                }
            }

            TaskRepository.add(title)
            "success"
        }

        if (result == "success") {
            Logger.log(sessionId, requestId, "T1_Add", "persist", "success", duration, 201, isHtmx)

            if (call.isHtmx()) {
                val query = call.request.queryParameters["q"] ?: ""
                val page = call.request.queryParameters["page"]?.toIntOrNull() ?: 1
                val (tasks, totalCount) = TaskRepository.search(query, page, 5)
                val totalPages = if (totalCount > 0) (totalCount + 5 - 1) / 5 else 1

                val model = mapOf(
                    "tasks" to tasks, 
                    "q" to query, 
                    "page" to page, 
                    "totalCount" to totalCount, 
                    "totalPages" to totalPages
                )
                val template = pebble.getTemplate("tasks/index.peb")
                val writer = StringWriter()
                template.evaluate(writer, model)
                
                // [Week 10 Critical Fix] Capture HTML and append Feedback
                val html = writer.toString()
                val feedback = """
                    <div id="sr-announcer" hx-swap-oob="true" 
                         role="status" 
                         aria-live="polite" 
                         style="background-color: #d4edda; color: #155724; padding: 10px; margin-bottom: 15px; border-radius: 4px; border: 1px solid #c3e6cb;">
                        ✅ Task "${call.receiveParameters()["title"]?.trim()}" added successfully.
                    </div>
                """.trimIndent()

                call.respondText(html + feedback, ContentType.Text.Html, HttpStatusCode.Created)
            } else {
                call.response.headers.append("Location", "/tasks")
                call.respond(HttpStatusCode.SeeOther)
            }
        }
    }

    // POST /tasks/{id}/delete
    post("/tasks/{id}/delete") {
        val requestId = UUID.randomUUID().toString().substring(0, 8)
        val sessionId = call.request.queryParameters["sid"] ?: "anonymous"
        val isHtmx = call.isHtmx()

        val (removed, duration) = timed {
            val id = call.parameters["id"]?.toIntOrNull()
            id?.let { TaskRepository.delete(it) } ?: false
        }

        Logger.log(sessionId, requestId, "T3_Delete", "persist", if (removed) "success" else "not_found", duration, 200, isHtmx)

        if (call.isHtmx()) {
            val query = call.request.queryParameters["q"] ?: ""
            val page = call.request.queryParameters["page"]?.toIntOrNull() ?: 1
            val (tasks, totalCount) = TaskRepository.search(query, page, 5)
            val totalPages = if (totalCount > 0) (totalCount + 5 - 1) / 5 else 1
            
            val model = mapOf("tasks" to tasks, "q" to query, "page" to page, "totalCount" to totalCount, "totalPages" to totalPages)
            val template = pebble.getTemplate("tasks/index.peb")
            val writer = StringWriter()
            template.evaluate(writer, model)
            call.respondText(writer.toString(), ContentType.Text.Html)
        } else {
            call.response.headers.append("Location", "/tasks")
            call.respond(HttpStatusCode.SeeOther)
        }
    }

    // GET /tasks/{id}/edit
    get("/tasks/{id}/edit") {
        val id = call.parameters["id"]?.toIntOrNull()
        if (id == null) { call.respond(HttpStatusCode.BadRequest); return@get }

        val query = call.request.queryParameters["q"] ?: ""
        val page = call.request.queryParameters["page"]?.toIntOrNull() ?: 1
        val pageSize = 5
        val (tasks, totalCount) = TaskRepository.search(query, page, pageSize)
        val totalPages = if (totalCount > 0) (totalCount + pageSize - 1) / pageSize else 1

        if (call.isHtmx()) {
            val model = mapOf("tasks" to tasks, "editingId" to id, "q" to query, "page" to page, "totalCount" to totalCount, "totalPages" to totalPages)
            val template = pebble.getTemplate("tasks/index.peb")
            val writer = StringWriter()
            template.evaluate(writer, model)
            call.respondText(writer.toString(), ContentType.Text.Html)
        } else {
            call.respondRedirect("/tasks?editing=$id&page=$page&q=$query")
        }
    }

    // POST /tasks/{id} (Update)
    post("/tasks/{id}") {
        val id = call.parameters["id"]?.toIntOrNull()
        val title = call.receiveParameters()["title"]?.trim()
        val requestId = UUID.randomUUID().toString().substring(0, 8)
        val sessionId = call.request.queryParameters["sid"] ?: "anonymous"
        val isHtmx = call.isHtmx()

        val (updated, duration) = timed {
            if (id != null && !title.isNullOrBlank()) { TaskRepository.update(id, title); true } else false
        }
        Logger.log(sessionId, requestId, "T2_Edit", "persist_update", if (updated) "success" else "error", duration, 200, isHtmx)

        if (call.isHtmx()) {
            val query = call.request.queryParameters["q"] ?: ""
            val page = call.request.queryParameters["page"]?.toIntOrNull() ?: 1
            val (tasks, totalCount) = TaskRepository.search(query, page, 5)
            val totalPages = if (totalCount > 0) (totalCount + 5 - 1) / 5 else 1

            val model = mapOf("tasks" to tasks, "q" to query, "page" to page, "totalCount" to totalCount, "totalPages" to totalPages)
            val template = pebble.getTemplate("tasks/index.peb")
            val writer = StringWriter()
            template.evaluate(writer, model)
            call.respondText(writer.toString(), ContentType.Text.Html)
        } else {
            val query = call.request.queryParameters["q"] ?: ""
            val page = call.request.queryParameters["page"] ?: "1"
            call.respondRedirect("/tasks?page=$page&q=$query")
        }
    }

    // GET /tasks/{id} (Cancel)
    get("/tasks/{id}") {
        val query = call.request.queryParameters["q"] ?: ""
        val page = call.request.queryParameters["page"]?.toIntOrNull() ?: 1
        if (call.isHtmx()) {
            val (tasks, totalCount) = TaskRepository.search(query, page, 5)
            val totalPages = if (totalCount > 0) (totalCount + 5 - 1) / 5 else 1
            val model = mapOf("tasks" to tasks, "q" to query, "page" to page, "totalCount" to totalCount, "totalPages" to totalPages)
            val template = pebble.getTemplate("tasks/index.peb")
            val writer = StringWriter()
            template.evaluate(writer, model)
            call.respondText(writer.toString(), ContentType.Text.Html)
        } else {
            call.respondRedirect("/tasks?page=$page&q=$query")
        }
    }
}
