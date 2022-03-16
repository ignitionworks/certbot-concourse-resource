package works.ignition.certbotresource.exec

import java.nio.file.Path

class ShellOutComparisonRunner : DirectoryComparisonRunner {
    override fun execute(builder: ProcessBuilder, directory: Path): Result {
        val before = directoryHashes(directory)
        val process = builder.start()

        return if (process.waitFor() == 0) {
            val after = directoryHashes(directory)

            if (before.contentEquals(after)) {
                SuccessWithoutChange
            } else {
                SuccessWithChange
            }
        } else {
            ExecutionFailure
        }
    }

    private fun directoryHashes(directory: Path): ByteArray? {
        val md5Process = ProcessBuilder(
            "find",
            directory.toString(),
            "-type",
            "f",
            "-exec",
            "md5sum",
            "{}",
            ";"
        ).start()
        val stdout = md5Process.inputStream
        return stdout.readAllBytes()
    }
}
