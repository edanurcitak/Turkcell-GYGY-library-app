package com.turkcell.libraryapp.ui.viewmodel

import android.icu.util.Calendar
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.turkcell.libraryapp.data.model.Book
import com.turkcell.libraryapp.data.model.BorrowRequest
import com.turkcell.libraryapp.data.model.StockUpdate
import com.turkcell.libraryapp.data.model.repository.BookRepository
import com.turkcell.libraryapp.data.supabase.supabase
import io.github.jan.supabase.SupabaseClient
import io.github.jan.supabase.auth.auth
import io.github.jan.supabase.postgrest.postgrest
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.Locale
import java.util.TimeZone

class BookViewModel: ViewModel () {
    private val repository = BookRepository()

    private val _books = MutableStateFlow<List<Book>>(emptyList())
    val books: StateFlow<List<Book>> = _books

    private val _isLoading = MutableStateFlow(false)
    val isLoading: StateFlow<Boolean> = _isLoading

    private val _error = MutableStateFlow<String?>(null)
    val error: StateFlow<String?> = _error

    init {
        loadBooks()
    }

    fun loadBooks() {
        viewModelScope.launch {
            _isLoading.value = true
            repository
                .getAllBooks()
                .onSuccess { _books.value = it }
                .onFailure { _error.value = it.message }
            _isLoading.value = false
        }
    }

    fun borrowBook(bookId: String, returnDateMillis: Long, onSuccess: () -> Unit) {
        viewModelScope.launch {
            try {
                val userId = supabase.auth.currentUserOrNull()?.id
                if (userId == null) {
                    println("Hata: Kullanıcı giriş yapmamış!")
                    return@launch
                }

                val dateFormat = java.text.SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss'Z'", java.util.Locale.getDefault())
                dateFormat.timeZone = java.util.TimeZone.getTimeZone("UTC")
                val returnDateStr = dateFormat.format(java.util.Date(returnDateMillis))

                val request = BorrowRequest(userId = userId, bookId = bookId, returnDate = returnDateStr)
                supabase.postgrest["borrow_records"].insert(request)

                val currentBook = _books.value.find { it.id == bookId }
                if (currentBook != null) {
                    val newStock = currentBook.avaiableCopies - 1
                    val stockUpdate = StockUpdate(avaiableCopies = newStock)

                    supabase.postgrest["books"].update(stockUpdate) {
                        filter { eq("id", bookId) }
                    }
                }

                loadBooks()

                onSuccess()
                println("BAŞARILI: Kitap kiralandı ve stok düşürüldü!")

            } catch (e: Exception) {
                println("Kiralama Hatası: ${e.message}")
            }
        }
    }
}