package works.ignition.certbotresource.storage

interface Storage {
    fun read(version: String?): ByteArray?
    fun store(bytes: ByteArray): String
    fun versions(): List<String>
    fun delete()
    fun isLatest(bytes: ByteArray): Boolean
}
