package works.ignition.certbotresource.out

import com.fasterxml.jackson.module.kotlin.jacksonObjectMapper
import works.ignition.certbotresource.storage.GCS

fun out(input: String) {
    val request = jacksonObjectMapper().readValue(input, Request::class.java)
    val storage = GCS(bucket = request.source.bucket, obj = request.source.versionedFile)
    println(
        jacksonObjectMapper()
            .writeValueAsString(
                process(storage, request)
            )
    )
}
