package org.abel.library


data class Book(
    val id: Int,
    val title: String,
    val author: String,
    var isAvailable: Boolean = true
) {
    @Override
    override fun toString(): String {
        return "Book(id=$id, title='$title', author='$author', isAvailable=$isAvailable)"
    }
}

data class Member(
    val id: Int,
    val name: String,
    val borrowedBooks: MutableList<Book> = mutableListOf()
) {

    override fun toString(): String {
        return "Member(id=$id, name='$name', books=$borrowedBooks)"
    }

}

