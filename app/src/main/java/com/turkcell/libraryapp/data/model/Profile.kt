package com.turkcell.libraryapp.data.model

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class Profile(
    @SerialName("user_id") val userId: String,
    val role: String,
    @SerialName("full_name") val fullName: String,
    @SerialName("student_no") val studentNo: String? = null
)