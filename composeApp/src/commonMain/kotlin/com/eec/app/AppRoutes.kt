package com.eec.app

import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.SnackbarHostState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberUpdatedState
import androidx.compose.ui.Modifier
import com.eec.app.categories.CategoryManagerScreen
import com.eec.app.categories.CategoryManagerScreenModel
import com.eec.app.dashboard.DashboardScreen
import com.eec.app.dashboard.DashboardScreenModel
import com.eec.app.di.AppContainer
import com.eec.app.editor.EditorTarget
import com.eec.app.editor.TransactionEditor
import com.eec.app.editor.TransactionEditorScreenModel
import com.eec.app.navigation.Screen
import com.eec.app.reports.ReportsScreen
import com.eec.app.reports.ReportsScreenModel
import com.eec.app.settings.SettingsScreen
import com.eec.app.settings.SettingsScreenModel
import com.eec.app.transactions.TransactionsScreen
import com.eec.app.transactions.TransactionsScreenModel
import com.eec.app.ui.rememberScreenScope
import com.eec.core.model.TransactionListItem
import com.eec.design.component.NavigationItem
import com.eec.design.icon.EecIcons

val navigationItems: List<NavigationItem> = listOf(
    NavigationItem(key = Screen.Dashboard.route, label = "الرئيسية", icon = EecIcons.dashboard),
    NavigationItem(key = Screen.Transactions.route, label = "المعاملات", icon = EecIcons.transactions),
    NavigationItem(key = Screen.Reports.route, label = "التقارير", icon = EecIcons.reports),
    NavigationItem(key = Screen.Settings.route, label = "الإعدادات", icon = EecIcons.settings),
)

fun Screen.navigationKey(): String = when (this) {
    Screen.Dashboard -> "dashboard"
    Screen.Transactions -> "transactions"
    Screen.Reports -> "reports"
    Screen.Settings -> "settings"
    Screen.Categories -> "settings"
}

fun Screen.title(): String = when (this) {
    Screen.Dashboard -> "الرئيسية"
    Screen.Transactions -> "المعاملات"
    Screen.Reports -> "التقارير"
    Screen.Settings -> "الإعدادات"
    Screen.Categories -> "التصنيفات"
}

fun navigationScreen(key: String): Screen = when (key) {
    "transactions" -> Screen.Transactions
    "reports" -> Screen.Reports
    "settings" -> Screen.Settings
    "categories" -> Screen.Categories
    else -> Screen.Dashboard
}

@Composable
fun DashboardRoute(
    container: AppContainer,
    onAdd: () -> Unit,
    onEdit: (TransactionListItem) -> Unit,
) {
    val scope = rememberScreenScope(container)
    val model = remember(container) { DashboardScreenModel(container, scope) }
    val state by model.state.collectAsState()
    DashboardScreen(
        state = state,
        onEvent = model::onEvent,
        onAdd = onAdd,
        onEdit = onEdit,
        modifier = Modifier.fillMaxSize(),
    )
}

@Composable
fun TransactionsRoute(
    container: AppContainer,
    snackbarHostState: SnackbarHostState,
    onAdd: () -> Unit,
    onEdit: (TransactionListItem) -> Unit,
) {
    val scope = rememberScreenScope(container)
    val model = remember(container) { TransactionsScreenModel(container, scope) }
    val state by model.state.collectAsState()
    TransactionsScreen(
        state = state,
        onEvent = model::onEvent,
        onEdit = onEdit,
        onAdd = onAdd,
        snackbarHostState = snackbarHostState,
        modifier = Modifier.fillMaxSize(),
    )
}

@Composable
fun ReportsRoute(
    container: AppContainer,
    snackbarHostState: SnackbarHostState,
    onAdd: () -> Unit,
) {
    val scope = rememberScreenScope(container)
    val model = remember(container) { ReportsScreenModel(container, scope) }
    val state by model.state.collectAsState()
    ReportsScreen(
        state = state,
        canShareFiles = container.shareService.canShareFiles(),
        onEvent = model::onEvent,
        onAdd = onAdd,
        snackbarHostState = snackbarHostState,
        modifier = Modifier.fillMaxSize(),
    )
}

@Composable
fun SettingsRoute(
    container: AppContainer,
    theme: ThemeMode,
    onThemeChange: (ThemeMode) -> Unit,
    onLockChanged: (Boolean) -> Unit,
    onOpenCategories: () -> Unit,
    snackbarHostState: SnackbarHostState,
) {
    val scope = rememberScreenScope(container)
    val currentOnLockChanged by rememberUpdatedState(onLockChanged)
    val model = remember(container) {
        SettingsScreenModel(container, scope) { enabled -> currentOnLockChanged(enabled) }
    }
    val state by model.state.collectAsState()
    SettingsScreen(
        state = state,
        theme = theme,
        canShareFiles = container.shareService.canShareFiles(),
        onThemeChange = onThemeChange,
        onOpenCategories = onOpenCategories,
        onEvent = model::onEvent,
        snackbarHostState = snackbarHostState,
        modifier = Modifier.fillMaxSize(),
    )
}

@Composable
fun CategoriesRoute(
    container: AppContainer,
    snackbarHostState: SnackbarHostState,
) {
    val scope = rememberScreenScope(container)
    val model = remember(container) { CategoryManagerScreenModel(container, scope) }
    val state by model.state.collectAsState()
    CategoryManagerScreen(
        state = state,
        onEvent = model::onEvent,
        snackbarHostState = snackbarHostState,
        modifier = Modifier.fillMaxSize(),
    )
}

@Composable
fun EditorRoute(
    container: AppContainer,
    target: EditorTarget,
    onClose: () -> Unit,
    onSaved: () -> Unit,
) {
    val scope = rememberScreenScope(container, key = target)
    val model = remember(target) {
        TransactionEditorScreenModel(
            container = container,
            scope = scope,
            target = target,
            onSaved = onSaved,
        )
    }
    val state by model.state.collectAsState()
    TransactionEditor(
        state = state,
        onEvent = model::onEvent,
        onClose = onClose,
    )
}
