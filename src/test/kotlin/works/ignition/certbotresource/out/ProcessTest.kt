package works.ignition.certbotresource.out

import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.Test
import works.ignition.certbotresource.Source
import works.ignition.certbotresource.Version
import works.ignition.certbotresource.compression.ShellOutCompressor
import works.ignition.certbotresource.storage.FakeStorage

internal class ProcessTest {
    @Test
    internal fun `missing blobs are created`() {
        val storage = FakeStorage()
        val compressor = ShellOutCompressor()
        val response = process(
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
    internal fun `when certbot fails, no new version is created`() {
        val storage = FakeStorage()
        val compressor = ShellOutCompressor()
        val response = process(
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
                    certbotConfigDir = "/tmp/please-not-my-real-system"
                )
            )
        )

        assertEquals(Failure, response)
        assert(storage.versions().isEmpty())
    }
}
