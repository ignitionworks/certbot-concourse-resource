package works.ignition.certbotresource.compression

import java.io.InputStream
import java.nio.file.Path

interface Compressor {
    fun decompress(inputStream: InputStream, out: Path) : DecompressionResult
    fun compress(input: Path, output: Path)
}
