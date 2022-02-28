package works.ignition.certbotresource.storage

import com.google.cloud.storage.*
import com.google.cloud.storage.Storage.BlobListOption

class GCS(val bucket: String, val obj: String) : Storage {
    private val gcs: com.google.cloud.storage.Storage =
        StorageOptions.getDefaultInstance().service

    override fun read(): ByteArray? = blob()?.getContent()

    override fun store(bytes: ByteArray) {
        gcs.create(blobInfo(), bytes)
    }

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
    private fun blob(): Blob? = gcs.get(blobId())
}
