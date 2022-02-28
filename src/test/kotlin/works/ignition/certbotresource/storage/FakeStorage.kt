package works.ignition.certbotresource.storage

class FakeStorage(
    private var currentVersion: Int = 0,
    private var value: ByteArray? = null
) : Storage {
    override fun read(): ByteArray? = value

    override fun store(bytes: ByteArray) {
        value = bytes
        currentVersion += 1
    }

    override fun versions(): List<String> = (0..currentVersion).map(Int::toString)

    override fun delete() {
        currentVersion = 0
        value = null
    }
}
