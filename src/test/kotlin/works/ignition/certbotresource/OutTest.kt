package works.ignition.certbotresource

import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.Test
import works.ignition.certbotresource.compression.ShellOutCompressor
import works.ignition.certbotresource.out.*
import works.ignition.certbotresource.storage.FakeStorage
import java.nio.file.Path
import kotlin.io.path.createTempDirectory
import kotlin.io.path.readBytes

internal class OutTest {
    @Test
    internal fun `no new version is created when certbot succeeds without change`() {
        val storage = FakeStorage()
        val compressor = ShellOutCompressor()

        val dir: Path = createTempDirectory("outtest")
        val tarball: Path = dir.resolve("out.tar.gz")
        compressor.compress(dir.resolve("in"), tarball)
        storage.store(tarball.readBytes())

        val response = out(
            compressor,
            storage,
            ProcessBuilder("/bin/true"),
            Request(
                params = Params(domains = listOf("some.domain", "some.other.domain")),
                source = Source(
                    email = "email@example.com",
                    bucket = "iw-letsencrypt",
                    versionedFile = "some-missing-file",
                    acmeServerURL = "https://acme-staging-v02.api.letsencrypt.org/directory",
                    certbotConfigDir = dir.resolve("in").toString()
                )
            )
        )

        assertEquals(
            Success(
                version = Version("1"),
                metadata = listOf(Metadata(name = "domains", value = "some.domain, some.other.domain"))
            ),
            response
        )
        assertEquals(listOf("1"), storage.versions())
    }

    @Test
    internal fun `missing blobs are created`() {
        val storage = FakeStorage()
        val compressor = ShellOutCompressor()
        val response = out(
            compressor,
            storage,
            ProcessBuilder("/bin/true"),
            Request(
                params = Params(domains = listOf("some.domain", "some.other.domain")),
                source = Source(
                    email = "email@example.com",
                    bucket = "iw-letsencrypt",
                    versionedFile = "some-missing-file",
                    acmeServerURL = "https://acme-staging-v02.api.letsencrypt.org/directory",
                    certbotConfigDir = "/tmp/please-not-my-real-system"
                )
            )
        )

        assertEquals(
            Success(
                version = Version("1"),
                metadata = listOf(Metadata(name = "domains", value = "some.domain, some.other.domain"))
            ),
            response
        )
        assertEquals(listOf("1"), storage.versions())
    }

    @Test
    internal fun `when certbot fails, previous version is emitted`() {
        val storage = FakeStorage()
        val compressor = ShellOutCompressor()

        out(
            compressor,
            storage,
            ProcessBuilder("/bin/true"),
            Request(
                params = Params(domains = listOf("first.domain")),
                source = Source(
                    email = "email@example.com",
                    bucket = "iw-letsencrypt",
                    versionedFile = "some-missing-file",
                    acmeServerURL = "https://acme-staging-v02.api.letsencrypt.org/directory",
                    certbotConfigDir = "/tmp/please-not-my-real-system"
                )
            )
        )

        val certbotDir: Path = createTempDirectory(prefix = "outtest")

        val response = out(
            compressor,
            storage,
            ProcessBuilder("/bin/false"),
            Request(
                params = Params(domains = listOf("some.domain")),
                source = Source(
                    email = "email@example.com",
                    bucket = "iw-letsencrypt",
                    versionedFile = "some-missing-file",
                    acmeServerURL = "https://acme-staging-v02.api.letsencrypt.org/directory",
                    certbotConfigDir = certbotDir.toString()
                )
            )
        )

        assertEquals(Failure(
            version = Version("1"),
            metadata = emptyList()
        ), response)

        assertEquals(listOf("1"), storage.versions())
    }

    @Test
    internal fun `when no previous version, failures contain a null version`() {
        val storage = FakeStorage()
        val compressor = ShellOutCompressor()

        val response = out(
            compressor,
            storage,
            ProcessBuilder("/bin/false"),
            Request(
                params = Params(domains = listOf("some.domain")),
                source = Source(
                    email = "email@example.com",
                    bucket = "iw-letsencrypt",
                    versionedFile = "some-missing-file",
                    acmeServerURL = "https://acme-staging-v02.api.letsencrypt.org/directory",
                    certbotConfigDir = "/tmp/please-nooo"
                )
            )
        )

        assertEquals(Failure(
            version = null,
            metadata = emptyList()
        ), response)

        assert(storage.versions().isEmpty())
    }
}
