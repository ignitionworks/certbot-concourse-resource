package works.ignition.certbotresource.check

import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Test
import works.ignition.certbotresource.Source
import works.ignition.certbotresource.Version
import works.ignition.certbotresource.storage.FakeStorage

class ProcessTest {
    @Test
    internal fun `returns versions after and including the one provided`() {
        val storage = FakeStorage()
        storage.store("one".toByteArray())
        storage.store("two".toByteArray())
        storage.store("three".toByteArray())
        storage.store("four".toByteArray())

        val request = Request(
            source = Source(
                email = "me@example.com",
                bucket = "iw-justatest",
                versionedFile = "fake-obj",
                acmeServerURL = "https://not.called.in.this.test"
            ),
            version = Version("2")
        )

        assertEquals(
            listOf(
                Version("2"),
                Version("3"),
                Version("4"),
            ),
            process(storage, request)
        )
    }

    @Test
    internal fun `if no version provided, returns current version`() {
        val storage = FakeStorage()
        storage.store("one".toByteArray())
        storage.store("two".toByteArray())
        storage.store("three".toByteArray())
        storage.store("four".toByteArray())

        val request = Request(
            source = Source(
                email = "me@example.com",
                bucket = "iw-justatest",
                versionedFile = "fake-obj",
                acmeServerURL = "https://not.called.in.this.test"
            ),
            version = null
        )

        assertEquals(
            listOf(
                Version("4"),
            ),
            process(storage, request)
        )
    }
}
