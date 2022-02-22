package works.ignition.certbotresource

import com.fasterxml.jackson.module.kotlin.jacksonObjectMapper
import works.ignition.certbotresource.out.Request

fun main() {
    val request = readln()
    jacksonObjectMapper().readValue(
        request,
        Request::class.java
    )
    println(request)
}
