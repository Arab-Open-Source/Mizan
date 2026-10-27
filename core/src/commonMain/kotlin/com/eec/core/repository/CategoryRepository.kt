package com.eec.core.repository

import com.eec.core.db.AppDatabase
import com.eec.core.db.CategoryEntity
import com.eec.core.model.Category
import com.eec.core.model.TransactionKind
import kotlin.uuid.ExperimentalUuidApi
import kotlin.uuid.Uuid
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

class CategoryRepository(database: AppDatabase) {
    private val categoryDao = database.categoryDao()

    val all: Flow<List<Category>> = categoryDao.observeAll().map { rows -> rows.map { it.toDomain() } }

    suspend fun allOnce(): List<Category> = categoryDao.all().map { it.toDomain() }

    suspend fun byId(id: String): Category? = categoryDao.byId(id)?.toDomain()

    suspend fun active(kind: TransactionKind): List<Category> = allOnce()
        .filter { it.kind == kind && !it.archived }

    suspend fun create(
        name: String,
        kind: TransactionKind,
        colorKey: String,
        iconKey: String = DEFAULT_ICON_KEY,
    ): WriteResult<Category> {
        val trimmedName = name.trim()
        val normalizedColor = normalizeColorKey(colorKey)
        validate(trimmedName, normalizedColor)?.let { return WriteResult.Failure(it) }
        return try {
            val sortOrder = (categoryDao.all().maxOfOrNull { it.sortOrder } ?: 0) + 1
            val entity = CategoryEntity(
                id = newId(),
                name = trimmedName,
                kind = kind.name,
                colorKey = normalizedColor,
                iconKey = iconKey,
                sortOrder = sortOrder,
                archived = false,
            )
            categoryDao.insert(entity)
            WriteResult.Success(entity.toDomain())
        } catch (cancellation: CancellationException) {
            throw cancellation
        } catch (error: Exception) {
            WriteResult.Failure(RepositoryError.Storage(error))
        }
    }

    suspend fun update(id: String, name: String, colorKey: String): WriteResult<Category> {
        val trimmedName = name.trim()
        val normalizedColor = normalizeColorKey(colorKey)
        validate(trimmedName, normalizedColor)?.let { return WriteResult.Failure(it) }
        return try {
            if (categoryDao.byId(id) == null) {
                return WriteResult.Failure(RepositoryError.NotFound(id))
            }
            if (categoryDao.updateNameAndColor(id = id, name = trimmedName, colorKey = normalizedColor) == 0) {
                WriteResult.Failure(RepositoryError.NotFound(id))
            } else {
                val updated = categoryDao.byId(id)
                    ?: return WriteResult.Failure(RepositoryError.NotFound(id))
                WriteResult.Success(updated.toDomain())
            }
        } catch (cancellation: CancellationException) {
            throw cancellation
        } catch (error: Exception) {
            WriteResult.Failure(RepositoryError.Storage(error))
        }
    }

    suspend fun setArchived(id: String, archived: Boolean): WriteResult<Unit> = try {
        if (categoryDao.setArchived(id = id, archived = archived) == 0) {
            WriteResult.Failure(RepositoryError.NotFound(id))
        } else {
            WriteResult.Success(Unit)
        }
    } catch (cancellation: CancellationException) {
        throw cancellation
    } catch (error: Exception) {
        WriteResult.Failure(RepositoryError.Storage(error))
    }

    private fun validate(name: String, colorKey: String): RepositoryError? = when {
        name.isBlank() ->
            RepositoryError.Validation(ValidationField.CATEGORY_NAME, ValidationReason.REQUIRED)

        name.length > NAME_MAX_LENGTH ->
            RepositoryError.Validation(ValidationField.CATEGORY_NAME, ValidationReason.TOO_LONG)

        colorKey.isBlank() ->
            RepositoryError.Validation(ValidationField.CATEGORY_COLOR, ValidationReason.REQUIRED)

        !isValidColorKey(colorKey) ->
            RepositoryError.Validation(ValidationField.CATEGORY_COLOR, ValidationReason.INVALID_FORMAT)

        else -> null
    }

    private fun normalizeColorKey(colorKey: String): String {
        val trimmed = colorKey.trim()
        return if (trimmed.startsWith("#")) trimmed.uppercase() else trimmed
    }

    private fun isValidColorKey(colorKey: String): Boolean =
        !colorKey.startsWith("#") || HEX_COLOR.matches(colorKey)

    @OptIn(ExperimentalUuidApi::class)
    private fun newId(): String = Uuid.random().toString()

    companion object {
        const val NAME_MAX_LENGTH: Int = 40
        const val DEFAULT_ICON_KEY: String = "note"

        private val HEX_COLOR = Regex("^#[0-9A-F]{6}$")
    }
}
