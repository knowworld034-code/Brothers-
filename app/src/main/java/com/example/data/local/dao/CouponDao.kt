package com.example.data.local.dao

import androidx.room.*
import com.example.data.local.entity.CouponEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface CouponDao {
    @Query("SELECT * FROM coupons WHERE code = :code AND isExpired = 0")
    suspend fun getCouponByCode(code: String): CouponEntity?

    @Query("SELECT * FROM coupons")
    fun getAllCoupons(): Flow<List<CouponEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertCoupon(coupon: CouponEntity)
}
