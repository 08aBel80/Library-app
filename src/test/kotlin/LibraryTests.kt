import org.abel.database.DatabaseFactory
import org.abel.database.SqlLibraryStorage
import org.abel.errors.BookNotAvailableException
import org.abel.errors.BookNotFoundException
import org.abel.errors.BookNotInPossessionException
import org.abel.errors.MemberNotFoundException
import org.abel.library.Book
import org.abel.library.LibraryStorage
import org.abel.library.Member
import org.abel.simple.InMemoryLibraryStorage
import org.junit.jupiter.api.AfterEach
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows


private const val testingDatabaseEnabled: Boolean = true // Set this based on configuration

fun getTestingLibraryStorage(): LibraryStorage {
    return if (testingDatabaseEnabled) {
        val testDatabaseManager = DatabaseFactory.getTestDatabase()
        SqlLibraryStorage(testDatabaseManager)
    } else {
        InMemoryLibraryStorage()
    }
}

fun deleteTestingDatabase() {

    if (testingDatabaseEnabled) {
        DatabaseFactory.clearTestDatabase()
    }

}

class ModelsEqualTests {
    @Test
    fun booksEqualTest() {
        val b1 = Book(1, "Book 1", "Author 1")
        val b2 = Book(1, "Book 1", "Author 1")
        val b3 = Book(2, "Book 2", "Author 2")

        assert(b1 == b2)
        assert(b1 != b3)
    }

    @Test
    fun membersEqualTest() {
        val m1 = Member(1, "John Doe")
        val m2 = Member(1, "John Doe")
        val m3 = Member(2, "Jane Doe")

        assert(m1 == m2)
        assert(m1 != m3)
    }
}

class LibraryStorageCreationTests {
    private lateinit var library: LibraryStorage

    @BeforeEach
    fun setup() {
        library = getTestingLibraryStorage()
    }

    @AfterEach
    fun tearDown() {
        deleteTestingDatabase()
    }

    @Test
    fun addAndGetMembersTest() {
        library.addMember("John Doe")
        library.addMember("Jane Doe")

        val members = library.getMembers()
        assert(members.size == 2)
        val m1 = members.first()
        val m2 = members.last()

        if (m1.name == "John Doe") {
            assert(m2.name == "Jane Doe")
        } else if (m1.name == "Jane Doe") {
            assert(m2.name == "John Doe")
        }

        assert(library.findMemberById(m1.id) == m1)
        assert(library.findMemberById(m2.id) == m2)
    }

    @Test
    fun addAndGetBooksTest() {
        library.addBook("Book 1", "Author 1")
        library.addBook("Book 2", "Author 2")
        val books = library.getBooks()
        assert(books.size == 2)

        val b1 = books.first()
        val b2 = books.last()

        if (b1.title == "Book 1") {
            assert(b2.title == "Book 2")
            assert(b1.author == "Author 1")
            assert(b2.author == "Author 2")
        } else if (b1.title == "Book 2") {
            assert(b2.title == "Book 1")
            assert(b1.author == "Author 2")
            assert(b2.author == "Author 1")
        }

        assert(library.findBookById(b1.id) == b1)
        assert(library.findBookById(b2.id) == b2)
    }

    @Test
    fun emptyLibraryTests() {
        assert(library.getMembers().isEmpty())
        assert(library.getBooks().isEmpty())

        assertThrows<MemberNotFoundException> {
            library.findMemberById(1)
        }
        assertThrows<BookNotFoundException> {
            library.findBookById(1)
        }
    }
}

class FilledInMemoryLibraryStorageTest {
    private lateinit var library: LibraryStorage
    private lateinit var m1: Member
    private lateinit var m2: Member
    private lateinit var b1: Book
    private lateinit var b2: Book


    private lateinit var member1: Member
    private lateinit var member2: Member
    private lateinit var book1: Book
    private lateinit var book2: Book

    @BeforeEach
    fun setup() {
        library = getTestingLibraryStorage()
        library.addMember("John Doe")
        library.addMember("Jane Doe")
        library.addMember("Third Doe") // Added for testing purposes
        library.addBook("Book 1", "Author 1")
        library.addBook("Book 2", "Author 2")
        library.addBook("Book 3", "Author 3") // Added for testing purposes

        val members = library.getMembers()
        m1 = members.first()
        m2 = members.last()

        val books = library.getBooks()
        b1 = books.first()
        b2 = books.last()
    }

    @AfterEach
    fun tearDown() {
        deleteTestingDatabase()
    }

    private fun resetMembersAndBooks() {
        member1 = library.findMemberById(m1.id)
        member2 = library.findMemberById(m2.id)
        book1 = library.findBookById(b1.id)
        book2 = library.findBookById(b2.id)
    }

    @Test
    fun emptyMemberBooksTest() {
        assert(library.getMembers().size == 3)

        assert(library.findMemberById(m1.id).borrowedBooks.isEmpty())
        assert(library.findMemberById(m2.id).borrowedBooks.isEmpty())
    }

    @Test
    fun booksAvailableTest() {
        assert(library.getBooks().size == 3)

        assert(library.findBookById(b1.id).isAvailable)
        assert(library.findBookById(b2.id).isAvailable)
    }

    @Test
    fun borrowAndReturnBookTest() {
        library.borrowBook(m1.id, b1.id)
        resetMembersAndBooks()
        assert(member1.borrowedBooks.contains(book1)) { Pair(member1, book1) }
        assert(member2.borrowedBooks.size == 0) { member2 }
        assert(member1.borrowedBooks.size == 1) { member1 }
        assert(book1.isAvailable.not()) { book1 }
        assert(book2.isAvailable) { book2 }

        library.borrowBook(m2.id, b2.id)
        resetMembersAndBooks()
        assert(member2.borrowedBooks.contains(book2)) { Pair(member2, book2) }
        assert(member2.borrowedBooks.size == 1) { member2 }
        assert(member1.borrowedBooks.size == 1) { member1 }
        assert(book1.isAvailable.not()) { book1 }
        assert(book2.isAvailable.not()) { book2 }

        library.returnBook(m1.id, b1.id)
        resetMembersAndBooks()
        assert(member2.borrowedBooks.size == 1) { member2 }
        assert(member1.borrowedBooks.size == 0) { member1 }
        assert(book1.isAvailable) { book1 }
        assert(book2.isAvailable.not()) { book2 }

        library.returnBook(m2.id, b2.id)
        resetMembersAndBooks()
        assert(member1.borrowedBooks.size == 0) { member1 }
        assert(member2.borrowedBooks.size == 0) { member2 }
        assert(book1.isAvailable) { book1 }
        assert(book2.isAvailable) { book2 }
    }

    @Test
    fun bookNotAvailableExceptionTest() {
        library.borrowBook(m1.id, b1.id)
        library.borrowBook(m2.id, b2.id)
        assertThrows<BookNotAvailableException>() {
            library.borrowBook(m1.id, b1.id)
        }
        resetMembersAndBooks()
        assert(member1.borrowedBooks.size == 1) { member1 }
        assert(member2.borrowedBooks.size == 1) { member2 }
        assert(book1.isAvailable.not()) { book1 }
        assert(book2.isAvailable.not()) { book2 }

        assertThrows<BookNotAvailableException>() {
            library.borrowBook(m2.id, b2.id)
        }
        resetMembersAndBooks()
        assert(member1.borrowedBooks.size == 1) { member1 }
        assert(member2.borrowedBooks.size == 1) { member2 }
        assert(book1.isAvailable.not()) { book1 }
        assert(book2.isAvailable.not()) { book2 }

        assertThrows<BookNotAvailableException>() {
            library.borrowBook(m1.id, b2.id)
        }
        resetMembersAndBooks()
        assert(member1.borrowedBooks.size == 1) { member1 }
        assert(member2.borrowedBooks.size == 1) { member2 }
        assert(book1.isAvailable.not()) { book1 }
        assert(book2.isAvailable.not()) { book2 }

        assertThrows<BookNotAvailableException>() {
            library.borrowBook(m2.id, b1.id)
        }
        resetMembersAndBooks()
        assert(member1.borrowedBooks.size == 1) { member1 }
        assert(member2.borrowedBooks.size == 1) { member2 }
        assert(book1.isAvailable.not()) { book1 }
        assert(book2.isAvailable.not()) { book2 }
    }

    @Test
    fun bookNotInPossessionExceptionTest() {
        library.borrowBook(m1.id, b1.id)
        library.borrowBook(m2.id, b2.id)
        assertThrows<BookNotInPossessionException>() {
            library.returnBook(m1.id, b2.id)
        }
        resetMembersAndBooks()
        assert(member1.borrowedBooks.size == 1)
        assert(member2.borrowedBooks.size == 1)
        assert(book1.isAvailable.not())
        assert(book2.isAvailable.not())

        assertThrows<BookNotInPossessionException>() {
            library.returnBook(m2.id, b1.id)
        }
        resetMembersAndBooks()
        assert(member1.borrowedBooks.size == 1) { member1 }
        assert(member2.borrowedBooks.size == 1) { member2 }
        assert(book1.isAvailable.not()) { book1 }
        assert(book2.isAvailable.not()) { book2 }

        library.returnBook(m1.id, b1.id)
        assertThrows<BookNotInPossessionException>() {
            library.returnBook(m1.id, b1.id)
        }
        resetMembersAndBooks()
        assert(member1.borrowedBooks.size == 0) { member1 }
        assert(member2.borrowedBooks.size == 1) { member2 }
        assert(book1.isAvailable) { book1 }
        assert(book2.isAvailable.not()) { book2 }

        library.returnBook(m2.id, b2.id)
        assertThrows<BookNotInPossessionException>() {
            library.returnBook(m2.id, b2.id)
        }
        resetMembersAndBooks()
        assert(member1.borrowedBooks.size == 0) { member1 }
        assert(member2.borrowedBooks.size == 0) { member2 }
        assert(book1.isAvailable) { book1 }
        assert(book2.isAvailable) { book2 }
    }

    @Test
    fun memberNotFoundExceptionTest() {
        assertThrows<MemberNotFoundException>() {
            library.borrowBook(getNonExistingMemberId(), b1.id)
        }
        assertThrows<MemberNotFoundException>() {
            library.returnBook(getNonExistingMemberId(), b1.id)
        }
        assertThrows<MemberNotFoundException>() {
            library.findMemberById(getNonExistingMemberId())
        }
    }

    @Test
    fun bookNotFoundExceptionTest() {
        assertThrows<BookNotFoundException>() {
            library.borrowBook(m1.id, getNonExistingBookId())
        }
        assertThrows<BookNotFoundException>() {
            library.returnBook(m1.id, getNonExistingBookId())
        }
        assertThrows<BookNotFoundException>() {
            library.findBookById(getNonExistingBookId())
        }
    }

    private fun getNonExistingMemberId(): Int {
        val members = library.getMembers()
        var id = 1
        while (members.any { it.id == id }) {
            id++
        }
        return id
    }

    private fun getNonExistingBookId(): Int {
        val books = library.getBooks()
        var id = 1
        while (books.any { it.id == id }) {
            id++
        }
        return id
    }
}
