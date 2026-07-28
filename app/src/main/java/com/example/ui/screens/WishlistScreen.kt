package com.example.ui.screens

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ui.components.ProductCard
import com.example.ui.viewmodel.EcommerceViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun WishlistScreen(
    viewModel: EcommerceViewModel,
    onNavigateToProductDetail: (Long) -> Unit,
    modifier: Modifier = Modifier
) {
    val wishlistItems by viewModel.wishlistItems.collectAsState()
    val allProducts by viewModel.allProducts.collectAsState()

    val wishlistedProducts = remember(wishlistItems, allProducts) {
        val ids = wishlistItems.map { it.productId }.toSet()
        allProducts.filter { ids.contains(it.id) }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("My Wishlist (${wishlistedProducts.size})", fontWeight = FontWeight.Bold) },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = MaterialTheme.colorScheme.surface)
            )
        }
    ) { innerPadding ->
        if (wishlistedProducts.isEmpty()) {
            Box(
                contentAlignment = Alignment.Center,
                modifier = Modifier
                    .fillMaxSize()
                    .padding(innerPadding)
            ) {
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Icon(
                        imageVector = Icons.Default.Favorite,
                        contentDescription = null,
                        modifier = Modifier.size(64.dp),
                        tint = Color.LightGray
                    )
                    Spacer(modifier = Modifier.height(12.dp))
                    Text(text = "Your wishlist is empty", fontWeight = FontWeight.Bold, fontSize = 18.sp)
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(text = "Save items you love to view them later", color = MaterialTheme.colorScheme.onSurfaceVariant)
                }
            }
        } else {
            LazyVerticalGrid(
                columns = GridCells.Fixed(2),
                contentPadding = PaddingValues(16.dp),
                horizontalArrangement = Arrangement.spacedBy(12.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp),
                modifier = modifier
                    .fillMaxSize()
                    .padding(innerPadding)
            ) {
                items(wishlistedProducts) { product ->
                    ProductCard(
                        product = product,
                        isWishlisted = true,
                        onProductClick = { onNavigateToProductDetail(product.id) },
                        onWishlistClick = { viewModel.toggleWishlist(product.id) },
                        onAddToCartClick = { viewModel.addToCart(product.id) }
                    )
                }
            }
        }
    }
}
