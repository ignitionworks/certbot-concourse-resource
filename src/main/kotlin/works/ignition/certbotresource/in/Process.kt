package works.ignition.certbotresource.`in`

import works.ignition.certbotresource.compression.Compressor
import works.ignition.certbotresource.storage.Storage
import java.nio.file.Path

fun process(compressor: Compressor, storage: Storage, destDir: Path, request: Request): Response =
    storage.read()?.inputStream()
        ?.let { stream ->
            compressor.decompress(inputStream = stream, out = destDir)
            return Success(
                version = request.version,
            )
        }
        ?: Failure("Couldn't find object '${request.source.versionedFile}' in storage")
