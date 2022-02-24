package works.ignition.certbotresource.out

import org.apache.commons.compress.archivers.tar.TarArchiveEntry
import org.apache.commons.compress.archivers.tar.TarArchiveInputStream
import org.apache.commons.compress.compressors.gzip.GzipCompressorInputStream
import org.apache.commons.compress.utils.IOUtils
import works.ignition.certbotresource.Metadatum
import works.ignition.certbotresource.Version
import java.io.*

fun decompress(inputStream: InputStream, out: File?) {
    TarArchiveInputStream(GzipCompressorInputStream(inputStream))
        .use { tar ->
            var entry: TarArchiveEntry?
            while (tar.nextTarEntry.also { entry = it } != null) {
                if (entry!!.isDirectory) {
                    continue
                }
                val currentFile = File(out, entry!!.name)
                val parent: File = currentFile.parentFile
                if (!parent.exists()) {
                    parent.mkdirs()
                }
                IOUtils.copy(tar, FileOutputStream(currentFile))
            }
        }
}

fun process(storage: works.ignition.certbotresource.storage.Storage, request: Request): Response {
    decompress(inputStream = ByteArrayInputStream(storage.read()), out = File(request.source.certbotConfigDir))

    val certbotProcess = Runtime.getRuntime().exec(
        arrayOf(
            "certbot",
            "certonly",
            "--non-interactive",
            "--dns-google",
            "--agree-tos",
            "--email=${request.source.email}",
            "--domains=${request.params.domains.joinToString(",")}"
        )
    )

    val exitCode = certbotProcess.waitFor()

    println("process exited: ${exitCode}")

    val stdout = certbotProcess.inputStream.bufferedReader().readText()
    println("stdout: ${stdout}")

    val stderr = certbotProcess.errorStream.bufferedReader().readText()
    println("stderr: ${stderr}")

    return Response(
        version = Version(123),
        metadata = listOf(
            Metadatum(renewedDomains = request.params.domains)
        )
    )
}
