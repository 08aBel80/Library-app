import org.abel.cli.ConsoleLibrary
import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.BeforeAll
import org.junit.jupiter.params.ParameterizedTest
import org.junit.jupiter.params.provider.CsvFileSource

class ConsoleLibraryTests() {

    @ParameterizedTest
    @CsvFileSource(resources = ["/commands.csv"], numLinesToSkip = 1)
    fun testCommandsFromCsvFile(command: String, expectedResult: String) {
        val actualResult = library.executeCommand(command)
        assertTrue(
            actualResult.contains(expectedResult),
            "Failed for command: $command\nExpected: $expectedResult\nActual: $actualResult"
        )
    }

    companion object {
        private lateinit var library: ConsoleLibrary
        @JvmStatic
        @BeforeAll
        fun setup(): Unit {
            library = ConsoleLibrary()
        }
    }
}