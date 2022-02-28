package works.ignition.certbotresource.out

import com.fasterxml.jackson.module.kotlin.jacksonObjectMapper
import works.ignition.certbotresource.Version
import works.ignition.certbotresource.compression.Compressor
import works.ignition.certbotresource.compression.ShellOutCompressor
import works.ignition.certbotresource.storage.GCS
import java.io.ByteArrayInputStream
import java.io.File
import kotlin.io.path.readBytes

fun main() {
    val request = jacksonObjectMapper().readValue(readln(), Request::class.java)

    println(
        jacksonObjectMapper()
            .writeValueAsString(
                out(
                    ShellOutCompressor(),
                    GCS(request.source.bucket, request.source.versionedFile),
                    ProcessBuilder(
                        "certbot",
                        "certonly",
                        "--non-interactive",
                        "--dns-google",
                        "--agree-tos",
                        "--email=${request.source.email}",
                        "--domains=${request.params.domains.joinToString(",")}"
                    )
                        .redirectOutput(ProcessBuilder.Redirect.appendTo(File("/dev/stderr")))
                        .redirectError(ProcessBuilder.Redirect.INHERIT),
                    request
                )
            )
    )
}

fun out(
    compressor: Compressor,
    storage: works.ignition.certbotresource.storage.Storage,
    certbotProcessBuilder: ProcessBuilder,
    request: Request
): Response {
    val letsencryptDir = File(request.source.certbotConfigDir).toPath()
    storage.versions().lastOrNull()?.let { latestVersion ->
        storage.read(latestVersion)?.let { contents ->
            compressor.decompress(
                inputStream = ByteArrayInputStream(contents),
                out = letsencryptDir
            )
        }
    }

    certbotProcessBuilder.start().waitFor().let { exitCode ->
        return if (exitCode == 0) {
            val tarball = File("/tmp/letsencrypt.tar.gz").toPath()

            compressor.compress(input = letsencryptDir, output = tarball)
            storage.store(tarball.readBytes())
            Success(
                version = Version(storage.versions().last()),
                metadata = listOf(
                    Metadatum(renewedDomains = request.params.domains)
                )
            )
        } else {
            Failure
        }
    }
}
