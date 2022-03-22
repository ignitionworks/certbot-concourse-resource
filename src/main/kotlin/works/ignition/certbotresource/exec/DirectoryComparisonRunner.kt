package works.ignition.certbotresource.exec

import java.nio.file.Path

sealed class Result
object SuccessWithChange : Result()
object SuccessWithoutChange : Result()
object ExecutionFailure : Result()

class DirectoryComparisonRunner {
    fun execute(builder: ProcessBuilder, directory: Path): Result =
        directoryHashes(directory).let { before ->
            if (builder.start().waitFor() == 0) {
                if (before.contentEquals(directoryHashes(directory))) {
                    SuccessWithoutChange
                } else {
                    SuccessWithChange
                }
            } else {
                ExecutionFailure
            }
        }

    private fun directoryHashes(directory: Path): ByteArray? =
        ProcessBuilder(
            "find",
            directory.toString(),
            "-type",
            "f",
            "-exec",
            "md5sum",
            "{}",
            ";"
        ).start().inputStream.readAllBytes()
}
