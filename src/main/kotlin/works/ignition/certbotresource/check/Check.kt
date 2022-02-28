package works.ignition.certbotresource.check

import com.fasterxml.jackson.module.kotlin.jacksonObjectMapper
import works.ignition.certbotresource.storage.GCS

fun main() {
    val request = jacksonObjectMapper().readValue(readln(), Request::class.java)
    val storage = GCS(bucket = request.source.bucket, obj = request.source.versionedFile)
    println(
        jacksonObjectMapper()
            .writeValueAsString(
                process(storage, request)
            )
    )
}
