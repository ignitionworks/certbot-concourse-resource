package works.ignition.certbotresource.`in`

import com.fasterxml.jackson.module.kotlin.jacksonObjectMapper
import works.ignition.certbotresource.compression.Compressor
import works.ignition.certbotresource.compression.ShellOutCompressor
import works.ignition.certbotresource.storage.GCS
import works.ignition.certbotresource.storage.Storage
import java.io.File
import java.nio.file.Path

fun main(args: Array<String>) {
    val request = jacksonObjectMapper().readValue(readln(), Request::class.java)
    val workingDir = args[0]

    println(
        jacksonObjectMapper()
            .writeValueAsString(
                `in`(
                    ShellOutCompressor(),
                    GCS(bucket = request.source.bucket, obj = request.source.versionedFile),
                    File(workingDir).toPath(),
                    request
                )
            )
    )

}

fun `in`(compressor: Compressor, storage: Storage, destDir: Path, request: Request): Response =
    storage.read(request.version.generation)?.inputStream()
        ?.let { stream ->
            compressor.decompress(inputStream = stream, out = destDir)
            return Success(
                version = request.version,
            )
        }
        ?: Failure("Couldn't find object '${request.source.versionedFile}' in storage")
