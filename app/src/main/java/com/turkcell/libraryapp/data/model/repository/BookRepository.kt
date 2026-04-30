package com.turkcell.libraryapp.data.model.repository

import com.turkcell.libraryapp.data.model.Book
import com.turkcell.libraryapp.data.supabase.supabase
import io.github.jan.supabase.postgrest.postgrest

class BookRepository {

    suspend fun getAllBooks(): Result<List<Book>> = runCatching {
        supabase.postgrest["books"]
            .select()
            .decodeList<Book>()
    }

    suspend fun getBookById(id:String): Result<Book> = runCatching {
        supabase.postgrest["books"]
            .select { filter { eq("id",id) } }
            .decodeSingle<Book>()
    }

    suspend fun addBook(book: Book): Result<Unit> = runCatching {
        supabase.postgrest["books"].insert(book)
    }

    //Search fonksiyonu
    suspend fun searchBooks(query: String): Result<List<Book>> = runCatching {
        supabase.postgrest ["books"].select {
            filter {
                or {
                    ilike("title", "%$query%")
                    ilike("author", "%$query%")
                }
            }
        }.decodeList<Book>()
    }

    //Update fonksiyonu
    suspend fun updateBook(book: Book): Result<Unit> = runCatching {
        supabase.postgrest["books"].update(book) {
            filter {
                eq("id", book.id)
            }
        }
    }

    //Delete fonksiyonu
    suspend fun deleteBook(bookId: String): Result<Unit> = runCatching {
        supabase.postgrest["books"].delete {
            filter {
                eq("id", bookId)
            }
        }
    }

}