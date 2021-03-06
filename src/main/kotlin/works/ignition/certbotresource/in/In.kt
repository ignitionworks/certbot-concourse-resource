package works.ignition.certbotresource.`in`

import com.fasterxml.jackson.module.kotlin.jacksonObjectMapper
import works.ignition.certbotresource.compression.Compressor
import works.ignition.certbotresource.compression.ShellOutCompressor
import works.ignition.certbotresource.storage.GCS
import works.ignition.certbotresource.storage.Storage
import java.io.File
import java.nio.file.Path
import kotlin.system.exitProcess

fun main(args: Array<String>) {
    val request = jacksonObjectMapper().readValue(readln(), Request::class.java)
    val workingDir = args[0]
    val response = `in`(
        ShellOutCompressor(),
        GCS(bucket = request.source.bucket, obj = request.source.versionedFile),
        File(workingDir).toPath(),
        request
    )
    println(jacksonObjectMapper().writeValueAsString(response))
    when (response) {
        is Success -> exitProcess(0)
        is Failure -> {
            System.err.println("Failed: ${response.reason}")
            exitProcess(1)
        }
    }
}

fun `in`(compressor: Compressor, storage: Storage, destDir: Path, request: Request): Response =
    storage.read(request.version.generation)?.inputStream()
        ?.let { stream ->
            when (compressor.decompress(inputStream = stream, out = destDir)) {
                works.ignition.certbotresource.compression.Failure -> Failure(
                    "Couldn't decompress '${request.source.versionedFile}'"
                )
                works.ignition.certbotresource.compression.Success -> Success(
                    version = request.version,
                )
            }
        }
        ?: Failure("Couldn't find object '${request.source.versionedFile}' in storage")
