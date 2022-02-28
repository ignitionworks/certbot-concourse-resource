package works.ignition.certbotresource.compression

import java.io.InputStream
import java.nio.file.Path

class ShellOutCompressor : Compressor {
    override fun decompress(inputStream: InputStream, out: Path): DecompressionResult {
        val builder = ProcessBuilder("tar", "-x").directory(out.toFile())
        val process = builder.start()
        val stdin = process.outputStream
        stdin.write(inputStream.readBytes())
        stdin.close()

        if (process.waitFor() == 0) {
            return Success
        } else {
            return Failure
        }
    }

    override fun compress(input: Path, output: Path) {
        val builder = ProcessBuilder("tar", "-cf", output.toString(), input.toString())
        val process = builder.start()
        process.waitFor()
    }
}
