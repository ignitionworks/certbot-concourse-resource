package works.ignition.certbotresource.compression

import java.io.File
import java.io.InputStream
import java.nio.file.Path

class ShellOutCompressor : Compressor {
    override fun decompress(inputStream: InputStream, out: Path): DecompressionResult {
        val builder = ProcessBuilder("tar", "-x").directory(out.toFile())
            .redirectError(ProcessBuilder.Redirect.INHERIT)
            .redirectOutput(File("/dev/null"))
        val process = builder.start()
        val stdin = process.outputStream

        inputStream.transferTo(stdin)
        inputStream.close()
        stdin.close()

        return if (process.waitFor() == 0) {
            Success
        } else {
            Failure
        }
    }

    override fun compress(input: Path, output: Path) {
        val builder = ProcessBuilder("tar", "-cf", output.toString(), input.toString())
            .inheritIO()
        val process = builder.start()
        process.waitFor()
    }
}
