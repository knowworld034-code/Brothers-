package com.example.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ui.viewmodel.EcommerceViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CheckoutScreen(
    viewModel: EcommerceViewModel,
    onBackClick: () -> Unit,
    onOrderPlaced: (String) -> Unit,
    modifier: Modifier = Modifier
) {
    val cartSummary by viewModel.cartSummary.collectAsState()
    val userProfile by viewModel.userProfile.collectAsState()

    var customerName by remember { mutableStateOf(userProfile?.name ?: "Farhan Nadeem") }
    var customerPhone by remember { mutableStateOf(userProfile?.phone ?: "03472065158") }
    var customerEmail by remember { mutableStateOf(userProfile?.email ?: "Mrbast@gmail.com") }
    var shippingAddress by remember { mutableStateOf(userProfile?.address ?: "Plot 45-B, Commercial Area, Gulberg III, Lahore") }

    val paymentOptions = listOf(
        "Cash on Delivery",
        "EasyPaisa",
        "JazzCash",
        "Bank Transfer",
        "Credit/Debit Card"
    )
    var selectedPayment by remember { mutableStateOf(paymentOptions.first()) }

    var placedOrderId by remember { mutableStateOf<String?>(null) }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(text = "Secure Checkout", fontWeight = FontWeight.Bold) },
                navigationIcon = {
                    IconButton(onClick = onBackClick) {
                        Icon(Icons.Default.ArrowBack, contentDescription = "Back")
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = MaterialTheme.colorScheme.surface)
            )
        }
    ) { innerPadding ->
        Column(
            modifier = modifier
                .fillMaxSize()
                .padding(innerPadding)
                .verticalScroll(rememberScrollState())
                .padding(16.dp)
        ) {
            // Shipping Address Card
            Card(
                shape = RoundedCornerShape(12.dp),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(Icons.Default.LocationOn, contentDescription = null, tint = MaterialTheme.colorScheme.primary)
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(text = "Shipping Details", fontWeight = FontWeight.Bold, fontSize = 16.sp)
                    }

                    Spacer(modifier = Modifier.height(12.dp))

                    OutlinedTextField(
                        value = customerName,
                        onValueChange = { customerName = it },
                        label = { Text("Full Name") },
                        modifier = Modifier.fillMaxWidth()
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    OutlinedTextField(
                        value = customerPhone,
                        onValueChange = { customerPhone = it },
                        label = { Text("WhatsApp / Phone Number") },
                        modifier = Modifier.fillMaxWidth()
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    OutlinedTextField(
                        value = customerEmail,
                        onValueChange = { customerEmail = it },
                        label = { Text("Email Address") },
                        modifier = Modifier.fillMaxWidth()
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    OutlinedTextField(
                        value = shippingAddress,
                        onValueChange = { shippingAddress = it },
                        label = { Text("Complete Shipping Address") },
                        modifier = Modifier.fillMaxWidth()
                    )
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            // Payment Methods Selection Card
            Card(
                shape = RoundedCornerShape(12.dp),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(Icons.Default.Payment, contentDescription = null, tint = MaterialTheme.colorScheme.primary)
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(text = "Select Payment Method", fontWeight = FontWeight.Bold, fontSize = 16.sp)
                    }

                    Spacer(modifier = Modifier.height(12.dp))

                    paymentOptions.forEach { option ->
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            modifier = Modifier
                                .fillMaxWidth()
                                .clickable { selectedPayment = option }
                                .padding(vertical = 6.dp)
                        ) {
                            RadioButton(
                                selected = (selectedPayment == option),
                                onClick = { selectedPayment = option }
                            )
                            Spacer(modifier = Modifier.width(8.dp))
                            Text(text = option, fontWeight = FontWeight.Medium, fontSize = 14.sp)
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            // Order Summary Breakdown Card
            Card(
                shape = RoundedCornerShape(12.dp),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Text(text = "Order Summary", fontWeight = FontWeight.Bold, fontSize = 16.sp)
                    Spacer(modifier = Modifier.height(8.dp))

                    cartSummary.items.forEach { item ->
                        Row(
                            horizontalArrangement = Arrangement.SpaceBetween,
                            modifier = Modifier.fillMaxWidth().padding(vertical = 2.dp)
                        ) {
                            Text(
                                text = "${item.cartItem.quantity}x ${item.product.name}",
                                fontSize = 13.sp,
                                modifier = Modifier.weight(1f)
                            )
                            val price = (item.product.discountPrice ?: item.product.price) * item.cartItem.quantity
                            Text(text = "PKR ${price.toInt()}", fontSize = 13.sp, fontWeight = FontWeight.Bold)
                        }
                    }

                    HorizontalDivider(modifier = Modifier.padding(vertical = 8.dp))

                    Row(horizontalArrangement = Arrangement.SpaceBetween, modifier = Modifier.fillMaxWidth()) {
                        Text(text = "Subtotal:")
                        Text(text = "PKR ${cartSummary.subtotal.toInt()}", fontWeight = FontWeight.Bold)
                    }
                    if (cartSummary.discountAmount > 0) {
                        Row(horizontalArrangement = Arrangement.SpaceBetween, modifier = Modifier.fillMaxWidth()) {
                            Text(text = "Discount:", color = Color(0xFF059669))
                            Text(text = "-PKR ${cartSummary.discountAmount.toInt()}", color = Color(0xFF059669), fontWeight = FontWeight.Bold)
                        }
                    }
                    Row(horizontalArrangement = Arrangement.SpaceBetween, modifier = Modifier.fillMaxWidth()) {
                        Text(text = "Delivery Fee:")
                        Text(text = "PKR ${cartSummary.deliveryFee.toInt()}", fontWeight = FontWeight.Bold)
                    }

                    HorizontalDivider(modifier = Modifier.padding(vertical = 8.dp))

                    Row(horizontalArrangement = Arrangement.SpaceBetween, modifier = Modifier.fillMaxWidth()) {
                        Text(text = "Total Payable:", fontSize = 16.sp, fontWeight = FontWeight.Black)
                        Text(
                            text = "PKR ${cartSummary.total.toInt()}",
                            fontSize = 18.sp,
                            fontWeight = FontWeight.Black,
                            color = MaterialTheme.colorScheme.primary
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(24.dp))

            // Confirm & Place Order Button
            Button(
                onClick = {
                    if (customerName.isNotBlank() && customerPhone.isNotBlank() && shippingAddress.isNotBlank()) {
                        viewModel.placeOrder(
                            customerName = customerName,
                            phone = customerPhone,
                            email = customerEmail,
                            address = shippingAddress,
                            paymentMethod = selectedPayment,
                            onSuccess = { id ->
                                placedOrderId = id
                            }
                        )
                    }
                },
                shape = RoundedCornerShape(12.dp),
                modifier = Modifier.fillMaxWidth().height(50.dp)
            ) {
                Icon(Icons.Default.CheckCircle, contentDescription = null)
                Spacer(modifier = Modifier.width(8.dp))
                Text(text = "Confirm & Place Order", fontWeight = FontWeight.Bold, fontSize = 16.sp)
            }
        }
    }

    // Order Success Dialog
    if (placedOrderId != null) {
        AlertDialog(
            onDismissRequest = { },
            icon = { Icon(Icons.Default.CheckCircle, contentDescription = null, tint = Color(0xFF059669), modifier = Modifier.size(48.dp)) },
            title = { Text("Order Placed Successfully!") },
            text = {
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Text(text = "Order ID: ${placedOrderId}", fontWeight = FontWeight.Bold, fontSize = 16.sp)
                    Spacer(modifier = Modifier.height(8.dp))
                    Text(
                        text = "Thank you for shopping with Three Brothers! We will confirm your order via phone/WhatsApp at $customerPhone shortly.",
                        fontSize = 13.sp
                    )
                }
            },
            confirmButton = {
                Button(
                    onClick = {
                        val id = placedOrderId!!
                        placedOrderId = null
                        onOrderPlaced(id)
                    }
                ) {
                    Text("Track Order")
                }
            }
        )
    }
}
