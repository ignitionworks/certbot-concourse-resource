package works.ignition.certbotresource.storage

import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertNull
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import kotlin.text.Charsets.UTF_8

internal class GCSTest : StorageTest(GCS(bucket = "iw-letsencrypt", obj = "test-file"))
internal class FakeStorageTest: StorageTest(FakeStorage())

abstract class StorageTest(private val storage: Storage) {
    @BeforeEach
    internal fun setUp() {
        storage.delete()
    }

    @Test
    internal fun `can produce versions`() {
        val initialVersions = storage.versions()
        storage.store("some-content".byteInputStream().readAllBytes())
        assertEquals(initialVersions.size + 1, storage.versions().size)
        storage.store("some-other-content".byteInputStream().readAllBytes())
        assertEquals(initialVersions.size + 2, storage.versions().size)
    }

    @Test
    internal fun `can store and read an object`() {
        storage.store("some-content".toByteArray(UTF_8))
        assertEquals("some-content", storage.read()!!.toString(UTF_8))
    }

    @Test
    internal fun `non-existent objects are null`() {
        assertNull(storage.read())
    }
}
