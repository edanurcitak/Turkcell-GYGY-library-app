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
)