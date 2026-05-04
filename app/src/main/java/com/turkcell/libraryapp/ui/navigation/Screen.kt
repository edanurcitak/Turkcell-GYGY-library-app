package com.turkcell.libraryapp.ui.navigation

/*Eğer uygulamanın bir yerinde yanlışlıkla "loginn" veya "Login" (büyük harfle) yazarsak,
Android Studio bu hatayı kodu yazarken fark etmez. Uygulama çalışır, butona basınca hedef
bulunamadığı için uygulama anında çöker. "login" metnini elle yazmak yerine uygulamanın her yerinde
Screen.Login.route değişkenini çağırıyoruz ki bir harf hatası yaparsak Android Studio altını kırmızı çizsin.*/

// Sayfa routelarımın tanımı
sealed class Screen(val route: String)// Sealed class çünkü uygulamanın sayfa sayısı belli ve sabit
{
    object Login : Screen("login")
    object Register : Screen("register")
    object Homepage : Screen("homepage")
    object Splash : Screen("splash")
    object Borrow : Screen("borrow_screen")
    object BookDetail : Screen("book_detail/{bookId}") {
        fun createRoute(bookId: String) = "book_detail/$bookId"
    }
}