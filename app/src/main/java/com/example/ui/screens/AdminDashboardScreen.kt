package com.example.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
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
import com.example.data.local.entity.ProductEntity
import com.example.ui.components.BarChartWidget
import com.example.ui.components.SimpleLineChart
import com.example.ui.components.StatusBadge
import com.example.ui.components.storeCategories
import com.example.ui.viewmodel.EcommerceViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AdminDashboardScreen(
    viewModel: EcommerceViewModel,
    onBackClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    var adminTab by remember { mutableIntStateOf(0) } // 0: Products, 1: Orders, 2: Inventory, 3: Analytics, 4: AI Intelligence

    val adminProducts by viewModel.adminProducts.collectAsState()
    val allOrders by viewModel.allOrders.collectAsState()
    val aiForecastText by viewModel.aiForecastText.collectAsState()
    val aiGeneratedDesc by viewModel.aiGeneratedDesc.collectAsState()

    var showAddProductDialog by remember { mutableStateOf(false) }
    var editingProduct by remember { mutableStateOf<ProductEntity?>(null) }

    // Add/Edit Form States
    var formName by remember { mutableStateOf("") }
    var formCategory by remember { mutableStateOf("Shoes") }
    var formBrand by remember { mutableStateOf("Three Brothers") }
    var formPrice by remember { mutableStateOf("3500") }
    var formDiscountPrice by remember { mutableStateOf("2990") }
    var formStock by remember { mutableStateOf("50") }
    var formSku by remember { mutableStateOf("TB-NEW-01") }
    var formSizes by remember { mutableStateOf("39, 40, 41, 42, 43") }
    var formColors by remember { mutableStateOf("Black, Brown") }
    var formImageUrl by remember { mutableStateOf("https://images.unsplash.com/photo-1549298916-b41d501d3772?auto=format&fit=crop&w=600&q=80") }
    var formDescription by remember { mutableStateOf("") }

    val openFormForNew = {
        editingProduct = null
        formName = ""
        formCategory = "Shoes"
        formBrand = "Three Brothers"
        formPrice = "3500"
        formDiscountPrice = "2990"
        formStock = "50"
        formSku = "TB-NEW-" + (10..99).random()
        formSizes = "39, 40, 41, 42, 43"
        formColors = "Black, Brown"
        formImageUrl = "https://images.unsplash.com/photo-1549298916-b41d501d3772?auto=format&fit=crop&w=600&q=80"
        formDescription = "High quality handcrafted product by Three Brothers."
        showAddProductDialog = true
    }

    val openFormForEdit: (ProductEntity) -> Unit = { p ->
        editingProduct = p
        formName = p.name
        formCategory = p.category
        formBrand = p.brand
        formPrice = p.price.toInt().toString()
        formDiscountPrice = p.discountPrice?.toInt()?.toString() ?: ""
        formStock = p.stockQuantity.toString()
        formSku = p.sku
        formSizes = p.sizes
        formColors = p.colors
        formImageUrl = p.imageUrl
        formDescription = p.description
        showAddProductDialog = true
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Three Brothers Admin Panel", fontWeight = FontWeight.Bold) },
                navigationIcon = {
                    IconButton(onClick = {
                        viewModel.isAdminMode.value = false
                        onBackClick()
                    }) {
                        Icon(Icons.Default.ArrowBack, contentDescription = "Exit Admin")
                    }
                },
                actions = {
                    if (adminTab == 0) {
                        IconButton(onClick = openFormForNew) {
                            Icon(Icons.Default.Add, contentDescription = "Add Product")
                        }
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
        ) {
            // Admin Navigation Tabs
            ScrollableTabRow(
                selectedTabIndex = adminTab,
                edgePadding = 16.dp,
                modifier = Modifier.fillMaxWidth()
            ) {
                Tab(
                    selected = adminTab == 0,
                    onClick = { adminTab = 0 },
                    text = { Text("Products (${adminProducts.size})") }
                )
                Tab(
                    selected = adminTab == 1,
                    onClick = { adminTab = 1 },
                    text = { Text("Orders (${allOrders.size})") }
                )
                Tab(
                    selected = adminTab == 2,
                    onClick = { adminTab = 2 },
                    text = { Text("Inventory") }
                )
                Tab(
                    selected = adminTab == 3,
                    onClick = { adminTab = 3 },
                    text = { Text("Analytics Suite") }
                )
                Tab(
                    selected = adminTab == 4,
                    onClick = { adminTab = 4 },
                    text = { Text("AI Intelligence") }
                )
            }

            when (adminTab) {
                // Tab 0: Product Upload & Management
                0 -> {
                    LazyColumn(
                        contentPadding = PaddingValues(16.dp),
                        verticalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        item {
                            Button(
                                onClick = openFormForNew,
                                shape = RoundedCornerShape(12.dp),
                                modifier = Modifier.fillMaxWidth().height(48.dp)
                            ) {
                                Icon(Icons.Default.Add, contentDescription = null)
                                Spacer(modifier = Modifier.width(8.dp))
                                Text("Upload New Product", fontWeight = FontWeight.Bold)
                            }
                        }

                        items(adminProducts) { product ->
                            Card(
                                shape = RoundedCornerShape(12.dp),
                                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                                modifier = Modifier.fillMaxWidth()
                            ) {
                                Row(
                                    verticalAlignment = Alignment.CenterVertically,
                                    modifier = Modifier.padding(12.dp)
                                ) {
                                    Column(modifier = Modifier.weight(1f)) {
                                        Text(text = product.name, fontWeight = FontWeight.Bold, fontSize = 14.sp)
                                        Text(text = "${product.category} • SKU: ${product.sku}", fontSize = 11.sp, color = Color.Gray)
                                        Text(
                                            text = "Price: PKR ${product.discountPrice?.toInt() ?: product.price.toInt()} | Stock: ${product.stockQuantity}",
                                            fontWeight = FontWeight.Bold,
                                            fontSize = 12.sp,
                                            color = MaterialTheme.colorScheme.primary
                                        )
                                    }

                                    IconButton(onClick = { openFormForEdit(product) }) {
                                        Icon(Icons.Default.Edit, contentDescription = "Edit", tint = MaterialTheme.colorScheme.primary)
                                    }

                                    IconButton(onClick = { viewModel.deleteProduct(product.id) }) {
                                        Icon(Icons.Default.Delete, contentDescription = "Delete", tint = Color.Red)
                                    }
                                }
                            }
                        }
                    }
                }

                // Tab 1: Orders Management
                1 -> {
                    LazyColumn(
                        contentPadding = PaddingValues(16.dp),
                        verticalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        items(allOrders) { order ->
                            Card(
                                shape = RoundedCornerShape(12.dp),
                                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                                modifier = Modifier.fillMaxWidth()
                            ) {
                                Column(modifier = Modifier.padding(14.dp)) {
                                    Row(
                                        horizontalArrangement = Arrangement.SpaceBetween,
                                        modifier = Modifier.fillMaxWidth()
                                    ) {
                                        Text(text = order.orderId, fontWeight = FontWeight.Bold)
                                        StatusBadge(status = order.status)
                                    }
                                    Spacer(modifier = Modifier.height(4.dp))
                                    Text(text = "Customer: ${order.customerName} (${order.customerPhone})", fontSize = 12.sp)
                                    Text(text = "Address: ${order.shippingAddress}", fontSize = 12.sp, color = Color.Gray)
                                    Text(text = "Amount: PKR ${order.finalAmount.toInt()} via ${order.paymentMethod}", fontWeight = FontWeight.Bold, fontSize = 13.sp)

                                    Spacer(modifier = Modifier.height(8.dp))

                                    Row(
                                        horizontalArrangement = Arrangement.spacedBy(6.dp),
                                        modifier = Modifier.fillMaxWidth()
                                    ) {
                                        listOf("Pending", "Confirmed", "Shipped", "Delivered", "Cancelled").forEach { status ->
                                            FilterChip(
                                                selected = (order.status == status),
                                                onClick = { viewModel.updateOrderStatus(order.orderId, status) },
                                                label = { Text(status, fontSize = 10.sp) }
                                            )
                                        }
                                    }
                                }
                            }
                        }
                    }
                }

                // Tab 2: Inventory Management
                2 -> {
                    Column(
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(16.dp)
                            .verticalScroll(rememberScrollState())
                    ) {
                        Text(text = "Inventory Stock & Low Stock Alerts", fontWeight = FontWeight.Bold, fontSize = 16.sp)
                        Spacer(modifier = Modifier.height(12.dp))

                        adminProducts.forEach { p ->
                            val isLow = p.stockQuantity < 20
                            Card(
                                shape = RoundedCornerShape(10.dp),
                                colors = CardDefaults.cardColors(
                                    containerColor = if (isLow) Color(0xFFFEF2F2) else MaterialTheme.colorScheme.surface
                                ),
                                modifier = Modifier.fillMaxWidth().padding(vertical = 4.dp)
                            ) {
                                Row(
                                    verticalAlignment = Alignment.CenterVertically,
                                    horizontalArrangement = Arrangement.SpaceBetween,
                                    modifier = Modifier.padding(12.dp)
                                ) {
                                    Column {
                                        Text(text = p.name, fontWeight = FontWeight.Bold, fontSize = 13.sp)
                                        Text(text = "SKU: ${p.sku} | Category: ${p.category}", fontSize = 11.sp)
                                    }
                                    Text(
                                        text = "Stock: ${p.stockQuantity}",
                                        fontWeight = FontWeight.Bold,
                                        color = if (isLow) Color.Red else Color(0xFF15803D)
                                    )
                                }
                            }
                        }
                    }
                }

                // Tab 3: Analytics Suite
                3 -> {
                    Column(
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(16.dp)
                            .verticalScroll(rememberScrollState())
                    ) {
                        // Metrics Cards
                        Row(horizontalArrangement = Arrangement.spacedBy(12.dp), modifier = Modifier.fillMaxWidth()) {
                            Card(
                                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.primary),
                                modifier = Modifier.weight(1f)
                            ) {
                                Column(modifier = Modifier.padding(14.dp)) {
                                    Text(text = "Total Revenue", color = Color.White.copy(alpha = 0.8f), fontSize = 11.sp)
                                    Text(text = "PKR 482,500", color = Color.White, fontWeight = FontWeight.Black, fontSize = 18.sp)
                                }
                            }
                            Card(
                                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.secondary),
                                modifier = Modifier.weight(1f)
                            ) {
                                Column(modifier = Modifier.padding(14.dp)) {
                                    Text(text = "Estimated Profit", color = Color.White.copy(alpha = 0.8f), fontSize = 11.sp)
                                    Text(text = "PKR 145,200", color = Color.White, fontWeight = FontWeight.Black, fontSize = 18.sp)
                                }
                            }
                        }

                        Spacer(modifier = Modifier.height(16.dp))

                        // Sales Trend Line Chart
                        SimpleLineChart(
                            data = listOf(120f, 180f, 210f, 340f, 410f, 482f),
                            labels = listOf("Jan", "Feb", "Mar", "Apr", "May", "Jun")
                        )

                        Spacer(modifier = Modifier.height(16.dp))

                        // Category Share Bar Chart
                        BarChartWidget(
                            categories = listOf(
                                "Shoes & Slippers" to 42f,
                                "Clothing" to 28f,
                                "Electronics" to 18f,
                                "Grocery & Digital" to 12f
                            )
                        )
                    }
                }

                // Tab 4: AI Intelligence
                4 -> {
                    Column(
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(16.dp)
                            .verticalScroll(rememberScrollState())
                    ) {
                        Text(text = "AI Sales & Inventory Forecasting", fontWeight = FontWeight.Bold, fontSize = 16.sp)
                        Spacer(modifier = Modifier.height(8.dp))

                        Button(
                            onClick = { viewModel.loadAiForecast() },
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Icon(Icons.Default.AutoAwesome, contentDescription = null)
                            Spacer(modifier = Modifier.width(8.dp))
                            Text("Generate AI Market Forecast")
                        }

                        if (aiForecastText != null) {
                            Spacer(modifier = Modifier.height(12.dp))
                            Card(
                                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant),
                                modifier = Modifier.fillMaxWidth()
                            ) {
                                Text(
                                    text = aiForecastText!!,
                                    fontSize = 13.sp,
                                    lineHeight = 18.sp,
                                    modifier = Modifier.padding(14.dp)
                                )
                            }
                        }
                    }
                }
            }
        }
    }

    // Add / Edit Product Dialog
    if (showAddProductDialog) {
        AlertDialog(
            onDismissRequest = { showAddProductDialog = false },
            title = { Text(if (editingProduct == null) "Add New Product" else "Edit Product") },
            text = {
                Column(modifier = Modifier.verticalScroll(rememberScrollState())) {
                    OutlinedTextField(
                        value = formName,
                        onValueChange = { formName = it },
                        label = { Text("Product Name") },
                        modifier = Modifier.fillMaxWidth()
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    OutlinedTextField(
                        value = formPrice,
                        onValueChange = { formPrice = it },
                        label = { Text("Price (PKR)") },
                        modifier = Modifier.fillMaxWidth()
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    OutlinedTextField(
                        value = formDiscountPrice,
                        onValueChange = { formDiscountPrice = it },
                        label = { Text("Discount Price (PKR)") },
                        modifier = Modifier.fillMaxWidth()
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    OutlinedTextField(
                        value = formStock,
                        onValueChange = { formStock = it },
                        label = { Text("Stock Quantity") },
                        modifier = Modifier.fillMaxWidth()
                    )
                    Spacer(modifier = Modifier.height(8.dp))

                    Text(text = "Category", fontWeight = FontWeight.Bold, fontSize = 12.sp)
                    Row(horizontalArrangement = Arrangement.spacedBy(4.dp)) {
                        storeCategories.filter { it != "All" }.take(4).forEach { cat ->
                            FilterChip(
                                selected = (formCategory == cat),
                                onClick = { formCategory = cat },
                                label = { Text(cat, fontSize = 11.sp) }
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(8.dp))

                    OutlinedButton(
                        onClick = { viewModel.generateProductCopy(formName, formCategory) },
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Icon(Icons.Default.AutoAwesome, contentDescription = null, modifier = Modifier.size(16.dp))
                        Spacer(modifier = Modifier.width(6.dp))
                        Text("AI Auto-Generate Description", fontSize = 12.sp)
                    }

                    if (aiGeneratedDesc != null) {
                        Spacer(modifier = Modifier.height(4.dp))
                        Text(text = aiGeneratedDesc!!, fontSize = 11.sp, color = MaterialTheme.colorScheme.primary)
                        formDescription = aiGeneratedDesc!!
                    }

                    Spacer(modifier = Modifier.height(8.dp))
                    OutlinedTextField(
                        value = formDescription,
                        onValueChange = { formDescription = it },
                        label = { Text("Description") },
                        modifier = Modifier.fillMaxWidth()
                    )
                }
            },
            confirmButton = {
                Button(
                    onClick = {
                        if (formName.isNotBlank()) {
                            val product = ProductEntity(
                                id = editingProduct?.id ?: 0L,
                                name = formName,
                                category = formCategory,
                                brand = formBrand,
                                price = formPrice.toDoubleOrNull() ?: 2000.0,
                                discountPrice = formDiscountPrice.toDoubleOrNull(),
                                stockQuantity = formStock.toIntOrNull() ?: 10,
                                sku = formSku,
                                sizes = formSizes,
                                colors = formColors,
                                imageUrl = formImageUrl,
                                description = formDescription,
                                shortDescription = formName
                            )
                            viewModel.saveProduct(product)
                            showAddProductDialog = false
                        }
                    }
                ) {
                    Text("Save Product")
                }
            },
            dismissButton = {
                TextButton(onClick = { showAddProductDialog = false }) {
                    Text("Cancel")
                }
            }
        )
    }
}
