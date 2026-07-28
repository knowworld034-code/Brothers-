package com.example.ui.viewmodel

import android.app.Application
import android.content.Intent
import android.net.Uri
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.data.local.ThreeBrothersDatabase
import com.example.data.local.entity.*
import com.example.data.remote.GeminiAiService
import com.example.data.repository.EcommerceRepository
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch

data class ChatMessage(
    val sender: String, // "User" or "AI"
    val text: String,
    val timestamp: Long = System.currentTimeMillis()
)

data class CartSummary(
    val items: List<CartItemWithProduct>,
    val subtotal: Double,
    val discountAmount: Double,
    val deliveryFee: Double,
    val total: Double,
    val appliedCoupon: CouponEntity?
)

data class CartItemWithProduct(
    val cartItem: CartItemEntity,
    val product: ProductEntity
)

class EcommerceViewModel(application: Application) : AndroidViewModel(application) {

    private val db = ThreeBrothersDatabase.getDatabase(application)
    val repository = EcommerceRepository(db)

    // UI State
    val selectedCategory = MutableStateFlow("All")
    val searchQuery = MutableStateFlow("")
    val sortOption = MutableStateFlow("Featured") // Featured, PriceLowHigh, PriceHighLow, Rating

    val isAdminMode = MutableStateFlow(false)
    val selectedProductId = MutableStateFlow<Long?>(null)
    val activeTab = MutableStateFlow("Home") // Home, Categories, AI, Wishlist, Profile, Admin

    val appliedCoupon = MutableStateFlow<CouponEntity?>(null)
    val couponMessage = MutableStateFlow<String?>(null)

    // AI Chat
    val chatMessages = MutableStateFlow<List<ChatMessage>>(
        listOf(
            ChatMessage("AI", "Hello! Welcome to Three Brothers official store. I am your AI assistant. How can I help you today?")
        )
    )
    val isAiThinking = MutableStateFlow(false)

    // AI Forecast & Admin Tools
    val aiForecastText = MutableStateFlow<String?>(null)
    val aiFraudResult = MutableStateFlow<String?>(null)
    val aiGeneratedDesc = MutableStateFlow<String?>(null)

    // Data Flows
    val allProducts: StateFlow<List<ProductEntity>> = repository.allProducts.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5000),
        initialValue = emptyList()
    )

    val adminProducts: StateFlow<List<ProductEntity>> = repository.adminProducts.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5000),
        initialValue = emptyList()
    )

    val featuredProducts: StateFlow<List<ProductEntity>> = repository.featuredProducts.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5000),
        initialValue = emptyList()
    )

    val wishlistItems: StateFlow<List<WishlistItemEntity>> = repository.wishlistItems.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5000),
        initialValue = emptyList()
    )

    val allOrders: StateFlow<List<OrderEntity>> = repository.allOrders.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5000),
        initialValue = emptyList()
    )

    val userProfile: StateFlow<UserProfileEntity?> = repository.userProfile.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5000),
        initialValue = null
    )

    // Filtered Products
    val filteredProducts: StateFlow<List<ProductEntity>> = combine(
        allProducts,
        selectedCategory,
        searchQuery,
        sortOption
    ) { products, category, query, sort ->
        var list = products

        if (category != "All") {
            list = list.filter { it.category.equals(category, ignoreCase = true) }
        }

        if (query.isNotBlank()) {
            val q = query.trim().lowercase()
            list = list.filter {
                it.name.lowercase().contains(q) ||
                it.description.lowercase().contains(q) ||
                it.tags.lowercase().contains(q) ||
                it.brand.lowercase().contains(q)
            }
        }

        when (sort) {
            "PriceLowHigh" -> list.sortedBy { it.discountPrice ?: it.price }
            "PriceHighLow" -> list.sortedByDescending { it.discountPrice ?: it.price }
            "Rating" -> list.sortedByDescending { it.rating }
            else -> list.sortedByDescending { if (it.isFeatured) 1 else 0 }
        }
    }.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5000),
        initialValue = emptyList()
    )

    // Combined Cart Summary
    val cartSummary: StateFlow<CartSummary> = combine(
        repository.cartItems,
        allProducts,
        appliedCoupon
    ) { cartItems, products, coupon ->
        val itemsWithProducts = cartItems.mapNotNull { cartItem ->
            val product = products.find { it.id == cartItem.productId }
            if (product != null) CartItemWithProduct(cartItem, product) else null
        }

        val subtotal = itemsWithProducts.sumOf { item ->
            val p = item.product.discountPrice ?: item.product.price
            p * item.cartItem.quantity
        }

        var discount = 0.0
        if (coupon != null && subtotal >= coupon.minOrderAmount) {
            discount = (subtotal * (coupon.discountPercentage / 100.0)).coerceAtMost(coupon.maxDiscount)
        }

        val delivery = if (itemsWithProducts.isEmpty()) 0.0 else 150.0
        val total = (subtotal - discount + delivery).coerceAtLeast(0.0)

        CartSummary(
            items = itemsWithProducts,
            subtotal = subtotal,
            discountAmount = discount,
            deliveryFee = delivery,
            total = total,
            appliedCoupon = coupon
        )
    }.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5000),
        initialValue = CartSummary(emptyList(), 0.0, 0.0, 0.0, 0.0, null)
    )

    // Cart Actions
    fun addToCart(productId: Long, size: String = "", color: String = "") {
        viewModelScope.launch {
            repository.addToCart(productId, size, color)
        }
    }

    fun updateCartQuantity(productId: Long, quantity: Int) {
        viewModelScope.launch {
            repository.updateCartQuantity(productId, quantity)
        }
    }

    fun removeFromCart(productId: Long) {
        viewModelScope.launch {
            repository.removeFromCart(productId)
        }
    }

    fun toggleWishlist(productId: Long) {
        viewModelScope.launch {
            repository.toggleWishlist(productId)
        }
    }

    fun applyCoupon(code: String) {
        viewModelScope.launch {
            val coupon = repository.validateCoupon(code)
            if (coupon != null) {
                appliedCoupon.value = coupon
                couponMessage.value = "Coupon '${coupon.code}' applied successfully! (${coupon.discountPercentage}% OFF)"
            } else {
                couponMessage.value = "Invalid or expired coupon code."
            }
        }
    }

    fun addReview(productId: Long, author: String, rating: Float, text: String) {
        viewModelScope.launch {
            repository.addReview(
                ProductReviewEntity(
                    productId = productId,
                    customerName = author,
                    rating = rating,
                    reviewText = text
                )
            )
        }
    }

    // Checkout
    fun placeOrder(
        customerName: String,
        phone: String,
        email: String,
        address: String,
        paymentMethod: String,
        onSuccess: (String) -> Unit
    ) {
        viewModelScope.launch {
            val currentCart = cartSummary.value
            if (currentCart.items.isEmpty()) return@launch

            val orderId = "TB-ORD-" + (10000..99999).random()
            val summaryText = currentCart.items.joinToString(", ") {
                "${it.cartItem.quantity}x ${it.product.name}"
            }

            val order = OrderEntity(
                orderId = orderId,
                customerName = customerName,
                customerPhone = phone,
                customerEmail = email,
                shippingAddress = address,
                paymentMethod = paymentMethod,
                totalAmount = currentCart.subtotal,
                discountAmount = currentCart.discountAmount,
                deliveryCharge = currentCart.deliveryFee,
                finalAmount = currentCart.total,
                status = "Pending",
                trackingNumber = "TRK-" + (1000..9999).random(),
                itemsSummary = summaryText,
                createdAt = System.currentTimeMillis()
            )

            repository.placeOrder(order)
            appliedCoupon.value = null
            onSuccess(orderId)
        }
    }

    // AI Chat Support
    fun sendAiMessage(text: String) {
        if (text.isBlank()) return
        val userMsg = ChatMessage("User", text)
        chatMessages.value = chatMessages.value + userMsg
        isAiThinking.value = true

        viewModelScope.launch {
            val catalogSummary = allProducts.value.take(10).joinToString("\n") {
                "- ${it.name} (${it.category}): PKR ${it.discountPrice ?: it.price}"
            }
            val reply = GeminiAiService.chatWithSupport(text, catalogSummary)
            isAiThinking.value = false
            chatMessages.value = chatMessages.value + ChatMessage("AI", reply)
        }
    }

    // AI Forecast for Admin
    fun loadAiForecast() {
        viewModelScope.launch {
            val summary = "Orders: ${allOrders.value.size}, Total Products: ${allProducts.value.size}"
            aiForecastText.value = GeminiAiService.getAiSalesForecast(summary)
        }
    }

    fun generateProductCopy(name: String, cat: String) {
        viewModelScope.launch {
            aiGeneratedDesc.value = GeminiAiService.autoGenerateProductDetails(name, cat)
        }
    }

    // Admin Operations
    fun saveProduct(product: ProductEntity) {
        viewModelScope.launch {
            if (product.id == 0L) {
                repository.addProduct(product)
            } else {
                repository.updateProduct(product)
            }
        }
    }

    fun deleteProduct(id: Long) {
        viewModelScope.launch {
            repository.deleteProduct(id)
        }
    }

    fun updateOrderStatus(orderId: String, status: String) {
        viewModelScope.launch {
            repository.updateOrderStatus(orderId, status)
        }
    }

    // WhatsApp Contact Helper
    fun openWhatsAppChat() {
        try {
            val phone = "923472065158" // 0347 206 5158 formatted with country code
            val url = "https://api.whatsapp.com/send?phone=$phone&text=" + Uri.encode("Hello Three Brothers, I have an inquiry regarding products.")
            val intent = Intent(Intent.ACTION_VIEW, Uri.parse(url))
            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
            getApplication<Application>().startActivity(intent)
        } catch (e: Exception) {
            // Fallback to phone dialer
            val dialIntent = Intent(Intent.ACTION_DIAL, Uri.parse("tel:03472065158"))
            dialIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
            getApplication<Application>().startActivity(dialIntent)
        }
    }
}
