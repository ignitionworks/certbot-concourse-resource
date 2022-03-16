package works.ignition.certbotresource.exec

import java.nio.file.Path

sealed class Result
object SuccessWithChange : Result()
object SuccessWithoutChange : Result()
object ExecutionFailure : Result()

interface DirectoryComparisonRunner {
    fun execute(builder: ProcessBuilder, directory: Path) : Result
}
