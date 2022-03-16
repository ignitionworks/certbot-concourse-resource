package works.ignition.certbotresource.out

import com.fasterxml.jackson.module.kotlin.jacksonObjectMapper
import works.ignition.certbotresource.Version
import works.ignition.certbotresource.compression.Compressor
import works.ignition.certbotresource.compression.ShellOutCompressor
import works.ignition.certbotresource.storage.GCS
import java.io.ByteArrayInputStream
import java.io.File
import kotlin.io.path.readBytes
import kotlin.system.exitProcess

fun main() {
    val request = jacksonObjectMapper().readValue(readln(), Request::class.java)
    val response = out(
        ShellOutCompressor(),
        GCS(request.source.bucket, request.source.versionedFile),
        ProcessBuilder(
            "certbot",
            "certonly",
            "--non-interactive",
            "--expand",
            "--dns-google",
            "--agree-tos",
            "--email=${request.source.email}",
            "--domains=${request.params.domains.joinToString(",")}"
        )
            .redirectOutput(ProcessBuilder.Redirect.appendTo(File("/dev/stderr")))
            .redirectError(ProcessBuilder.Redirect.INHERIT),
        request
    )
    println(jacksonObjectMapper().writeValueAsString(response))
    when (response) {
        is Success -> exitProcess(0)
        is Failure -> exitProcess(1)
    }
}

fun out(
    compressor: Compressor,
    storage: works.ignition.certbotresource.storage.Storage,
    certbotProcessBuilder: ProcessBuilder,
    request: Request
): Response {
    storage.versions().lastOrNull()?.let { latestVersion ->
        storage.read(latestVersion)?.let { contents ->
            compressor.decompress(
                inputStream = ByteArrayInputStream(contents),
                out = File(request.source.certbotUnpackDir).toPath()
            )
        }
    }

    certbotProcessBuilder.start().waitFor().let { exitCode ->
        return if (exitCode == 0) {
            val tarball = File("/tmp/letsencrypt.tar.gz").toPath()

            compressor.compress(
                input = File(request.source.certbotConfigDir).toPath(),
                output = tarball
            )
            storage.store(tarball.readBytes())
            Success(
                version = Version(storage.versions().last()),
                metadata = listOf(
                    Metadata(name = "domains", value = request.params.domains)
                )
            )
        } else {
            Failure(
                version = storage.versions().lastOrNull()?.let(::Version),
                metadata = emptyList()
            )
        }
    }
}
