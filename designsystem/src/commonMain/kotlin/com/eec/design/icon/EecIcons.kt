package com.eec.design.icon

import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.StrokeJoin
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.graphics.vector.PathParser
import androidx.compose.ui.unit.dp

private fun lineIcon(
    name: String,
    pathData: String,
    autoMirror: Boolean = false,
): ImageVector = ImageVector.Builder(
    name = name,
    defaultWidth = 24.dp,
    defaultHeight = 24.dp,
    viewportWidth = 24f,
    viewportHeight = 24f,
    autoMirror = autoMirror,
).addPath(
    pathData = PathParser().parsePathString(pathData).toNodes(),
    fill = null,
    stroke = SolidColor(Color.Black),
    strokeLineWidth = 2f,
    strokeLineCap = StrokeCap.Round,
    strokeLineJoin = StrokeJoin.Round,
).build()

object EecIcons {
    val dashboard: ImageVector = lineIcon(
        name = "eec.dashboard",
        pathData = "M4 4h6v6H4z M14 4h6v6h-6z M4 14h6v6H4z M14 14h6v6h-6z",
    )

    val transactions: ImageVector = lineIcon(
        name = "eec.transactions",
        pathData = "M4 6h16 M4 12h16 M4 18h10",
    )

    val reports: ImageVector = lineIcon(
        name = "eec.reports",
        pathData = "M5 20V10 M12 20V4 M19 20v-7 M3 20h18",
    )

    val settings: ImageVector = lineIcon(
        name = "eec.settings",
        pathData = "M4 7h16 M4 12h16 M4 17h16 M7 7m-2 0a2 2 0 1 0 4 0a2 2 0 1 0 -4 0 " +
            "M15 12m-2 0a2 2 0 1 0 4 0a2 2 0 1 0 -4 0 M9 17m-2 0a2 2 0 1 0 4 0a2 2 0 1 0 -4 0",
    )

    val add: ImageVector = lineIcon(
        name = "eec.add",
        pathData = "M12 5v14 M5 12h14",
    )

    val search: ImageVector = lineIcon(
        name = "eec.search",
        pathData = "M11 4a7 7 0 1 0 0 14a7 7 0 1 0 0 -14 M16.5 16.5L21 21",
    )

    val filter: ImageVector = lineIcon(
        name = "eec.filter",
        pathData = "M3 5h18l-7 8v6l-4 2v-8z",
    )

    val back: ImageVector = lineIcon(
        name = "eec.back",
        pathData = "M15 5l-7 7l7 7",
        autoMirror = true,
    )

    val calendar: ImageVector = lineIcon(
        name = "eec.calendar",
        pathData = "M4 6h16v14H4z M8 3v4 M16 3v4 M4 10h16",
    )

    val cash: ImageVector = lineIcon(
        name = "eec.cash",
        pathData = "M3 6h18v12H3z M7 12m-2 0a2 2 0 1 0 4 0a2 2 0 1 0 -4 0 " +
            "M16 12m-2 0a2 2 0 1 0 4 0a2 2 0 1 0 -4 0 M3 9h2 M19 15h2",
    )

    val card: ImageVector = lineIcon(
        name = "eec.card",
        pathData = "M3 6h18v12H3z M3 10h18 M7 15h4",
    )

    val wallet: ImageVector = lineIcon(
        name = "eec.wallet",
        pathData = "M4 7h14a2 2 0 0 1 2 2v8H4z M4 7v10 M18 11h2v4h-2z",
    )

    val transfer: ImageVector = lineIcon(
        name = "eec.transfer",
        pathData = "M4 8h14 M14 4l4 4l-4 4 M20 16H6 M10 12l-4 4l4 4",
        autoMirror = true,
    )

    val note: ImageVector = lineIcon(
        name = "eec.note",
        pathData = "M4 20h4L20 8l-4-4L4 16z M14 6l4 4",
    )

    val delete: ImageVector = lineIcon(
        name = "eec.delete",
        pathData = "M4 7h16 M9 7V4h6v3 M6 7l1 13h10l1-13 M10 11v6 M14 11v6",
    )

    val close: ImageVector = lineIcon(
        name = "eec.close",
        pathData = "M6 6l12 12 M18 6L6 18",
    )

    val all: List<ImageVector> = listOf(
        dashboard,
        transactions,
        reports,
        settings,
        add,
        search,
        filter,
        back,
        calendar,
        cash,
        card,
        wallet,
        transfer,
        note,
        delete,
        close,
    )

    private val byKey: Map<String, ImageVector> = mapOf(
        "dashboard" to dashboard,
        "transactions" to transactions,
        "reports" to reports,
        "settings" to settings,
        "add" to add,
        "search" to search,
        "filter" to filter,
        "back" to back,
        "calendar" to calendar,
        "cash" to cash,
        "card" to card,
        "wallet" to wallet,
        "transfer" to transfer,
        "note" to note,
        "delete" to delete,
        "close" to close,
    )

    fun byKey(key: String): ImageVector? = byKey[key]

    fun categoryIcon(key: String): ImageVector = byKey[key] ?: transactions
}
