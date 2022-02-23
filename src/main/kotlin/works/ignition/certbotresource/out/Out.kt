package works.ignition.certbotresource.out

import com.fasterxml.jackson.module.kotlin.jacksonObjectMapper

fun out(input: String) {
    println(
        jacksonObjectMapper()
            .writeValueAsString(
                process(
                    jacksonObjectMapper()
                        .readValue(input, Request::class.java)
                )
            )
    )
}
