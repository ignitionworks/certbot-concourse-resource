package works.ignition.certbotresource.check

import com.fasterxml.jackson.module.kotlin.jacksonObjectMapper

fun main() {
    val request = jacksonObjectMapper().readValue(readln(), Request::class.java)
    println(
        jacksonObjectMapper()
            .writeValueAsString(
                process(request)
            )
    )
}
