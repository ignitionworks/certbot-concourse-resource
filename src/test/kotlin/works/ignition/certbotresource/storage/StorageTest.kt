package works.ignition.certbotresource.storage

import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import kotlin.text.Charsets.UTF_8

internal class GCSTest : StorageTest(GCS(bucket = "iw-letsencrypt", obj = "test-file"))
internal class FakeStorageTest : StorageTest(FakeStorage())

abstract class StorageTest(private val storage: Storage) {
    @BeforeEach
    internal fun setUp() {
        storage.delete()
    }

    @Test
    internal fun `can produce versions`() {
        val initialVersions = storage.versions()
        storage.store("first-version".toByteArray())
        assertEquals(initialVersions.size + 1, storage.versions().size)
        storage.store("second-version".toByteArray())
        assertEquals(initialVersions.size + 2, storage.versions().size)
    }

    @Test
    internal fun `can store and read an object`() {
        val firstVersion: String = storage.store("first-version".toByteArray(UTF_8))
        val secondVersion: String = storage.store("second-version".toByteArray(UTF_8))
        assertEquals(
            "first-version",
            storage.read(firstVersion)!!.toString(UTF_8)
        )
        assertEquals(
            "second-version",
            storage.read(secondVersion)!!.toString(UTF_8)
        )
    }

    @Test
    internal fun `non-existent objects are null`() {
        assertNull(storage.read("1"))
    }
}
