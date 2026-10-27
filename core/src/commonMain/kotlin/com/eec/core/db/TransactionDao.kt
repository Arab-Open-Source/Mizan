package com.eec.core.db

import androidx.room3.Dao
import androidx.room3.Insert
import androidx.room3.OnConflictStrategy
import androidx.room3.Query
import androidx.room3.Update
import kotlinx.coroutines.flow.Flow

@Dao
interface TransactionDao {
    @Insert(onConflict = OnConflictStrategy.ABORT)
    suspend fun insert(transaction: TransactionEntity)

    @Update
    suspend fun update(transaction: TransactionEntity): Int

    @Query("DELETE FROM transactions WHERE id = :id")
    suspend fun deleteById(id: String): Int

    @Query("SELECT * FROM transactions WHERE id = :id")
    suspend fun byId(id: String): TransactionEntity?

    @Query(
        """
        SELECT t.id, t.kind, t.amount_minor, t.currency, t.category_id,
            c.name AS category_name, c.color_key AS category_color_key, c.icon_key AS category_icon_key,
            t.occurred_at, t.note, t.payment_method, t.created_at, t.updated_at
        FROM transactions t
        INNER JOIN categories c ON c.id = t.category_id
        ORDER BY t.occurred_at DESC, t.created_at DESC
        """,
    )
    fun observeAll(): Flow<List<TransactionWithCategoryRow>>

    @Query(
        """
        SELECT t.id, t.kind, t.amount_minor, t.currency, t.category_id,
            c.name AS category_name, c.color_key AS category_color_key, c.icon_key AS category_icon_key,
            t.occurred_at, t.note, t.payment_method, t.created_at, t.updated_at
        FROM transactions t
        INNER JOIN categories c ON c.id = t.category_id
        WHERE (:startInclusive IS NULL OR t.occurred_at >= :startInclusive)
            AND (:endExclusive IS NULL OR t.occurred_at < :endExclusive)
            AND (:kind IS NULL OR t.kind = :kind)
            AND (:categoryId IS NULL OR t.category_id = :categoryId)
            AND (:paymentMethod IS NULL OR t.payment_method = :paymentMethod)
            AND (
                :notePattern IS NULL
                OR t.note LIKE :notePattern ESCAPE '\'
                OR c.name LIKE :notePattern ESCAPE '\'
            )
        ORDER BY t.occurred_at DESC, t.created_at DESC
        """,
    )
    fun observeFiltered(
        startInclusive: Long?,
        endExclusive: Long?,
        kind: String?,
        categoryId: String?,
        paymentMethod: String?,
        notePattern: String?,
    ): Flow<List<TransactionWithCategoryRow>>

    @Query(
        """
        SELECT t.id, t.kind, t.amount_minor, t.currency, t.category_id,
            c.name AS category_name, c.color_key AS category_color_key, c.icon_key AS category_icon_key,
            t.occurred_at, t.note, t.payment_method, t.created_at, t.updated_at
        FROM transactions t
        INNER JOIN categories c ON c.id = t.category_id
        WHERE (:startInclusive IS NULL OR t.occurred_at >= :startInclusive)
            AND (:endExclusive IS NULL OR t.occurred_at < :endExclusive)
        ORDER BY t.occurred_at ASC, t.created_at ASC
        """,
    )
    suspend fun exportRows(startInclusive: Long?, endExclusive: Long?): List<TransactionWithCategoryRow>

    @Query(
        """
        SELECT kind AS kind, COALESCE(SUM(amount_minor), 0) AS total_minor
        FROM transactions
        WHERE occurred_at >= :startInclusive AND occurred_at < :endExclusive
        GROUP BY kind
        """,
    )
    suspend fun totalsByKind(startInclusive: Long, endExclusive: Long): List<KindTotalRow>

    @Query(
        """
        SELECT c.id AS category_id, c.name AS name, c.color_key AS color_key, c.icon_key AS icon_key,
            COALESCE(SUM(t.amount_minor), 0) AS total_minor
        FROM transactions t
        INNER JOIN categories c ON c.id = t.category_id
        WHERE t.occurred_at >= :startInclusive
            AND t.occurred_at < :endExclusive
            AND t.kind = :kind
        GROUP BY c.id, c.name, c.color_key, c.icon_key
        ORDER BY total_minor DESC, c.name ASC
        """,
    )
    suspend fun categoryTotals(
        kind: String,
        startInclusive: Long,
        endExclusive: Long,
    ): List<CategoryTotalRow>

    @Query(
        """
        SELECT ((occurred_at + 7200000) / 86400000) AS day_bucket,
            COALESCE(SUM(amount_minor), 0) AS total_minor
        FROM transactions
        WHERE kind = :kind
            AND occurred_at >= :startInclusive
            AND occurred_at < :endExclusive
        GROUP BY day_bucket
        """,
    )
    suspend fun dailyTotals(kind: String, startInclusive: Long, endExclusive: Long): List<DailyBucketRow>

    @Query(
        """
        SELECT COALESCE(SUM(amount_minor), 0)
        FROM transactions
        WHERE kind = :kind AND occurred_at >= :startInclusive AND occurred_at < :endExclusive
        """,
    )
    suspend fun totalBetween(kind: String, startInclusive: Long, endExclusive: Long): Long
}
