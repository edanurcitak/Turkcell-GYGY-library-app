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
    bookId: String,
    bookViewModel: BookViewModel,
    onNavigateToBorrows: () -> Unit
) {
    val book = bookViewModel.getBookById(bookId)

    if (book == null) {
        Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
            CircularProgressIndicator() // Yuvarlak yükleniyor animasyonu
        }
        return // Kodun aşağıya inmesini engelliyoruz
    }

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
            Text(text = "Yazar: ${book.author}", style = MaterialTheme.typography.titleMedium, modifier = Modifier.padding(bottom = 8.dp))
            Text(text = "Tür: ${book.category}", style = MaterialTheme.typography.bodyLarge, modifier = Modifier.padding(bottom = 8.dp))
            Text(text = "Sayfa Sayısı: ${book.pageCount}", style = MaterialTheme.typography.bodyLarge)
        }

        Spacer(modifier = Modifier.height(24.dp))

        val hasStock = book.avaiableCopies > 0

        Button(
            onClick = { showDatePickerDialog = true },
            enabled = hasStock,
            modifier = Modifier.fillMaxWidth().height(50.dp)
        ) {
            Text(
                text = if (hasStock) "ÖDÜNÇ AL" else "STOKTA YOK",
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold
            )
        }

        if (showDatePickerDialog) {
            BorrowBookDateRangePickerDialog(
                onDismiss = { showDatePickerDialog = false },
                onConfirm = { startDateMillis, endDateMillis ->
                    showDatePickerDialog = false

                    bookViewModel.borrowBook(
                        bookId = book.id,
                        currentStock = book.avaiableCopies,
                        startDateMillis = startDateMillis,
                        returnDateMillis = endDateMillis,
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
                        onNavigateToBorrows()
                    }) {
                        Text("Kiralamalarım'a Git")
                    }
                },
                dismissButton = {
                    TextButton(onClick = { showSuccessDialog = false }) { Text("Kapat") }
                }
            )
        }
    }
}


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun BorrowBookDateRangePickerDialog(
    onDismiss: () -> Unit,
    onConfirm: (Long, Long) -> Unit
) {
    val dateRangePickerState = rememberDateRangePickerState(
        selectableDates = object : SelectableDates {
            override fun isSelectableDate(utcTimeMillis: Long): Boolean {
                return utcTimeMillis >= System.currentTimeMillis() - 86400000 // Dünü engelle
            }
        }
    )

    val startDate = dateRangePickerState.selectedStartDateMillis
    val endDate = dateRangePickerState.selectedEndDateMillis

    // 5 GÜN KURALI
    var isButtonEnabled by remember { mutableStateOf(false) }
    var infoText by remember { mutableStateOf("Lütfen kiralama aralığı seçin") }

    LaunchedEffect(startDate, endDate) {
        if (startDate != null && endDate != null) {
            val diffMillis = endDate - startDate
            val days = diffMillis / (1000 * 60 * 60 * 24)

            when {
                days > 4 -> { // 0 dahil 5 gün için fark maksimum 4 olmalı
                    isButtonEnabled = false
                    infoText = "Maksimum 5 gün seçebilirsiniz."
                }
                days == 0L -> {
                    isButtonEnabled = false
                    infoText = "Kiralama en az 1 gün olmalıdır."
                }
                else -> {
                    isButtonEnabled = true
                    infoText = "${days + 1} günlük kiralama seçildi."
                }
            }
        } else {
            isButtonEnabled = false
            infoText = "Başlangıç ve Bitiş tarihi seçin"
        }
    }

    DatePickerDialog(
        onDismissRequest = onDismiss,
        confirmButton = {
            TextButton(
                onClick = {
                    if (startDate != null && endDate != null) {
                        onConfirm(startDate, endDate)
                    }
                },
                enabled = isButtonEnabled
            ) {
                Text("Onayla")
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) { Text("İptal") }
        }
    ) {
        Column {
            Text(
                text = infoText,
                color = if (!isButtonEnabled && startDate != null && endDate != null) MaterialTheme.colorScheme.error else MaterialTheme.colorScheme.primary,
                fontWeight = FontWeight.Bold,
                modifier = Modifier.padding(16.dp)
            )

            DateRangePicker(
                state = dateRangePickerState,
                modifier = Modifier.weight(1f),
                title = null,
                headline = null,
                showModeToggle = false
            )
        }
    }
}