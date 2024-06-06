import org.abel.app.Book
import org.abel.app.ILibrary
import org.abel.app.Library
import org.abel.app.Member
import org.abel.errors.BookNotAvailableException
import org.abel.errors.BookNotFoundException
import org.abel.errors.BookNotInPossessionException
import org.abel.errors.MemberNotFoundException
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.assertThrows
import kotlin.test.Test

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

class LibraryCreationTests {
    private lateinit var library: ILibrary

    @BeforeEach
    fun setup() {
        library = Library()
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

class FilledLibraryTest {
    private lateinit var library: ILibrary
    private lateinit var m1: Member
    private lateinit var m2: Member
    private lateinit var b1: Book
    private lateinit var b2: Book


    @BeforeEach
    fun setup() {
        library = Library()
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
        assert(library.findMemberById(m1.id).borrowedBooks.contains(b1))
        assert(library.findMemberById(m2.id).borrowedBooks.size == 0)
        assert(library.findMemberById(m1.id).borrowedBooks.size == 1)
        assert(library.findBookById(b1.id).isAvailable.not())
        assert(library.findBookById(b2.id).isAvailable)

        library.borrowBook(m2.id, b2.id)
        assert(library.findMemberById(m2.id).borrowedBooks.contains(b2))
        assert(library.findMemberById(m1.id).borrowedBooks.size == 1)
        assert(library.findMemberById(m1.id).borrowedBooks.size == 1)
        assert(library.findBookById(b1.id).isAvailable.not())
        assert(library.findBookById(b2.id).isAvailable.not())

        library.returnBook(m1.id, b1.id)
        assert(library.findMemberById(m2.id).borrowedBooks.size == 1)
        assert(library.findMemberById(m1.id).borrowedBooks.size == 0)
        assert(library.findBookById(b1.id).isAvailable)
        assert(library.findBookById(b2.id).isAvailable.not())


        library.returnBook(m2.id, b2.id)
        assert(library.findMemberById(m2.id).borrowedBooks.size == 0)
        assert(library.findMemberById(m1.id).borrowedBooks.size == 0)
        assert(library.findBookById(b1.id).isAvailable)
        assert(library.findBookById(b2.id).isAvailable)

    }

    @Test
    fun bookNotAvailableExceptionTest() {
        library.borrowBook(m1.id, b1.id)
        library.borrowBook(m2.id, b2.id)

        assertThrows<BookNotAvailableException>() {
            library.borrowBook(m1.id, b1.id)
        }
        assert(library.findMemberById(m1.id).borrowedBooks.size == 1)
        assert(library.findMemberById(m2.id).borrowedBooks.size == 1)
        assert(library.findBookById(b1.id).isAvailable.not())
        assert(library.findBookById(b2.id).isAvailable.not())

        assertThrows<BookNotAvailableException>() {
            library.borrowBook(m2.id, b2.id)
        }
        assert(library.findMemberById(m1.id).borrowedBooks.size == 1)
        assert(library.findMemberById(m2.id).borrowedBooks.size == 1)
        assert(library.findBookById(b1.id).isAvailable.not())
        assert(library.findBookById(b2.id).isAvailable.not())

        assertThrows<BookNotAvailableException>() {
            library.borrowBook(m1.id, b2.id)
        }
        assert(library.findMemberById(m1.id).borrowedBooks.size == 1)
        assert(library.findMemberById(m2.id).borrowedBooks.size == 1)
        assert(library.findBookById(b1.id).isAvailable.not())
        assert(library.findBookById(b2.id).isAvailable.not())

        assertThrows<BookNotAvailableException>() {
            library.borrowBook(m2.id, b1.id)
        }
        assert(library.findMemberById(m1.id).borrowedBooks.size == 1)
        assert(library.findMemberById(m2.id).borrowedBooks.size == 1)
        assert(library.findBookById(b1.id).isAvailable.not())
        assert(library.findBookById(b2.id).isAvailable.not())
    }

    @Test
    fun bookNotInPossessionExceptionTest() {
        library.borrowBook(m1.id, b1.id)
        library.borrowBook(m2.id, b2.id)

        assertThrows<BookNotInPossessionException>() {
            library.returnBook(m1.id, b2.id)
        }

        assert(library.findMemberById(m1.id).borrowedBooks.size == 1)
        assert(library.findMemberById(m2.id).borrowedBooks.size == 1)
        assert(library.findBookById(b1.id).isAvailable.not())
        assert(library.findBookById(b2.id).isAvailable.not())

        assertThrows<BookNotInPossessionException>() {
            library.returnBook(m2.id, b1.id)
        }
        assert(library.findMemberById(m1.id).borrowedBooks.size == 1)
        assert(library.findMemberById(m2.id).borrowedBooks.size == 1)
        assert(library.findBookById(b1.id).isAvailable.not())
        assert(library.findBookById(b2.id).isAvailable.not())

        library.returnBook(m1.id, b1.id)

        assertThrows<BookNotInPossessionException>() {
            library.returnBook(m1.id, b1.id)
        }
        assert(library.findMemberById(m1.id).borrowedBooks.size == 0)
        assert(library.findMemberById(m2.id).borrowedBooks.size == 1)
        assert(library.findBookById(b1.id).isAvailable)
        assert(library.findBookById(b2.id).isAvailable.not())

        library.returnBook(m2.id, b2.id)
        assertThrows<BookNotInPossessionException>() {
            library.returnBook(m2.id, b2.id)
        }
        assert(library.findMemberById(m1.id).borrowedBooks.size == 0)
        assert(library.findMemberById(m2.id).borrowedBooks.size == 0)
        assert(library.findBookById(b1.id).isAvailable)
        assert(library.findBookById(b2.id).isAvailable)
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
