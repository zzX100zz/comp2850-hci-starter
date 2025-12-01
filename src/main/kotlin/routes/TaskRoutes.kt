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

/**
 * NOTE FOR NON-INTELLIJ IDEs (VSCode, Eclipse, etc.):
 * IntelliJ IDEA automatically adds imports as you type. If using a different IDE,
 * you may need to manually add imports. The commented imports below show what you'll need
 * for future weeks. Uncomment them as needed when following the lab instructions.
 *
 * When using IntelliJ: You can ignore the commented imports below - your IDE will handle them.
 */

// Week 7+ imports (inline edit, toggle completion):
// import model.Task               // When Task becomes separate model class
// import model.ValidationResult   // For validation errors
// import renderTemplate            // Extension function from Main.kt
// import isHtmxRequest             // Extension function from Main.kt

// Week 8+ imports (pagination, search, URL encoding):
// import io.ktor.http.encodeURLParameter  // For query parameter encoding
// import utils.Page                       // Pagination helper class

// Week 9+ imports (metrics logging, instrumentation):
// import utils.jsMode              // Detect JS mode (htmx/nojs)
// import utils.logValidationError  // Log validation failures
// import utils.timed               // Measure request timing

// Note: Solution repo uses storage.TaskStore instead of data.TaskRepository
// You may refactor to this in Week 10 for production readiness

/**
 * Week 6 Lab 1: Simple task routes with HTMX progressive enhancement.
 *
 * **Teaching approach**: Start simple, evolve incrementally
 * - Week 6: Basic CRUD with Int IDs
 * - Week 7: Add toggle, inline edit
 * - Week 8: Add pagination, search
 */

fun Route.taskRoutes() {
    val pebble =
        PebbleEngine
            .Builder()
            .loader(
                io.pebbletemplates.pebble.loader.ClasspathLoader().apply {
                    prefix = "templates/"
                },
            ).build()

    /**
     * Helper: Check if request is from HTMX
     */
    fun ApplicationCall.isHtmx(): Boolean = request.headers["HX-Request"]?.equals("true", ignoreCase = true) == true

    /**
     * GET /tasks - List tasks with Search & Pagination (Week 8)
     */
    get("/tasks") {
        // 1. Parse Query Parameters (q = query, page = page number)
        val query = call.request.queryParameters["q"] ?: ""
        val page = call.request.queryParameters["page"]?.toIntOrNull() ?: 1
        val editingId = call.request.queryParameters["editing"]?.toIntOrNull()
        
        // 2. Configuration (Page size could be config constant)
        val pageSize = 5 

        // 3. Get Data from Repository (Using Week 8 search method)
        val (tasks, totalCount) = TaskRepository.search(query, page, pageSize)

        // 4. Calculate Total Pages
        // Logic: (total + pageSize - 1) / pageSize gives ceil(total/size) using integer division
        val totalPages = if (totalCount > 0) (totalCount + pageSize - 1) / pageSize else 1

        val model = mapOf(
            "title" to "Tasks",
            "tasks" to tasks,
            "editingId" to editingId,
            // Week 8: New variables for template
            "q" to query,
            "page" to page,
            "totalPages" to totalPages,
            "totalCount" to totalCount
        )

        // 5. Render Template
        val template = pebble.getTemplate("tasks/index.peb")
        val writer = StringWriter()
        template.evaluate(writer, model)
        call.respondText(writer.toString(), ContentType.Text.Html)
    }


    /**
     * POST /tasks - Add new task
     * Dual-mode: HTMX fragment or PRG redirect
     */
    post("/tasks") {
        val requestId = UUID.randomUUID().toString().substring(0, 8)
        val sessionId = call.request.queryParameters["sid"] ?: "anonymous"
        val isHtmx = call.isHtmx()
        val params = call.receiveParameters()
        val title = params["title"].orEmpty().trim()

        val (result, duration) = timed {
            // 这里的 title 现在是引用外面的变量，而不是新定义
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

        // [Week 9] Log success
        if (result == "success") {
            Logger.log(sessionId, requestId, "T1_Add", "persist", "success", duration, 201, isHtmx)

            if (call.isHtmx()) {
                val query = call.request.queryParameters["q"] ?: ""
                val page = call.request.queryParameters["page"]?.toIntOrNull() ?: 1
                val (tasks, totalCount) = TaskRepository.search(query, page, 5)
                val totalPages = if (totalCount > 0) (totalCount + 5 - 1) / 5 else 1

                // [Week 10 Fix] 现在 title 变量在这里可以被访问到了！
                val model = mapOf(
                    "tasks" to tasks, 
                    "q" to query, 
                    "page" to page, 
                    "totalCount" to totalCount, 
                    "totalPages" to totalPages,
                    "flashMessage" to "Task \"$title\" added successfully." 
                )
                
                val template = pebble.getTemplate("tasks/index.peb")
                val writer = StringWriter()
                template.evaluate(writer, model)
                
                call.respondText(writer.toString(), ContentType.Text.Html, HttpStatusCode.Created)
            } else {
                call.response.headers.append("Location", "/tasks")
                call.respond(HttpStatusCode.SeeOther)
            }
        }
    }


        // [Week 9] Log success
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
                    "totalPages" to totalPages,
                    "flashMessage" to "Task \"${call.receiveParameters()["title"]?.trim()}\" added successfully."
                )
                
                val template = pebble.getTemplate("tasks/index.peb")
                val writer = StringWriter()
                template.evaluate(writer, model)
                call.respondText(writer.toString(), ContentType.Text.Html, HttpStatusCode.Created)
            } else {
                call.response.headers.append("Location", "/tasks")
                call.respond(HttpStatusCode.SeeOther)
            }
        }
    }

    /**
     * POST /tasks/{id}/delete - Delete task
     * Dual-mode: HTMX empty response or PRG redirect
     */
    post("/tasks/{id}/delete") {
        // [Week 9] Instrumentation setup
        val requestId = UUID.randomUUID().toString().substring(0, 8)
        val sessionId = call.request.queryParameters["sid"] ?: "anonymous"
        val isHtmx = call.isHtmx()

        // [Week 9] Timer wrapper
        val (removed, duration) = timed {
            val id = call.parameters["id"]?.toIntOrNull()
            id?.let { TaskRepository.delete(it) } ?: false
        }

        // [Week 9] Log outcome
        val outcome = if (removed) "success" else "not_found"
        Logger.log(sessionId, requestId, "T3_Delete", "persist", outcome, duration, 200, isHtmx)

        if (call.isHtmx()) {
            // Week 8 Update: Return list to keep pagination correct
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
            call.respondText(writer.toString(), ContentType.Text.Html)
        } else {
            // No-JS: POST-Redirect-GET pattern (303 See Other)
            call.response.headers.append("Location", "/tasks")
            call.respond(HttpStatusCode.SeeOther)
        }
    }

    // TODO: Week 7 Lab 1 Activity 2 Steps 2-5
    // Add inline edit routes here
    // Follow instructions in mdbook to implement:
    // - GET /tasks/{id}/edit - Show edit form (dual-mode)
    // - POST /tasks/{id}/edit - Save edits with validation (dual-mode)
    // - GET /tasks/{id}/view - Cancel edit (HTMX only)
    /**
     * GET /tasks/{id}/edit - Enter edit mode
     * HTMX: Returns just the list (with editingId set) to swap the UI
     * No-JS: Redirects to /tasks?editing={id} to reload page with edit form
     */
    get("/tasks/{id}/edit") {
        val id = call.parameters["id"]?.toIntOrNull()
        // [Week 9] Simple log for edit interaction (optional but good)
        Logger.log(
            call.request.queryParameters["sid"] ?: "anonymous", 
            UUID.randomUUID().toString().substring(0, 8), 
            "T2_Edit", "click_edit", "success", 0, 200, call.isHtmx()
        )

        if (id == null) {
            call.respond(HttpStatusCode.BadRequest)
            return@get
        }

        // [Week 8] Preserve Search/Page context
        val query = call.request.queryParameters["q"] ?: ""
        val page = call.request.queryParameters["page"]?.toIntOrNull() ?: 1
        val pageSize = 5
        val (tasks, totalCount) = TaskRepository.search(query, page, pageSize)
        val totalPages = if (totalCount > 0) (totalCount + pageSize - 1) / pageSize else 1

        if (call.isHtmx()) {
            // For HTMX, we re-render the list but with "editingId" set.
            // The template logic you added to index.peb will switch this row to a form.
            val model = mapOf(
                "tasks" to tasks,
                "editingId" to id,
                "q" to query,
                "page" to page,
                "totalCount" to totalCount,
                "totalPages" to totalPages
            )
            val template = pebble.getTemplate("tasks/index.peb")
            val writer = StringWriter()
            template.evaluate(writer, model)
            call.respondText(writer.toString(), ContentType.Text.Html)
        } else {
            // No-JS fallback: Redirect to main list with query param
            call.respondRedirect("/tasks?editing=$id&page=$page&q=$query")
        }
    }

    /**
     * POST /tasks/{id} - Save changes (Update)
     */
    post("/tasks/{id}") {
        val id = call.parameters["id"]?.toIntOrNull()
        val title = call.receiveParameters()["title"]?.trim()
        
        // [Week 9] Instrumentation
        val requestId = UUID.randomUUID().toString().substring(0, 8)
        val sessionId = call.request.queryParameters["sid"] ?: "anonymous"
        val isHtmx = call.isHtmx()

        val (updated, duration) = timed {
            if (id != null && !title.isNullOrBlank()) {
                TaskRepository.update(id, title)
                true
            } else false
        }
        
        Logger.log(sessionId, requestId, "T2_Edit", "persist_update", 
                   if (updated) "success" else "error", duration, 200, isHtmx)

        if (call.isHtmx()) {
            // [Week 8] Return to list view with context preserved
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
            call.respondText(writer.toString(), ContentType.Text.Html)
        } else {
            // No-JS: PRG pattern
            val query = call.request.queryParameters["q"] ?: ""
            val page = call.request.queryParameters["page"] ?: "1"
            call.respondRedirect("/tasks?page=$page&q=$query")
        }
    }

    /**
     * GET /tasks/{id} - Cancel edit / View task
     * Used by the "Cancel" button to exit edit mode
     */
    get("/tasks/{id}") {
        val query = call.request.queryParameters["q"] ?: ""
        val page = call.request.queryParameters["page"]?.toIntOrNull() ?: 1
        
        if (call.isHtmx()) {
            // Just return the list without editingId -> renders as read-only text
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
            call.respondText(writer.toString(), ContentType.Text.Html)
        } else {
            // No-JS: Just go back to the main list
            call.respondRedirect("/tasks?page=$page&q=$query")
        }
    }

}
