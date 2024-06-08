import org.abel.cli.ConsoleLibrary
import org.abel.database.DatabaseManager
import org.abel.database.SqlLibraryStorage
import org.abel.library.LibraryStorage
import org.abel.simple.InMemoryLibraryStorage
import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.BeforeAll
import org.junit.jupiter.params.ParameterizedTest
import org.junit.jupiter.params.provider.CsvFileSource
import java.io.File

private const val testingDatabaseEnabled: Boolean = true // Set this based on configuration
private const val TEST_DATABASE_NAME = "build/testCLI"

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

        fun getTestingLibraryStorage(): LibraryStorage {
            return if (testingDatabaseEnabled) {
                val testDatabaseManager = DatabaseManager(TEST_DATABASE_NAME)
                SqlLibraryStorage(testDatabaseManager)
            } else {
                InMemoryLibraryStorage()
            }
        }

        fun deleteTestingDatabase() {
            if (testingDatabaseEnabled) {
                val databaseFile = File("$TEST_DATABASE_NAME.db")
                if (databaseFile.exists()) {
                    databaseFile.delete()
                }
            }
        }

        @JvmStatic
        @BeforeAll
        fun setup() {
            deleteTestingDatabase()
            library = ConsoleLibrary(getTestingLibraryStorage())
        }
    }
}