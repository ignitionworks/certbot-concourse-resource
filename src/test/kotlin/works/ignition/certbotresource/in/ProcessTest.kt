package works.ignition.certbotresource.`in`

import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Test
import works.ignition.certbotresource.Source
import works.ignition.certbotresource.Version
import works.ignition.certbotresource.compression.ShellOutCompressor
import works.ignition.certbotresource.storage.FakeStorage
import java.io.File
import java.nio.file.Path
import kotlin.io.path.createDirectory
import kotlin.io.path.createTempDirectory
import kotlin.io.path.readText

internal class ProcessTest {
    @Test
    internal fun `unpacks the tarball inside the provided dir`() {
        val compressor = ShellOutCompressor()
        val storage = FakeStorage()

        val fixtureRoot: Path = createTempDirectory(prefix = "certbotinprocessfixtures")
        val destDir: Path = createTempDirectory(prefix = "certbotinprocesstest")
        fixtureRoot.resolve("input").createDirectory()
        val fixtureInputFile = fixtureRoot.resolve("input/myfile")
        File(fixtureInputFile.toUri()).writeText("mytestcontent")
        val fixtureTarball = fixtureRoot.resolve("fixture.tar.gz")

        compressor.compress(
            input = fixtureRoot.resolve("input"),
            output = fixtureTarball
        )
        storage.store(fixtureTarball.toFile().readBytes())

        val request = Request(
            source = Source(
                email = "me@example.com",
                bucket = "iw-justatest",
                versionedFile = "fake-obj",
                acmeServerURL = "https://not.called.in.this.test"
            ),
            version = Version("1")
        )

        assertEquals(
            Success(
                version = Version("1"),
            ),
            process(compressor, storage, destDir, request)
        )

        assertEquals(
            "mytestcontent",
            destDir.resolve(fixtureRoot.toString().drop(1)).resolve("input/myfile").readText()
        )
    }

    @Test
    internal fun `fails if the object isn't in storage`() {
        val compressor = ShellOutCompressor()
        val storage = FakeStorage()
        val destDir: Path = File("/tmp/aint-gonna-need-it").toPath()

        val request = Request(
            source = Source(
                email = "me@example.com",
                bucket = "iw-justatest",
                versionedFile = "fake-obj",
                acmeServerURL = "https://not.called.in.this.test"
            ),
            version = Version("1")
        )

        assertEquals(
            Failure("Couldn't find object 'fake-obj' in storage"),
            process(compressor, storage, destDir, request)
        )
    }
}
