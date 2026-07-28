package com.example.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage
import com.example.ui.viewmodel.EcommerceViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CartScreen(
    viewModel: EcommerceViewModel,
    onBackClick: () -> Unit,
    onNavigateToCheckout: () -> Unit,
    modifier: Modifier = Modifier
) {
    val cartSummary by viewModel.cartSummary.collectAsState()
    val couponMessage by viewModel.couponMessage.collectAsState()
    var couponInput by remember { mutableStateOf("") }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(text = "Shopping Cart (${cartSummary.items.size})", fontWeight = FontWeight.Bold) },
                navigationIcon = {
                    IconButton(onClick = onBackClick) {
                        Icon(Icons.Default.ArrowBack, contentDescription = "Back")
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = MaterialTheme.colorScheme.surface)
            )
        },
        bottomBar = {
            if (cartSummary.items.isNotEmpty()) {
                Surface(
                    tonalElevation = 8.dp,
                    shadowElevation = 8.dp,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        Row(
                            horizontalArrangement = Arrangement.SpaceBetween,
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Text(text = "Subtotal:", color = MaterialTheme.colorScheme.onSurfaceVariant)
                            Text(text = "PKR ${cartSummary.subtotal.toInt()}", fontWeight = FontWeight.Bold)
                        }

                        if (cartSummary.discountAmount > 0) {
                            Row(
                                horizontalArrangement = Arrangement.SpaceBetween,
                                modifier = Modifier.fillMaxWidth()
                            ) {
                                Text(text = "Coupon Discount:", color = Color(0xFF059669))
                                Text(text = "-PKR ${cartSummary.discountAmount.toInt()}", color = Color(0xFF059669), fontWeight = FontWeight.Bold)
                            }
                        }

                        Row(
                            horizontalArrangement = Arrangement.SpaceBetween,
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Text(text = "Delivery Charges:", color = MaterialTheme.colorScheme.onSurfaceVariant)
                            Text(text = "PKR ${cartSummary.deliveryFee.toInt()}", fontWeight = FontWeight.Bold)
                        }

                        HorizontalDivider(modifier = Modifier.padding(vertical = 8.dp))

                        Row(
                            horizontalArrangement = Arrangement.SpaceBetween,
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Text(text = "Grand Total:", fontSize = 18.sp, fontWeight = FontWeight.Black)
                            Text(
                                text = "PKR ${cartSummary.total.toInt()}",
                                fontSize = 18.sp,
                                fontWeight = FontWeight.Black,
                                color = MaterialTheme.colorScheme.primary
                            )
                        }

                        Spacer(modifier = Modifier.height(12.dp))

                        Button(
                            onClick = onNavigateToCheckout,
                            shape = RoundedCornerShape(12.dp),
                            modifier = Modifier.fillMaxWidth().height(48.dp)
                        ) {
                            Text(text = "Proceed to Checkout", fontWeight = FontWeight.Bold, fontSize = 16.sp)
                            Spacer(modifier = Modifier.width(8.dp))
                            Icon(Icons.Default.ArrowForward, contentDescription = null)
                        }
                    }
                }
            }
        }
    ) { innerPadding ->
        if (cartSummary.items.isEmpty()) {
            Box(
                contentAlignment = Alignment.Center,
                modifier = Modifier
                    .fillMaxSize()
                    .padding(innerPadding)
            ) {
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Icon(
                        imageVector = Icons.Default.RemoveShoppingCart,
                        contentDescription = null,
                        modifier = Modifier.size(64.dp),
                        tint = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                    Spacer(modifier = Modifier.height(12.dp))
                    Text(text = "Your cart is empty", fontWeight = FontWeight.Bold, fontSize = 18.sp)
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(text = "Add products to start shopping", color = MaterialTheme.colorScheme.onSurfaceVariant)
                }
            }
        } else {
            LazyColumn(
                contentPadding = PaddingValues(16.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp),
                modifier = modifier
                    .fillMaxSize()
                    .padding(innerPadding)
            ) {
                items(cartSummary.items) { item ->
                    Card(
                        shape = RoundedCornerShape(12.dp),
                        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                        elevation = CardDefaults.cardElevation(2.dp),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            modifier = Modifier.padding(12.dp)
                        ) {
                            AsyncImage(
                                model = item.product.imageUrl,
                                contentDescription = item.product.name,
                                contentScale = ContentScale.Crop,
                                modifier = Modifier
                                    .size(72.dp)
                                    .clip(RoundedCornerShape(8.dp))
                                    .background(MaterialTheme.colorScheme.surfaceVariant)
                            )

                            Spacer(modifier = Modifier.width(12.dp))

                            Column(modifier = Modifier.weight(1f)) {
                                Text(
                                    text = item.product.name,
                                    fontWeight = FontWeight.Bold,
                                    fontSize = 14.sp,
                                    maxLines = 1
                                )
                                if (item.cartItem.selectedSize.isNotEmpty() || item.cartItem.selectedColor.isNotEmpty()) {
                                    Text(
                                        text = "Size: ${item.cartItem.selectedSize} | Color: ${item.cartItem.selectedColor}",
                                        fontSize = 11.sp,
                                        color = MaterialTheme.colorScheme.onSurfaceVariant
                                    )
                                }
                                val unitPrice = item.product.discountPrice ?: item.product.price
                                Text(
                                    text = "PKR ${unitPrice.toInt()}",
                                    fontWeight = FontWeight.Bold,
                                    color = MaterialTheme.colorScheme.primary,
                                    fontSize = 13.sp
                                )

                                Spacer(modifier = Modifier.height(6.dp))

                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    IconButton(
                                        onClick = { viewModel.updateCartQuantity(item.product.id, item.cartItem.quantity - 1) },
                                        modifier = Modifier.size(28.dp).background(MaterialTheme.colorScheme.surfaceVariant, shape = RoundedCornerShape(6.dp))
                                    ) {
                                        Text("-", fontWeight = FontWeight.Bold)
                                    }
                                    Text(
                                        text = "${item.cartItem.quantity}",
                                        fontWeight = FontWeight.Bold,
                                        fontSize = 13.sp,
                                        modifier = Modifier.padding(horizontal = 10.dp)
                                    )
                                    IconButton(
                                        onClick = { viewModel.updateCartQuantity(item.product.id, item.cartItem.quantity + 1) },
                                        modifier = Modifier.size(28.dp).background(MaterialTheme.colorScheme.surfaceVariant, shape = RoundedCornerShape(6.dp))
                                    ) {
                                        Text("+", fontWeight = FontWeight.Bold)
                                    }
                                }
                            }

                            IconButton(onClick = { viewModel.removeFromCart(item.product.id) }) {
                                Icon(Icons.Default.Delete, contentDescription = "Remove", tint = Color.Red)
                            }
                        }
                    }
                }

                // Coupon Code Box
                item {
                    Card(
                        shape = RoundedCornerShape(12.dp),
                        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                        modifier = Modifier.fillMaxWidth().padding(top = 8.dp)
                    ) {
                        Column(modifier = Modifier.padding(14.dp)) {
                            Text(text = "Have a Coupon / Promo Code?", fontWeight = FontWeight.Bold, fontSize = 13.sp)
                            Spacer(modifier = Modifier.height(8.dp))
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                OutlinedTextField(
                                    value = couponInput,
                                    onValueChange = { couponInput = it },
                                    placeholder = { Text("Try THREEB10 or WELCOME20", fontSize = 12.sp) },
                                    singleLine = true,
                                    modifier = Modifier.weight(1f)
                                )
                                Spacer(modifier = Modifier.width(8.dp))
                                Button(
                                    onClick = {
                                        viewModel.applyCoupon(couponInput)
                                    }
                                ) {
                                    Text("Apply")
                                }
                            }
                            if (couponMessage != null) {
                                Spacer(modifier = Modifier.height(4.dp))
                                Text(
                                    text = couponMessage!!,
                                    fontSize = 12.sp,
                                    color = if (cartSummary.appliedCoupon != null) Color(0xFF059669) else Color.Red
                                )
                            }
                        }
                    }
                }
            }
        }
    }
}
