package com.example.data.local

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.sqlite.db.SupportSQLiteDatabase
import com.example.data.local.dao.*
import com.example.data.local.entity.*
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

@Database(
    entities = [
        ProductEntity::class,
        CartItemEntity::class,
        WishlistItemEntity::class,
        OrderEntity::class,
        UserProfileEntity::class,
        CouponEntity::class,
        ProductReviewEntity::class
    ],
    version = 1,
    exportSchema = false
)
abstract class ThreeBrothersDatabase : RoomDatabase() {

    abstract fun productDao(): ProductDao
    abstract fun cartDao(): CartDao
    abstract fun wishlistDao(): WishlistDao
    abstract fun orderDao(): OrderDao
    abstract fun userProfileDao(): UserProfileDao
    abstract fun couponDao(): CouponDao
    abstract fun reviewDao(): ReviewDao

    companion object {
        @Volatile
        private var INSTANCE: ThreeBrothersDatabase? = null

        fun getDatabase(context: Context): ThreeBrothersDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    ThreeBrothersDatabase::class.java,
                    "three_brothers_db"
                )
                .fallbackToDestructiveMigration()
                .addCallback(DatabaseCallback(context))
                .build()
                INSTANCE = instance
                instance
            }
        }

        private class DatabaseCallback(private val context: Context) : RoomDatabase.Callback() {
            override fun onCreate(db: SupportSQLiteDatabase) {
                super.onCreate(db)
                INSTANCE?.let { database ->
                    CoroutineScope(Dispatchers.IO).launch {
                        populateInitialData(database)
                    }
                }
            }
        }

        private suspend fun populateInitialData(db: ThreeBrothersDatabase) {
            val productDao = db.productDao()
            val couponDao = db.couponDao()
            val userDao = db.userProfileDao()
            val orderDao = db.orderDao()

            // Pre-populate initial user profile
            userDao.insertUserProfile(
                UserProfileEntity(
                    id = 1,
                    name = "Farhan Nadeem",
                    email = "Mrbast@gmail.com",
                    phone = "03472065158",
                    address = "Plot 45-B, Commercial Area, Gulberg III, Lahore",
                    city = "Lahore",
                    postalCode = "54000",
                    isLoggedIn = true
                )
            )

            // Pre-populate Coupons
            couponDao.insertCoupon(
                CouponEntity(
                    code = "THREEB10",
                    discountPercentage = 10.0,
                    maxDiscount = 1000.0,
                    minOrderAmount = 2000.0,
                    description = "Get 10% OFF on orders over PKR 2,000"
                )
            )
            couponDao.insertCoupon(
                CouponEntity(
                    code = "WELCOME20",
                    discountPercentage = 20.0,
                    maxDiscount = 2500.0,
                    minOrderAmount = 5000.0,
                    description = "Special 20% WELCOME discount for new customers"
                )
            )

            // Pre-populate Products
            val sampleProducts = listOf(
                ProductEntity(
                    id = 1,
                    name = "Luxury Leather Oxford Shoes",
                    category = "Shoes",
                    brand = "Three Brothers Premium",
                    imageUrl = "https://images.unsplash.com/photo-1549298916-b41d501d3772?auto=format&fit=crop&w=600&q=80",
                    description = "Handcrafted genuine leather formal shoes designed for comfort and durability. Cushioned sole and sleek finish.",
                    shortDescription = "Genuine handcrafted leather Oxford shoes",
                    price = 6500.0,
                    discountPrice = 5200.0,
                    stockQuantity = 45,
                    sku = "TB-SH-001",
                    sizes = "39, 40, 41, 42, 43, 44",
                    colors = "Brown, Black, Tan",
                    weight = "0.9 kg",
                    specifications = "Upper: 100% Genuine Leather, Sole: Soft Rubber Grip",
                    tags = "Shoes, Leather, Formal, Featured, BestSeller",
                    deliveryCharges = 200.0,
                    warranty = "1 Year Leather Warranty",
                    returnPolicy = "7 Days Easy Return & Exchange",
                    isPublished = true,
                    isFeatured = true,
                    rating = 4.9f,
                    reviewCount = 38
                ),
                ProductEntity(
                    id = 2,
                    name = "Comfort Soft Leather Slippers",
                    category = "Slippers",
                    brand = "Three Brothers Comfort",
                    imageUrl = "https://images.unsplash.com/photo-1603808033192-082d6919d3e1?auto=format&fit=crop&w=600&q=80",
                    description = "Ultra-comfortable daily wear slippers with soft memory foam insoles and anti-slip rubber outsole.",
                    shortDescription = "Memory foam daily casual slippers",
                    price = 2200.0,
                    discountPrice = 1750.0,
                    stockQuantity = 80,
                    sku = "TB-SL-002",
                    sizes = "40, 41, 42, 43, 44",
                    colors = "Navy, Grey, Black",
                    weight = "0.4 kg",
                    specifications = "Insole: Memory Foam, Sole: Anti-slip Rubber",
                    tags = "Slippers, Casual, Comfort, BestSeller",
                    deliveryCharges = 150.0,
                    warranty = "30 Days Warranty",
                    returnPolicy = "7 Days Return",
                    isPublished = true,
                    isFeatured = true,
                    rating = 4.8f,
                    reviewCount = 24
                ),
                ProductEntity(
                    id = 3,
                    name = "Premium Wash & Wear Shalwar Kameez",
                    category = "Clothing",
                    brand = "Three Brothers Ethnic",
                    imageUrl = "https://images.unsplash.com/photo-1594938298603-c8148c4dae35?auto=format&fit=crop&w=600&q=80",
                    description = "High quality wash-and-wear fabric suit with immaculate stitching, cuff buttons, and comfortable mandarin collar.",
                    shortDescription = "Designer Wash & Wear unstitched/stitched suit",
                    price = 4800.0,
                    discountPrice = 3990.0,
                    stockQuantity = 30,
                    sku = "TB-CL-003",
                    sizes = "S, M, L, XL",
                    colors = "Royal Navy, Cream, Emerald, Charcoal",
                    weight = "0.6 kg",
                    specifications = "Fabric: Premium Micro-Fiber Wash & Wear",
                    tags = "Clothing, Ethnic, Shalwar Kameez, Featured",
                    deliveryCharges = 150.0,
                    warranty = "Color & Fabric Guaranteed",
                    returnPolicy = "7 Days Exchange",
                    isPublished = true,
                    isFeatured = true,
                    rating = 4.7f,
                    reviewCount = 19
                ),
                ProductEntity(
                    id = 4,
                    name = "ANC Wireless Noise Cancelling Headphones",
                    category = "Electronics",
                    brand = "Three Brothers Tech",
                    imageUrl = "https://images.unsplash.com/photo-1505740420928-5e560c06d30e?auto=format&fit=crop&w=600&q=80",
                    description = "Active Noise Cancelling over-ear bluetooth headphones with 40-hour battery life, deep bass, and HD microphone.",
                    shortDescription = "40H battery ANC Bluetooth headphones",
                    price = 12500.0,
                    discountPrice = 9900.0,
                    stockQuantity = 18,
                    sku = "TB-EL-004",
                    sizes = "Standard Adjustable",
                    colors = "Matte Black, Silver",
                    weight = "0.3 kg",
                    specifications = "Bluetooth 5.3, 40h Battery, Type-C Fast Charging",
                    tags = "Electronics, Wireless, Audio, Gadgets, Featured",
                    deliveryCharges = 250.0,
                    warranty = "1 Year Replacement Warranty",
                    returnPolicy = "7 Days Replacement",
                    isPublished = true,
                    isFeatured = true,
                    rating = 4.9f,
                    reviewCount = 52
                ),
                ProductEntity(
                    id = 5,
                    name = "Organic Extra Virgin Olive Oil 1L",
                    category = "Grocery",
                    brand = "Three Brothers Organics",
                    imageUrl = "https://images.unsplash.com/photo-1474979266404-7eaacbcd87c5?auto=format&fit=crop&w=600&q=80",
                    description = "100% pure cold-pressed extra virgin olive oil imported from Spain. Rich in antioxidants and healthy fats.",
                    shortDescription = "Cold pressed 100% pure extra virgin olive oil",
                    price = 3200.0,
                    discountPrice = 2850.0,
                    stockQuantity = 120,
                    sku = "TB-GR-005",
                    sizes = "1 Liter Bottle",
                    colors = "Amber Glass",
                    weight = "1.2 kg",
                    specifications = "100% Organic Cold Pressed Olive Oil",
                    tags = "Grocery, Olive Oil, Healthy, Organic",
                    deliveryCharges = 150.0,
                    warranty = "Freshness Guaranteed",
                    returnPolicy = "Return if Seal Unbroken",
                    isPublished = true,
                    isFeatured = false,
                    rating = 4.8f,
                    reviewCount = 15
                ),
                ProductEntity(
                    id = 6,
                    name = "E-Commerce Masterclass & Digital Toolkit",
                    category = "Digital",
                    brand = "Three Brothers Digital",
                    imageUrl = "https://images.unsplash.com/photo-1432888498266-38ffec3eaf0a?auto=format&fit=crop&w=600&q=80",
                    description = "Complete video course + downloadable spreadsheets and templates for running a high-converting online store.",
                    shortDescription = "Digital download course + business templates",
                    price = 1999.0,
                    discountPrice = 999.0,
                    stockQuantity = 9999,
                    sku = "TB-DG-006",
                    sizes = "Instant Digital Access",
                    colors = "N/A",
                    weight = "0 kg",
                    specifications = "PDF, Video MP4, Excel Spreadsheets",
                    tags = "Digital, Course, Templates, Instant Download",
                    deliveryCharges = 0.0,
                    warranty = "Lifetime Access",
                    returnPolicy = "Instant Digital Delivery",
                    isPublished = true,
                    isFeatured = true,
                    rating = 5.0f,
                    reviewCount = 42
                )
            )

            productDao.insertProducts(sampleProducts)

            // Pre-populate sample order for history tracking demonstration
            orderDao.insertOrder(
                OrderEntity(
                    orderId = "TB-ORD-88219",
                    customerName = "Farhan Nadeem",
                    customerPhone = "0347 206 5158",
                    customerEmail = "Mrbast@gmail.com",
                    shippingAddress = "House 12, Street 4, DHA Phase 5, Lahore",
                    paymentMethod = "EasyPaisa",
                    totalAmount = 5200.0,
                    discountAmount = 520.0,
                    deliveryCharge = 150.0,
                    finalAmount = 4830.0,
                    status = "Shipped",
                    trackingNumber = "EP-TRK-9921",
                    itemsSummary = "1x Luxury Leather Oxford Shoes (Size 42, Brown)",
                    createdAt = System.currentTimeMillis() - 86400000L
                )
            )
        }
    }
}
