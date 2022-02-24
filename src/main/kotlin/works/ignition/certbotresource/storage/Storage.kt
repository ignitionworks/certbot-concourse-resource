package works.ignition.certbotresource.storage

interface Storage {
    fun read(): ByteArray
    fun store(bytes: ByteArray)
    fun versions(): List<String>
}
