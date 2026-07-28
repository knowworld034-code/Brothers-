package com.example.data.repository

import com.example.data.local.ThreeBrothersDatabase
import com.example.data.local.entity.*
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.map

class EcommerceRepository(private val db: ThreeBrothersDatabase) {

    private val productDao = db.productDao()
    private val cartDao = db.cartDao()
    private val wishlistDao = db.wishlistDao()
    private val orderDao = db.orderDao()
    private val userDao = db.userProfileDao()
    private val couponDao = db.couponDao()
    private val reviewDao = db.reviewDao()

    // Products
    val allProducts: Flow<List<ProductEntity>> = productDao.getAllProducts()
    val adminProducts: Flow<List<ProductEntity>> = productDao.getAllProductsAdmin()
    val featuredProducts: Flow<List<ProductEntity>> = productDao.getFeaturedProducts()

    fun getProductById(id: Long): Flow<ProductEntity?> = productDao.getProductById(id)
    fun getProductsByCategory(category: String): Flow<List<ProductEntity>> = productDao.getProductsByCategory(category)
    fun searchProducts(query: String): Flow<List<ProductEntity>> = productDao.searchProducts(query)

    suspend fun addProduct(product: ProductEntity): Long = productDao.insertProduct(product)
    suspend fun updateProduct(product: ProductEntity) = productDao.updateProduct(product)
    suspend fun deleteProduct(id: Long) = productDao.deleteProductById(id)
    suspend fun incrementProductView(id: Long) = productDao.incrementViewCount(id)

    // Cart
    val cartItems: Flow<List<CartItemEntity>> = cartDao.getCartItems()

    suspend fun addToCart(productId: Long, size: String = "", color: String = "") {
        cartDao.insertCartItem(CartItemEntity(productId = productId, quantity = 1, selectedSize = size, selectedColor = color))
    }

    suspend fun updateCartQuantity(productId: Long, quantity: Int) {
        if (quantity <= 0) {
            cartDao.deleteCartItem(productId)
        } else {
            cartDao.updateQuantity(productId, quantity)
        }
    }

    suspend fun removeFromCart(productId: Long) = cartDao.deleteCartItem(productId)
    suspend fun clearCart() = cartDao.clearCart()

    // Wishlist
    val wishlistItems: Flow<List<WishlistItemEntity>> = wishlistDao.getWishlistItems()
    fun isWishlisted(productId: Long): Flow<Boolean> = wishlistDao.isWishlisted(productId)

    suspend fun toggleWishlist(productId: Long) {
        val currentWishlist = wishlistDao.getWishlistItems().first()
        val exists = currentWishlist.any { it.productId == productId }
        if (exists) {
            wishlistDao.deleteWishlist(productId)
        } else {
            wishlistDao.insertWishlist(WishlistItemEntity(productId = productId))
        }
    }

    // Orders
    val allOrders: Flow<List<OrderEntity>> = orderDao.getAllOrders()
    fun getOrderById(orderId: String): Flow<OrderEntity?> = orderDao.getOrderById(orderId)

    suspend fun placeOrder(order: OrderEntity) {
        orderDao.insertOrder(order)
        clearCart()
    }

    suspend fun updateOrderStatus(orderId: String, newStatus: String) {
        orderDao.updateOrderStatus(orderId, newStatus)
    }

    // User Profile
    val userProfile: Flow<UserProfileEntity?> = userDao.getUserProfile()
    suspend fun updateUserProfile(profile: UserProfileEntity) = userDao.insertUserProfile(profile)

    // Coupons
    val allCoupons: Flow<List<CouponEntity>> = couponDao.getAllCoupons()
    suspend fun validateCoupon(code: String): CouponEntity? = couponDao.getCouponByCode(code.trim().uppercase())
    suspend fun addCoupon(coupon: CouponEntity) = couponDao.insertCoupon(coupon)

    // Reviews
    fun getReviewsForProduct(productId: Long): Flow<List<ProductReviewEntity>> = reviewDao.getReviewsForProduct(productId)
    suspend fun addReview(review: ProductReviewEntity) = reviewDao.insertReview(review)
}
