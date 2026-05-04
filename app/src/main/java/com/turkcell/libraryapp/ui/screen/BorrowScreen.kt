package com.turkcell.libraryapp.ui.screen

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import coil.compose.AsyncImage
import com.turkcell.libraryapp.R
import com.turkcell.libraryapp.data.model.BorrowedBookUiModel
import com.turkcell.libraryapp.ui.viewmodel.BookViewModel

@Composable
fun BorrowScreen(bookViewModel: BookViewModel) {
    val borrowedBooks by bookViewModel.userBorrows.collectAsState()

    LaunchedEffect(Unit) {
        bookViewModel.loadUserBorrows()
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
    ) {
        Text(
            text = "Ödünç Aldıklarım",
            style = MaterialTheme.typography.headlineMedium,
            fontWeight = FontWeight.Bold,
            modifier = Modifier.padding(bottom = 16.dp)
        )

        if (borrowedBooks.isEmpty()) {
            Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                Text(
                    text = "Henüz hiç kitap ödünç almadınız.",
                    style = MaterialTheme.typography.bodyLarge
                )
            }
        } else {
            LazyColumn(
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                items(borrowedBooks) { item ->
                    BorrowedBookCard(item = item)
                }
            }
        }
    }
}


@Composable
fun BorrowedBookCard(item: BorrowedBookUiModel) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
    ) {
        Row(
            modifier = Modifier.padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            AsyncImage(
                model = item.book.imageUrl,
                contentDescription = item.book.title,
                modifier = Modifier.size(80.dp, 120.dp),
                contentScale = ContentScale.Crop,
                error = painterResource(id = R.drawable.error_image)
            )

            Spacer(modifier = Modifier.width(16.dp))

            Column {
                Text(
                    text = item.book.title,
                    fontWeight = FontWeight.Bold,
                    style = MaterialTheme.typography.titleMedium
                )
                Text(
                    text = item.book.author,
                    style = MaterialTheme.typography.bodyMedium,
                    modifier = Modifier.padding(bottom = 8.dp)
                )

                Text(
                    text = "Alış: ${item.borrowDate.take(10)}",
                    style = MaterialTheme.typography.bodySmall
                )
                Text(
                    text = "Teslim: ${item.returnDate.take(10)}",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.primary,
                    fontWeight = FontWeight.Bold
                )
            }
        }
    }
}