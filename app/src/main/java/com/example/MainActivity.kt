package com.example

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.navigation.NavGraph.Companion.findStartDestination
import androidx.navigation.NavType
import androidx.navigation.compose.*
import androidx.navigation.navArgument
import com.example.ui.components.WhatsAppFloatingButton
import com.example.ui.screens.*
import com.example.ui.theme.ThreeBrothersTheme
import com.example.ui.viewmodel.EcommerceViewModel

sealed class Screen(val route: String, val title: String, val icon: ImageVector) {
    object Home : Screen("home", "Home", Icons.Default.Home)
    object AiAssistant : Screen("ai_assistant", "AI Assistant", Icons.Default.AutoAwesome)
    object Wishlist : Screen("wishlist", "Wishlist", Icons.Default.Favorite)
    object Orders : Screen("orders", "Orders", Icons.Default.LocalShipping)
    object Profile : Screen("profile", "Account", Icons.Default.Person)
}

class MainActivity : ComponentActivity() {

    private val viewModel: EcommerceViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        setContent {
            var isDarkTheme by remember { mutableStateOf(true) }

            ThreeBrothersTheme(darkTheme = isDarkTheme) {
                val navController = rememberNavController()
                val navBackStackEntry by navController.currentBackStackEntryAsState()
                val currentRoute = navBackStackEntry?.destination?.route

                val bottomNavScreens = listOf(
                    Screen.Home,
                    Screen.AiAssistant,
                    Screen.Wishlist,
                    Screen.Orders,
                    Screen.Profile
                )

                val showBottomNav = currentRoute in bottomNavScreens.map { it.route }

                Scaffold(
                    modifier = Modifier.fillMaxSize(),
                    bottomBar = {
                        if (showBottomNav) {
                            NavigationBar {
                                bottomNavScreens.forEach { screen ->
                                    NavigationBarItem(
                                        icon = { Icon(screen.icon, contentDescription = screen.title) },
                                        label = { Text(screen.title) },
                                        selected = currentRoute == screen.route,
                                        onClick = {
                                            navController.navigate(screen.route) {
                                                popUpTo(navController.graph.findStartDestination().id) {
                                                    saveState = true
                                                }
                                                launchSingleTop = true
                                                restoreState = true
                                            }
                                        }
                                    )
                                }
                            }
                        }
                    },
                    floatingActionButton = {
                        if (showBottomNav) {
                            WhatsAppFloatingButton(
                                onClick = viewModel::openWhatsAppChat
                            )
                        }
                    }
                ) { innerPadding ->
                    NavHost(
                        navController = navController,
                        startDestination = Screen.Home.route,
                        modifier = Modifier.padding(innerPadding)
                    ) {
                        // Home Screen
                        composable(Screen.Home.route) {
                            HomeScreen(
                                viewModel = viewModel,
                                onNavigateToProductDetail = { productId ->
                                    navController.navigate("product_detail/$productId")
                                },
                                onNavigateToCart = {
                                    navController.navigate("cart")
                                }
                            )
                        }

                        // Product Detail Screen
                        composable(
                            route = "product_detail/{productId}",
                            arguments = listOf(navArgument("productId") { type = NavType.LongType })
                        ) { backStackEntry ->
                            val productId = backStackEntry.arguments?.getLong("productId") ?: 0L
                            ProductDetailScreen(
                                productId = productId,
                                viewModel = viewModel,
                                onBackClick = { navController.popBackStack() },
                                onNavigateToCart = { navController.navigate("cart") }
                            )
                        }

                        // Cart Screen
                        composable("cart") {
                            CartScreen(
                                viewModel = viewModel,
                                onBackClick = { navController.popBackStack() },
                                onNavigateToCheckout = { navController.navigate("checkout") }
                            )
                        }

                        // Checkout Screen
                        composable("checkout") {
                            CheckoutScreen(
                                viewModel = viewModel,
                                onBackClick = { navController.popBackStack() },
                                onOrderPlaced = { orderId ->
                                    navController.navigate(Screen.Orders.route) {
                                        popUpTo(Screen.Home.route)
                                    }
                                }
                            )
                        }

                        // AI Assistant Chat Screen
                        composable(Screen.AiAssistant.route) {
                            AiAssistantScreen(viewModel = viewModel)
                        }

                        // Wishlist Screen
                        composable(Screen.Wishlist.route) {
                            WishlistScreen(
                                viewModel = viewModel,
                                onNavigateToProductDetail = { productId ->
                                    navController.navigate("product_detail/$productId")
                                }
                            )
                        }

                        // Orders & Live Tracking Screen
                        composable(Screen.Orders.route) {
                            OrderTrackingScreen(
                                viewModel = viewModel,
                                onBackClick = { navController.popBackStack() }
                            )
                        }

                        // Profile Screen
                        composable(Screen.Profile.route) {
                            UserProfileScreen(
                                viewModel = viewModel,
                                onNavigateToOrders = { navController.navigate(Screen.Orders.route) },
                                onNavigateToWishlist = { navController.navigate(Screen.Wishlist.route) },
                                onNavigateToAdmin = { navController.navigate("admin") },
                                onToggleDarkTheme = { isDarkTheme = it },
                                isDarkTheme = isDarkTheme
                            )
                        }

                        // Owner Admin Dashboard Screen
                        composable("admin") {
                            AdminDashboardScreen(
                                viewModel = viewModel,
                                onBackClick = { navController.popBackStack() }
                            )
                        }
                    }
                }
            }
        }
    }
}
