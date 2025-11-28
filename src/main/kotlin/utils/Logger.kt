package utils

import java.io.File
import java.time.Instant
import java.time.format.DateTimeFormatter

object Logger {
    private val file = File("data/metrics.csv")

    init {
        if (!file.exists()) {
            file.parentFile?.mkdirs()
            file.writeText("ts_iso,session_id,request_id,task_code,step,outcome,ms,http_status,js_mode\n")
        }
    }

    
    fun log(
        sessionId: String,
        requestId: String,
        taskCode: String,
        step: String,
        outcome: String,
        ms: Long,
        status: Int,
        isHtmx: Boolean
    ) {
        val ts = DateTimeFormatter.ISO_INSTANT.format(Instant.now())
        val jsMode = if (isHtmx) "on" else "off"
        
        val line = "$ts,$sessionId,$requestId,$taskCode,$step,$outcome,$ms,$status,$jsMode\n"
        
        file.appendText(line)
        println("Logged: $taskCode - $outcome (${ms}ms) [SID: $sessionId]") 
    }
}
