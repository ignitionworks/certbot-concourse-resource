package works.ignition.certbotresource.`in`

import com.fasterxml.jackson.module.kotlin.jacksonObjectMapper
import works.ignition.certbotresource.compression.ShellOutCompressor
import works.ignition.certbotresource.storage.GCS
import java.io.File

fun main(args: Array<String>) {
    val request = jacksonObjectMapper().readValue(readln(), Request::class.java)
    val workingDir = args[0]

    println(
        jacksonObjectMapper()
            .writeValueAsString(
                process(
                    ShellOutCompressor(),
                    GCS(bucket = request.source.bucket, obj = request.source.versionedFile),
                    File(workingDir).toPath(),
                    request
                )
            )
    )

}
