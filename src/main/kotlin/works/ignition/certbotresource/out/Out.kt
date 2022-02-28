package works.ignition.certbotresource.out

import com.fasterxml.jackson.module.kotlin.jacksonObjectMapper
import works.ignition.certbotresource.compression.ShellOutCompressor
import works.ignition.certbotresource.storage.GCS
import java.io.File

fun main() {
    val request = jacksonObjectMapper().readValue(readln(), Request::class.java)
    val compressor = ShellOutCompressor()
    val storage = GCS(bucket = request.source.bucket, obj = request.source.versionedFile)
    val certbotProcessBuilder = ProcessBuilder(
        "certbot",
        "certonly",
        "--non-interactive",
        "--dns-google",
        "--agree-tos",
        "--email=${request.source.email}",
        "--domains=${request.params.domains.joinToString(",")}"
    )
    certbotProcessBuilder.redirectOutput(ProcessBuilder.Redirect.appendTo(File("/dev/stderr")))
    certbotProcessBuilder.redirectError(ProcessBuilder.Redirect.INHERIT)

    println(
        jacksonObjectMapper()
            .writeValueAsString(
                process(compressor, storage, certbotProcessBuilder, request)
            )
    )
}
