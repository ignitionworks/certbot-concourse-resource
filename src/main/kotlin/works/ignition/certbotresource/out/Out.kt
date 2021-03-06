package works.ignition.certbotresource.out

import com.fasterxml.jackson.module.kotlin.jacksonObjectMapper
import works.ignition.certbotresource.Version
import works.ignition.certbotresource.compression.Compressor
import works.ignition.certbotresource.compression.ShellOutCompressor
import works.ignition.certbotresource.exec.*
import works.ignition.certbotresource.storage.GCS
import works.ignition.certbotresource.storage.Storage
import java.io.ByteArrayInputStream
import java.io.File
import kotlin.io.path.Path
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
    storage: Storage,
    certbotProcessBuilder: ProcessBuilder,
    request: Request
): Response {
    storage.versions().lastOrNull()?.let { latestGeneration ->
        storage.read(latestGeneration)?.let { contents ->
            compressor.decompress(
                inputStream = ByteArrayInputStream(contents),
                out = File(request.source.certbotUnpackDir).toPath()
            )
        }
    }

    return when (DirectoryComparisonRunner().execute(
        certbotProcessBuilder,
        Path(request.source.certbotConfigDir)
    )) {
        ExecutionFailure ->
            Failure(
                version = storage.versions().lastOrNull()?.let(::Version),
                metadata = emptyList()
            )

        SuccessWithChange -> {
            Success(
                version = Version(store(compressor, request, storage)),
                metadata = listOf(
                    Metadata(name = "domains", value = request.params.domains.joinToString(", "))
                )
            )
        }

        SuccessWithoutChange -> {
            storage.versions().lastOrNull()
                ?.run(success(request))
                ?: store(compressor, request, storage)
                    .run(success(request))
        }
    }
}

private fun success(request: Request): String.() -> Success = {
    Success(
        version = Version(this),
        metadata = listOf(
            Metadata(name = "domains", value = request.params.domains.joinToString(", "))
        )
    )
}

private fun store(
    compressor: Compressor,
    request: Request,
    storage: Storage
): String {
    val tarball = File("/tmp/letsencrypt.tar").toPath()

    compressor.compress(
        input = File(request.source.certbotConfigDir).toPath(),
        output = tarball
    )

    return storage.store(tarball.readBytes())
}
