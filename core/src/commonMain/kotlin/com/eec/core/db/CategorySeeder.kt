package com.eec.core.db

import androidx.room3.withWriteTransaction

object SeedCategories {
    val all: List<CategoryEntity> = listOf(
        expense(
            id = "food",
            name = "طعام",
            colorKey = "teal",
            iconKey = "cash",
            sortOrder = 1,
        ),
        expense(
            id = "transport",
            name = "مواصلات",
            colorKey = "gold",
            iconKey = "transfer",
            sortOrder = 2,
        ),
        expense(
            id = "housing",
            name = "سكن",
            colorKey = "rust",
            iconKey = "dashboard",
            sortOrder = 3,
        ),
        expense(
            id = "bills",
            name = "فواتير",
            colorKey = "green",
            iconKey = "note",
            sortOrder = 4,
        ),
        expense(
            id = "health",
            name = "صحة",
            colorKey = "red",
            iconKey = "add",
            sortOrder = 5,
        ),
        expense(
            id = "education",
            name = "تعليم",
            colorKey = "slate",
            iconKey = "reports",
            sortOrder = 6,
        ),
        expense(
            id = "shopping",
            name = "تسوق",
            colorKey = "plum",
            iconKey = "card",
            sortOrder = 7,
        ),
        expense(
            id = "entertainment",
            name = "ترفيه",
            colorKey = "olive",
            iconKey = "wallet",
            sortOrder = 8,
        ),
        expense(
            id = "other_expense",
            name = "أخرى",
            colorKey = "slate",
            iconKey = "transactions",
            sortOrder = 9,
        ),
        income(
            id = "salary",
            name = "راتب",
            colorKey = "green",
            iconKey = "cash",
            sortOrder = 10,
        ),
        income(
            id = "freelance",
            name = "عمل حر",
            colorKey = "teal",
            iconKey = "note",
            sortOrder = 11,
        ),
        income(
            id = "gifts",
            name = "هدايا",
            colorKey = "gold",
            iconKey = "add",
            sortOrder = 12,
        ),
        income(
            id = "other_income",
            name = "أخرى",
            colorKey = "slate",
            iconKey = "wallet",
            sortOrder = 13,
        ),
    )

    private fun expense(id: String, name: String, colorKey: String, iconKey: String, sortOrder: Int) =
        CategoryEntity(
            id = id,
            name = name,
            kind = "EXPENSE",
            colorKey = colorKey,
            iconKey = iconKey,
            sortOrder = sortOrder,
            archived = false,
        )

    private fun income(id: String, name: String, colorKey: String, iconKey: String, sortOrder: Int) =
        CategoryEntity(
            id = id,
            name = name,
            kind = "INCOME",
            colorKey = colorKey,
            iconKey = iconKey,
            sortOrder = sortOrder,
            archived = false,
        )
}

class CategorySeeder(private val database: AppDatabase) {
    suspend fun seed() {
        database.withWriteTransaction {
            database.categoryDao().insertAll(SeedCategories.all)
        }
    }
}
