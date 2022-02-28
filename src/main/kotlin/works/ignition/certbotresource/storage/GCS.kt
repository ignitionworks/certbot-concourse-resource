package works.ignition.certbotresource.storage

import com.google.cloud.storage.*
import com.google.cloud.storage.Storage.BlobListOption

class GCS(val bucket: String, val obj: String) : Storage {
    private val gcs: com.google.cloud.storage.Storage =
        StorageOptions.getDefaultInstance().service

    override fun read(version: String): ByteArray? = blobAt(version)?.getContent()
    override fun store(bytes: ByteArray): String = gcs.create(blobInfo(), bytes).generation.toString()

    override fun versions(): List<String> =
        bucket().list(BlobListOption.versions(true)).iterateAll()
            .filter { it.name == obj }
            .map { it.generation.toString() }

    override fun delete() {
        blob()?.delete()
    }

    private fun bucket(): Bucket = gcs.get(bucket)
    private fun blobInfo(): BlobInfo = BlobInfo.newBuilder(blobId()).build()
    private fun blobId(): BlobId = BlobId.of(bucket, obj)
    private fun blobId(generation: Long): BlobId = BlobId.of(bucket, obj, generation)
    private fun blob(): Blob? = gcs.get(blobId())
    private fun blobAt(version: String): Blob? = gcs.get(blobId(version.toLong()))
}
