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
import com.eec.app.localization.LanguageMode
import com.eec.app.localization.appString
import com.eec.app.navigation.Screen
import com.eec.app.reports.ReportsScreen
import com.eec.app.resources.Res
import com.eec.app.resources.nav_dashboard
import com.eec.app.resources.nav_reports
import com.eec.app.resources.nav_settings
import com.eec.app.resources.nav_transactions
import com.eec.app.resources.screen_categories
import com.eec.app.share.shareTextStrings
import com.eec.app.reports.ReportsScreenModel
import com.eec.app.settings.SettingsScreen
import com.eec.app.settings.SettingsScreenModel
import com.eec.app.transactions.TransactionsScreen
import com.eec.app.transactions.TransactionsScreenModel
import com.eec.app.ui.rememberScreenScope
import com.eec.core.model.TransactionListItem
import com.eec.design.component.NavigationItem
import com.eec.design.icon.EecIcons

@Composable
fun navigationItems(): List<NavigationItem> = listOf(
    NavigationItem(
        key = Screen.Dashboard.route,
        label = appString(Res.string.nav_dashboard),
        icon = EecIcons.dashboard,
    ),
    NavigationItem(
        key = Screen.Transactions.route,
        label = appString(Res.string.nav_transactions),
        icon = EecIcons.transactions,
    ),
    NavigationItem(
        key = Screen.Reports.route,
        label = appString(Res.string.nav_reports),
        icon = EecIcons.reports,
    ),
    NavigationItem(
        key = Screen.Settings.route,
        label = appString(Res.string.nav_settings),
        icon = EecIcons.settings,
    ),
)

fun Screen.navigationKey(): String = when (this) {
    Screen.Dashboard -> "dashboard"
    Screen.Transactions -> "transactions"
    Screen.Reports -> "reports"
    Screen.Settings -> "settings"
    Screen.Categories -> "settings"
}

@Composable
fun Screen.title(): String = when (this) {
    Screen.Dashboard -> appString(Res.string.nav_dashboard)
    Screen.Transactions -> appString(Res.string.nav_transactions)
    Screen.Reports -> appString(Res.string.nav_reports)
    Screen.Settings -> appString(Res.string.nav_settings)
    Screen.Categories -> appString(Res.string.screen_categories)
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
    val shareStrings = shareTextStrings()
    val model = remember(container, shareStrings) {
        ReportsScreenModel(container, scope, shareStrings)
    }
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
    language: LanguageMode,
    onLanguageChange: (LanguageMode) -> Unit,
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
        language = language,
        canShareFiles = container.shareService.canShareFiles(),
        onThemeChange = onThemeChange,
        onLanguageChange = onLanguageChange,
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
