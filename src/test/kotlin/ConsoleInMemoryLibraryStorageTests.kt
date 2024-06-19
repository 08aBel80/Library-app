import org.abel.cli.ConsoleLibrary
import org.abel.database.DatabaseFactory
import org.abel.database.SqlLibraryStorage
import org.abel.library.LibraryStorage
import org.abel.simple.InMemoryLibraryStorage
import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.BeforeAll
import org.junit.jupiter.params.ParameterizedTest
import org.junit.jupiter.params.provider.CsvFileSource

private const val testingDatabaseEnabled: Boolean = true // Set this based on configuration


class ConsoleInMemoryLibraryStorageTests() {

    @ParameterizedTest
    @CsvFileSource(resources = ["/commands.csv"], numLinesToSkip = 1)
    fun testCommandsFromCsvFile(command: String, expectedResult: String) {
        val actualResult = library.executeCommand(command)
        assertTrue(
            actualResult.contains(expectedResult, ignoreCase = true),
            "Failed for command: $command\nExpected: $expectedResult\nActual: $actualResult"
        )
    }

    companion object {
        private lateinit var library: ConsoleLibrary

        private fun getTestingLibraryStorage(): LibraryStorage {
            return if (testingDatabaseEnabled) {
                val testDatabaseManager = DatabaseFactory.getTestCliDatabase()
                SqlLibraryStorage(testDatabaseManager)
            } else {
                InMemoryLibraryStorage()
            }
        }

        @JvmStatic
        @BeforeAll
        fun setup() {
            DatabaseFactory.clearTestCliDatabase()
            library = ConsoleLibrary(getTestingLibraryStorage())
        }
    }
}