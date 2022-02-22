package works.ignition.certbotresource

import com.fasterxml.jackson.module.kotlin.jacksonObjectMapper
import works.ignition.certbotresource.out.Request
import works.ignition.certbotresource.out.Response

fun main() =
    jacksonObjectMapper().readValue(
        readln(),
        Request::class.java
    ).let { request ->
        println(jacksonObjectMapper().writeValueAsString(process(request)))
    }

fun process(request: Request): Response =
    Response(
        version = Version(123),
        metadata = listOf(
            Metadatum(renewedDomains = request.params.domains)
        )
    )
