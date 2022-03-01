package works.ignition.certbotresource

import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.Test
import works.ignition.certbotresource.compression.ShellOutCompressor
import works.ignition.certbotresource.out.*
import works.ignition.certbotresource.storage.FakeStorage
import java.nio.file.Path
import kotlin.io.path.createTempDirectory

internal class OutTest {
    @Test
    internal fun `missing blobs are created`() {
        val storage = FakeStorage()
        val compressor = ShellOutCompressor()
        val response = out(
            compressor,
            storage,
            ProcessBuilder("/bin/true"),
            Request(
                params = Params(domains = listOf("some.domain")),
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
                metadata = listOf(Metadatum(renewedDomains = listOf("some.domain")))
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

        val unpackDir: Path = createTempDirectory(prefix = "outtest")

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
                    certbotConfigDir = unpackDir.toString()
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
