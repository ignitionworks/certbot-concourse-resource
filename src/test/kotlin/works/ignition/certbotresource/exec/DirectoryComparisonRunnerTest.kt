package works.ignition.certbotresource.exec

import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Test
import java.io.File
import java.nio.file.Path
import kotlin.io.path.createTempDirectory

class DirectoryComparisonRunnerTest {
    private var runner: DirectoryComparisonRunner = DirectoryComparisonRunner()

    @Test
    internal fun `notices when command changes the watched dir`() {
        val dir: Path = createTempDirectory("dircomprunnertest")

        assertEquals(
            SuccessWithChange,
            runner.execute(
                ProcessBuilder("/usr/bin/touch", dir.resolve("hellothere").toString()).inheritIO(),
                dir
            )
        )
    }

    @Test
    internal fun `notices when command doesn't change the dir`() {
        assertEquals(
            SuccessWithoutChange,
            runner.execute(
                ProcessBuilder("/bin/true"),
                File("/tmp/doesntmatter").toPath()
            )
        )
    }

    @Test
    internal fun `failures are reported`() {
        assertEquals(
            ExecutionFailure,
            runner.execute(ProcessBuilder("/bin/false"), File("/tmp/doesntmatter").toPath())
        )
    }
}
