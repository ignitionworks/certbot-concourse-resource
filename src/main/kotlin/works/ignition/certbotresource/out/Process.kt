package works.ignition.certbotresource.out

import works.ignition.certbotresource.Version
import works.ignition.certbotresource.compression.Compressor
import java.io.ByteArrayInputStream
import java.io.File
import kotlin.io.path.readBytes

fun process(
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
