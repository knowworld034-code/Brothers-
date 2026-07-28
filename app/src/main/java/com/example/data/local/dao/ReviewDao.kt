package com.example.data.local.dao

import androidx.room.*
import com.example.data.local.entity.ProductReviewEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface ReviewDao {
    @Query("SELECT * FROM product_reviews WHERE productId = :productId ORDER BY id DESC")
    fun getReviewsForProduct(productId: Long): Flow<List<ProductReviewEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertReview(review: ProductReviewEntity)
}
