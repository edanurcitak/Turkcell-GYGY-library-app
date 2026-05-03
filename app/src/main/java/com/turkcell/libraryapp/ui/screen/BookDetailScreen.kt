package com.turkcell.libraryapp.ui.screen

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import coil.compose.AsyncImage
import com.turkcell.libraryapp.R
import com.turkcell.libraryapp.data.model.Book
import com.turkcell.libraryapp.ui.viewmodel.BookViewModel

@Composable
fun BookDetailScreen(
    book: Book,
    bookViewModel: BookViewModel,
    onNavigateToBorrows: () -> Unit
) {
    var showDatePickerDialog by remember { mutableStateOf(false) }
    var showSuccessDialog by remember { mutableStateOf(false) }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        AsyncImage(
            model = book.imageUrl,
            contentDescription = book.title,
            modifier = Modifier
                .fillMaxWidth()
                .height(350.dp)
                .padding(bottom = 16.dp),
            contentScale = ContentScale.Fit,
            error = painterResource(id = R.drawable.error_image)
        )

        Text(
            text = book.title,
            style = MaterialTheme.typography.headlineLarge,
            fontWeight = FontWeight.Bold,
            textAlign = TextAlign.Center,
            modifier = Modifier.fillMaxWidth()
        )

        Spacer(modifier = Modifier.height(16.dp))

        Column(
            modifier = Modifier.fillMaxWidth(),
            horizontalAlignment = Alignment.Start
        ) {
            Text(
                text = "Yazar: ${book.author}",
                style = MaterialTheme.typography.titleMedium,
                modifier = Modifier.padding(bottom = 8.dp)
            )

            Text(
                text = "Tür: ${book.category}",
                style = MaterialTheme.typography.bodyLarge,
                modifier = Modifier.padding(bottom = 8.dp)
            )

            Text(
                text = "Sayfa Sayısı: ${book.pageCount}",
                style = MaterialTheme.typography.bodyLarge
            )
        }

        Spacer(modifier = Modifier.height(24.dp))

        val hasStock = book.avaiableCopies > 0

        Button(
            onClick = {
                showDatePickerDialog = true // Takvimi aç
            },
            enabled = hasStock,
            modifier = Modifier
                .fillMaxWidth()
                .height(50.dp)
        ) {
            Text(
                text = if (hasStock) "ÖDÜNÇ AL" else "STOKTA YOK",
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold
            )
        }

        if (showDatePickerDialog) {
            BorrowBookDatePickerDialog(
                onDismiss = { showDatePickerDialog = false },
                onConfirm = { selectedDateMillis ->
                    showDatePickerDialog = false

                    bookViewModel.borrowBook(
                        bookId = book.id,
                        returnDateMillis = selectedDateMillis,
                        onSuccess = { showSuccessDialog = true }
                    )
                }
            )
        }

        if (showSuccessDialog) {
            AlertDialog(
                onDismissRequest = { showSuccessDialog = false },
                title = { Text("İşlem Başarılı! 🎉", fontWeight = FontWeight.Bold) },
                text = { Text("${book.title} başarıyla kiralandı. Keyifli okumalar!") },
                confirmButton = {
                    Button(onClick = {
                        showSuccessDialog = false
                        onNavigateToBorrows() // Kiralamalar sayfasına yönlendirir
                    }) {
                        Text("Kiralamalarım'a Git")
                    }
                },
                dismissButton = {
                    TextButton(onClick = { showSuccessDialog = false }) {
                        Text("Kapat")
                    }
                }
            )
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun BorrowBookDatePickerDialog(
    onDismiss: () -> Unit,
    onConfirm: (Long) -> Unit
) {
    val datePickerState = rememberDatePickerState(
        selectableDates = object : SelectableDates {
            override fun isSelectableDate(utcTimeMillis: Long): Boolean {
                return utcTimeMillis >= System.currentTimeMillis() - 86400000 // Dünü engeller
            }
        }
    )

    DatePickerDialog(
        onDismissRequest = onDismiss,
        confirmButton = {
            TextButton(
                onClick = { datePickerState.selectedDateMillis?.let { onConfirm(it) } },
                enabled = datePickerState.selectedDateMillis != null // Tarih seçilmeden onaylanamaz
            ) {
                Text("Onayla")
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) { Text("İptal") }
        }
    ) {
        DatePicker(state = datePickerState)
    }
}