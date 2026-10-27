package com.eec.core.repository

import com.eec.core.db.AppDatabase
import com.eec.core.db.CategorySeeder
import com.eec.core.db.createInMemoryDatabase
import com.eec.core.model.Category
import com.eec.core.model.TransactionKind
import kotlin.test.AfterTest
import kotlin.test.BeforeTest
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFalse
import kotlin.test.assertIs
import kotlin.test.assertNotNull
import kotlin.test.assertNull
import kotlin.test.assertTrue
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.runBlocking

class CategoryRepositoryTest {
    private lateinit var database: AppDatabase
    private lateinit var repository: CategoryRepository

    @BeforeTest
    fun setUp() {
        database = createInMemoryDatabase()
        repository = CategoryRepository(database)
    }

    @AfterTest
    fun tearDown() {
        database.close()
    }

    @Test
    fun createsCategoryWithDefaults() = runBlocking {
        val result = repository.create(name = "  قهوة  ", kind = TransactionKind.EXPENSE, colorKey = "plum")

        val category = assertIs<WriteResult.Success<Category>>(result).value
        assertEquals("قهوة", category.name)
        assertEquals(TransactionKind.EXPENSE, category.kind)
        assertEquals("plum", category.colorKey)
        assertEquals(CategoryRepository.DEFAULT_ICON_KEY, category.iconKey)
        assertEquals(1, category.sortOrder)
        assertFalse(category.archived)
        assertEquals(listOf(category.id), repository.all.first().map { it.id })
    }

    @Test
    fun renamesAndRecolorsCategory() = runBlocking {
        CategorySeeder(database).seed()

        val result = repository.update(id = "food", name = "مطاعم", colorKey = "red")

        assertIs<WriteResult.Success<Category>>(result)
        val updated = assertNotNull(repository.byId("food"))
        assertEquals("مطاعم", updated.name)
        assertEquals("red", updated.colorKey)
        assertEquals("cash", updated.iconKey)
        assertEquals(1, updated.sortOrder)
        assertFalse(updated.archived)
    }

    @Test
    fun archivesAndRestoresCategory() = runBlocking {
        CategorySeeder(database).seed()

        assertIs<WriteResult.Success<Unit>>(repository.setArchived(id = "food", archived = true))
        assertTrue(repository.active(TransactionKind.EXPENSE).none { it.id == "food" })
        assertTrue(assertNotNull(repository.byId("food")).archived)

        assertIs<WriteResult.Success<Unit>>(repository.setArchived(id = "food", archived = false))
        assertTrue(repository.active(TransactionKind.EXPENSE).any { it.id == "food" })
        assertFalse(assertNotNull(repository.byId("food")).archived)
    }

    @Test
    fun rejectsInvalidCreateWithoutWriting() = runBlocking {
        val blankName = repository.create(name = "   ", kind = TransactionKind.EXPENSE, colorKey = "teal")
        val longName = repository.create(
            name = "x".repeat(CategoryRepository.NAME_MAX_LENGTH + 1),
            kind = TransactionKind.EXPENSE,
            colorKey = "teal",
        )
        val blankColor = repository.create(name = "قهوة", kind = TransactionKind.EXPENSE, colorKey = " ")

        val nameError = assertIs<WriteResult.Failure>(blankName).error
        assertIs<RepositoryError.Validation>(nameError)
        assertEquals(ValidationField.CATEGORY_NAME, nameError.field)
        assertEquals(ValidationReason.REQUIRED, nameError.reason)

        val longError = assertIs<RepositoryError.Validation>(assertIs<WriteResult.Failure>(longName).error)
        assertEquals(ValidationReason.TOO_LONG, longError.reason)

        val colorError = assertIs<RepositoryError.Validation>(assertIs<WriteResult.Failure>(blankColor).error)
        assertEquals(ValidationField.CATEGORY_COLOR, colorError.field)

        assertTrue(repository.allOnce().isEmpty())
    }

    @Test
    fun acceptsHexColorAndNormalizesCase() = runBlocking {
        val result = repository.create(
            name = "قهوة",
            kind = TransactionKind.EXPENSE,
            colorKey = "#0ea5e9",
        )

        val category = assertIs<WriteResult.Success<Category>>(result).value
        assertEquals("#0EA5E9", category.colorKey)
        assertEquals("#0EA5E9", assertNotNull(repository.byId(category.id)).colorKey)
    }

    @Test
    fun rejectsMalformedHexWithoutWriting() = runBlocking {
        val malformed = listOf("#12345", "#GGGGGG", "#0EA5E")
        malformed.forEach { colorKey ->
            val result = repository.create(
                name = "قهوة",
                kind = TransactionKind.EXPENSE,
                colorKey = colorKey,
            )
            val error = assertIs<RepositoryError.Validation>(
                assertIs<WriteResult.Failure>(result).error,
            )
            assertEquals(ValidationField.CATEGORY_COLOR, error.field)
            assertEquals(ValidationReason.INVALID_FORMAT, error.reason)
        }

        assertTrue(repository.allOnce().isEmpty())
    }

    @Test
    fun reportsNotFoundForUnknownCategory() = runBlocking {

        val update = repository.update(id = "missing", name = "قهوة", colorKey = "teal")
        val archive = repository.setArchived(id = "missing", archived = true)

        assertIs<RepositoryError.NotFound>(assertIs<WriteResult.Failure>(update).error)
        assertIs<RepositoryError.NotFound>(assertIs<WriteResult.Failure>(archive).error)
        assertNull(repository.byId("missing"))
    }
}
