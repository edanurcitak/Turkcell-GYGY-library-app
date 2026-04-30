package com.turkcell.libraryapp.ui.screen

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.turkcell.libraryapp.ui.viewmodel.AuthState
import com.turkcell.libraryapp.ui.viewmodel.AuthViewModel

@Composable
fun RegisterScreen(
    onNavigateToLogin: () -> Unit,
    authViewModel: AuthViewModel
) {
    val authState by authViewModel.authState.collectAsState()
    var email by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    var fullName by remember { mutableStateOf("") }
    var studentNo by remember { mutableStateOf("") }

    //Pop-up ve girdi kontrolleri
    var showDialog by remember { mutableStateOf(false) }
    var dialogMessage by remember { mutableStateOf("") }
    var fullNameError by remember { mutableStateOf<String?>(null) }
    var emailError by remember { mutableStateOf<String?>(null) }
    var passwordError by remember { mutableStateOf<String?>(null) }

    // Supabase'den gelen durumları dinle
    LaunchedEffect(authState) {
        when (authState) {
            is AuthState.Error -> {
                // Hata varsa pop-up göster
                dialogMessage = (authState as AuthState.Error).message
                showDialog = true
            }

            is AuthState.Success -> {
                onNavigateToLogin()
            }

            else -> {
                // Loading veya Idle durumlarında bir şey yapma
            }
        }
    }

    //Pop-up kodu
    if (showDialog) {
        AlertDialog(
            onDismissRequest = {showDialog = false},
            title = { Text("Uyarı")},
            text = { Text(dialogMessage)},
            confirmButton = {
                TextButton(onClick = {showDialog = false}) {
                    Text("Tamam")
                }
            }
        )
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(24.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Text(
            text = "Kayıt Ol",
            fontSize = 26.sp,
            fontWeight = FontWeight.Bold
        )
        Spacer(modifier = Modifier.height(32.dp))

        OutlinedTextField(
            value = fullName,
            onValueChange = {
                fullName = it
                fullNameError = null //Kullanıcı yazmaya başlayınca kırmızı uyarıyı kaldır
            },
            label = { Text(text = "Ad Soyad") },
            modifier = Modifier.fillMaxWidth(),
            singleLine = true,
            isError = fullNameError != null, //Hata varsa kutu kırmızı olur
            supportingText = {
                if (fullNameError != null) {
                    Text(text = fullNameError!!, color = MaterialTheme.colorScheme.error)
                }
            }
        )

        Spacer(modifier = Modifier.height(12.dp))

        OutlinedTextField(
            value = email,
            onValueChange = {
                email = it
                emailError = null
            },
            label = { Text("E-posta") },
            modifier = Modifier.fillMaxWidth(),
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Email),
            singleLine = true, // Virgül unutulmadı!
            isError = emailError != null,
            supportingText = {
                if (emailError != null) {
                    Text(text = emailError!!, color = MaterialTheme.colorScheme.error)
                }
            }
        )
        Spacer(modifier = Modifier.height(12.dp))

        OutlinedTextField(
            value = password,
            onValueChange = {
                password = it
                passwordError = null
            },
            label = { Text("Şifre") },
            modifier = Modifier.fillMaxWidth(),
            visualTransformation = PasswordVisualTransformation(),
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Password),
            singleLine = true, // Virgül unutulmadı!
            isError = passwordError != null,
            supportingText = {
                if (passwordError != null) {
                    Text(text = passwordError!!, color = MaterialTheme.colorScheme.error)
                }
            }
        )
        Spacer(modifier = Modifier.height(12.dp))

        OutlinedTextField(
            value = studentNo,
            onValueChange = { studentNo = it },
            label = { Text("Öğrenci No (opsiyonel)") },
            modifier = Modifier.fillMaxWidth(),
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
            singleLine = true
        )

        Spacer(modifier = Modifier.height(24.dp))

        if (authState is AuthState.Error) {
            Text(
                text = (authState as AuthState.Error).message,
                color = MaterialTheme.colorScheme.error,
                modifier = Modifier.padding(bottom = 12.dp)
            )
        }

        //Buttonun onClick olayını güncelledim
        Button(
            onClick = {
                val trimmedEmail = email.trim()
                val trimmedFullName = fullName.trim()
                val trimmedStudentNo = studentNo.trim().ifEmpty { null }
                val passwordRegex = "^(?=.*[A-Z])(?=.*[0-9])(?=.*[@#$%^&+=!._-]).{6,}$".toRegex()

                var hasError = false

                // Ad Soyad Kontrolü
                if (trimmedFullName.isBlank()) {
                    fullNameError = "Bu alan boş bırakılamaz"
                    hasError = true
                }

                // Email Kontrolü
                if (trimmedEmail.isBlank()) {
                    emailError = "Bu alan boş bırakılamaz"
                    hasError = true
                } else if (!android.util.Patterns.EMAIL_ADDRESS.matcher(trimmedEmail).matches()) {
                    emailError = "Geçersiz e-posta formatı"
                    hasError = true
                }

                // Şifre Kontrolü
                if (password.isBlank()) {
                    passwordError = "Bu alan boş bırakılamaz"
                    hasError = true
                } else if (password.length < 6) {
                    passwordError = "Şifre en az 6 karakter olmalıdır"
                    hasError = true
                } else if (!password.matches(passwordRegex)) {
                    passwordError = "Şifre en az 1 büyük harf, 1 sayı ve 1 özel karakter içermelidir"
                    hasError = true
                }

                // Eğer hiçbir hata yoksa veritabanına yolla
                if (!hasError) {
                    authViewModel.signUp(
                        email = trimmedEmail,
                        password = password,
                        fullName = trimmedFullName,
                        studentNo = trimmedStudentNo
                    )
                }
            },
            modifier = Modifier.fillMaxWidth(),
            enabled = authState !is AuthState.Loading
        ) {

            if (authState is AuthState.Loading) {
                CircularProgressIndicator(
                    modifier = Modifier.size(20.dp),
                    strokeWidth = 2.dp,
                    color = MaterialTheme.colorScheme.onPrimary
                )
            } else {
                Text("Kayıt Ol")
            }
        }

        Spacer(modifier = Modifier.height(12.dp))

        TextButton(onClick = {onNavigateToLogin()}) {
            Text("Zaten hesabın var mı? Giriş Yap")
        }
    }
}