package com.turkcell.libraryapp.data.model

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class Book (
    val id: String = "",
    val title: String,
    val author: String,
    val isbn: String = "",
    val category: String,
    @SerialName("page_count") val pageCount: Int,
    @SerialName("total_copies") val totalCopies: Int = 1,
    @SerialName("available_copies") val avaiableCopies: Int = 1,
    @SerialName("image_url") val imageUrl: String? = null //Kitap kapağı
)

@Serializable
data class BorrowRequest (
    @SerialName("user_id") val userId: String,
    @SerialName("book_id") val bookId: String,
    @SerialName("borrow_date") val borrowDate: String,
    @SerialName("return_date") val returnDate: String
)

@Serializable
data class StockUpdate(
    @SerialName("available_copies") val avaiableCopies: Int
)

@Serializable
data class BorrowRecordResponse(
    @SerialName("book_id") val bookId: String,
    @SerialName("borrow_date") val borrowDate: String,
    @SerialName("return_date") val returnDate: String
)

data class BorrowedBookUiModel(
    val book: Book,
    val borrowDate: String,
    val returnDate: String
)