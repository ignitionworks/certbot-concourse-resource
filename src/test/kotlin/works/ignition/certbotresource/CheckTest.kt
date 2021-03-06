package works.ignition.certbotresource

import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Test
import works.ignition.certbotresource.check.Request
import works.ignition.certbotresource.storage.FakeStorage

class CheckTest {
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
            works.ignition.certbotresource.check.check(storage, request)
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
            works.ignition.certbotresource.check.check(storage, request)
        )
    }

    @Test
    internal fun `returns empty list if there's no object in storage`() {
        val storage = FakeStorage()

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
            emptyList<Version>(),
            works.ignition.certbotresource.check.check(storage, request)
        )
    }
}
