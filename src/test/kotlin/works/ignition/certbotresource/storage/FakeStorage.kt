package works.ignition.certbotresource.storage

class FakeStorage(
    private var versions: MutableList<Pair<Int, ByteArray>> = emptyList<Pair<Int, ByteArray>>().toMutableList(),
) : Storage {
    override fun read(version: String?): ByteArray? =
        versions.find { v -> v.first.toString() == version }
        ?.second

    override fun store(bytes: ByteArray): String =
        Pair(versions.lastOrNull()
            ?.let { it.first + 1 } ?: 1, bytes)
            .also { versions.add(it) }
            .first.toString()

    override fun versions(): List<String> = versions.map { it.first.toString() }

    override fun delete() {
        versions.clear()
    }
}
