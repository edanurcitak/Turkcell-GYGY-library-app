package com.turkcell.libraryapp.ui.navigation

import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.turkcell.libraryapp.ui.screen.BookDetailScreen
import com.turkcell.libraryapp.ui.screen.BorrowScreen
import com.turkcell.libraryapp.ui.screen.HomeScreen
import com.turkcell.libraryapp.ui.screen.LoginScreen
import com.turkcell.libraryapp.ui.screen.RegisterScreen
import com.turkcell.libraryapp.ui.screen.SplashScreen
import com.turkcell.libraryapp.ui.viewmodel.AuthViewModel
import com.turkcell.libraryapp.ui.viewmodel.BookViewModel


/*Class dosyası açtık çünkü kranlar birer "nesne" (obje) değildir; sadece ekrana ne çizileceğini söyleyen
birer talimattır. Bu yüzden NavGraph, LoginScreen veya herhangi bir arayüz elemanı sadece birer
@Composable fun (fonksiyon) olarak yazılır. Durum tutmazlar, sadece çağrıldıklarında ekrana bir şeyler çizerler.*/

//Bu dosya tüm sayfaları tek bir çatı altında toplar ve aralarındaki kapıları tanımlar

@Composable
fun NavGraph(navController: NavHostController = rememberNavController()) {
    val authViewModel: AuthViewModel = viewModel()
    val bookViewModel: BookViewModel = viewModel()

    NavHost(navController = navController, startDestination = Screen.Splash.route)//Uygulama ilk açıldığında kullanıcının karşısına çıkacak ilk sayfa
    {

        composable(Screen.Splash.route) {
            SplashScreen(authViewModel,
                onAuthenticated = { role ->
                    navController.navigate(Screen.Homepage.route){
                        popUpTo(Screen.Splash.route) {inclusive=true}
                    }
                },
                onUnauthenticated = {
                    navController.navigate(Screen.Login.route)
                    {
                        popUpTo(Screen.Splash.route) {inclusive=true}
                    }
                })
        }
        composable(Screen.Login.route) { LoginScreen(
            onNavigateToRegister = { navController.navigate(Screen.Register.route) },
            onLoginSuccess = {role ->
                navController.navigate(Screen.Homepage.route) {
                    popUpTo(Screen.Login.route) {inclusive=true}
                }
            },
            authViewModel
            ) }
        composable(Screen.Register.route) { RegisterScreen(
            onNavigateToLogin = { navController.navigate(Screen.Login.route) },
            authViewModel
        ) }
        composable(Screen.Homepage.route) {
            HomeScreen(navController = navController, authViewModel = authViewModel, bookViewModel = bookViewModel)
        }

        composable(
            route = Screen.BookDetail.route,
            arguments = listOf(navArgument("bookId") { type = NavType.StringType })
        ) { backStackEntry ->

            val bookId = backStackEntry.arguments?.getString("bookId") ?: ""

            BookDetailScreen(
                bookId = bookId,
                bookViewModel = bookViewModel,
                onNavigateToBorrows = {
                    navController.navigate(Screen.Borrow.route)
                }
            )
        }
        composable(Screen.Borrow.route) {
            BorrowScreen(bookViewModel = bookViewModel)
        }
    }
}