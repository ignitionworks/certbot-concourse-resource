package works.ignition.certbotresource.compression

import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Test
import java.io.File
import java.io.FileInputStream
import java.nio.file.Path
import kotlin.io.path.createDirectory
import kotlin.io.path.createTempDirectory
import kotlin.io.path.readBytes

internal class ShellOutCompressorTest : CompressorTest(ShellOutCompressor())

abstract class CompressorTest(private val compressor: Compressor) {
    @Test
    internal fun `can round-trip a directory`() {
        val root: Path = createTempDirectory(prefix = "certbotcompressiontest")
        val input: Path = root.resolve("input").createDirectory()
        val outputFile = root.resolve("output").createDirectory().resolve("result.tar.gz")
        val decompressionOutput: File = root.resolve("decompressed").createDirectory().toFile()

        File(input.resolve("myfile").toUri()).writeText("somecontent")
        compressor.compress(input = input, output = outputFile)
        compressor.decompress(inputStream = FileInputStream(outputFile.toFile()), out = decompressionOutput.toPath())

        assertEquals(
            "somecontent",
            File(
                decompressionOutput.resolve(root.toString().drop(1)).resolve("input/myfile").toURI()
            ).readText(charset = Charsets.UTF_8)
        )
    }

    @Test
    internal fun `fails if the input isn't a tarball`() {
        val root: Path = createTempDirectory(prefix = "certbotcompressionnottarball")
        assertEquals(Failure, compressor.decompress("".byteInputStream(), root))
    }
}
