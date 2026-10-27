package com.eec.app

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.SnackbarHostState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalLayoutDirection
import androidx.compose.ui.unit.LayoutDirection
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import com.eec.app.di.AppContainer
import com.eec.app.editor.EditorTarget
import com.eec.app.navigation.EecNavigator
import com.eec.app.navigation.Screen
import com.eec.app.navigation.rememberEecNavigator
import com.eec.app.security.LockScreen
import com.eec.core.db.AppDatabase
import com.eec.core.model.TransactionListItem
import com.eec.design.component.AppScaffold
import com.eec.design.component.BottomBar
import com.eec.design.component.ErrorState
import com.eec.design.component.LoadingState
import com.eec.design.component.NavigationRail
import com.eec.design.component.TopBar
import com.eec.design.icon.EecIcons
import com.eec.design.theme.EecSizes
import com.eec.design.theme.EecTheme
import kotlinx.coroutines.launch

@Composable
fun App(database: AppDatabase) {
    val container = remember(database) { AppContainer(database) }
    val scope = rememberCoroutineScope()
    val appModel = remember(container) { AppScreenModel(container, scope) }
    val appState by appModel.state.collectAsState()
    val navigator = rememberEecNavigator()
    val backStackEntry by navigator.controller.currentBackStackEntryAsState()
    val current = Screen.fromRoute(backStackEntry?.destination?.route)
    val snackbarHostState = remember { SnackbarHostState() }
    var editorTarget by remember { mutableStateOf<EditorTarget?>(null) }
    val systemDark = isSystemInDarkTheme()
    val darkTheme = when (appState.theme) {
        ThemeMode.SYSTEM -> systemDark
        ThemeMode.LIGHT -> false
        ThemeMode.DARK -> true
    }
    val startupError = appState.error

    LaunchedEffect(appModel) {
        appModel.onEvent(AppEvent.Start)
    }
    LaunchedEffect(appState.message) {
        val message = appState.message ?: return@LaunchedEffect
        snackbarHostState.showSnackbar(message)
        appModel.onEvent(AppEvent.MessageShown)
    }

    EecTheme(darkTheme = darkTheme) {
        CompositionLocalProvider(LocalLayoutDirection provides LayoutDirection.Rtl) {
            when {
                appState.loading -> StartupLoading()
                startupError != null -> StartupError(
                    message = startupError,
                    onRetry = { appModel.onEvent(AppEvent.Start) },
                )

                appState.locked -> AppScaffold(topBar = { TopBar(title = AppInfo.NAME) }) { padding ->
                    LockScreen(
                        error = appState.pinError,
                        onUnlock = { appModel.onEvent(AppEvent.Unlock(it)) },
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(padding),
                    )
                }

                else -> {
                    AppShell(
                        container = container,
                        current = current,
                        navigator = navigator,
                        theme = appState.theme,
                        snackbarHostState = snackbarHostState,
                        onThemeChange = { appModel.onEvent(AppEvent.SetTheme(it)) },
                        onLockChanged = { appModel.onEvent(AppEvent.LockChanged(it)) },
                        onAdd = { editorTarget = EditorTarget.Add },
                        onEdit = { editorTarget = EditorTarget.Edit(it) },
                    )
                    editorTarget?.let { target ->
                        EditorRoute(
                            container = container,
                            target = target,
                            onClose = { editorTarget = null },
                            onSaved = {
                                editorTarget = null
                                scope.launch { snackbarHostState.showSnackbar("تم حفظ المعاملة") }
                            },
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun AppShell(
    container: AppContainer,
    current: Screen,
    navigator: EecNavigator,
    theme: ThemeMode,
    snackbarHostState: SnackbarHostState,
    onThemeChange: (ThemeMode) -> Unit,
    onLockChanged: (Boolean) -> Unit,
    onAdd: () -> Unit,
    onEdit: (TransactionListItem) -> Unit,
) {
    BoxWithConstraints(modifier = Modifier.fillMaxSize()) {
        val useRail = maxWidth >= EecSizes.railBreakpoint
        AppScaffold(
            topBar = { AppTopBar(current = current, onBack = { navigator.pop() }) },
            bottomBar = {
                if (!useRail) {
                    BottomBar(
                        items = navigationItems,
                        selectedKey = current.navigationKey(),
                        onSelect = { key -> navigator.select(navigationScreen(key)) },
                    )
                }
            },
            snackbarHostState = snackbarHostState,
            floatingActionButton = {
                if (current == Screen.Dashboard || current == Screen.Transactions) {
                    FloatingActionButton(
                        onClick = onAdd,
                        containerColor = EecTheme.colors.primary,
                        contentColor = EecTheme.colors.onPrimary,
                    ) {
                        Icon(
                            imageVector = EecIcons.add,
                            contentDescription = "إضافة معاملة",
                        )
                    }
                }
            },
        ) { padding ->
            Row(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(padding),
            ) {
                if (useRail) {
                    NavigationRail(
                        items = navigationItems,
                        selectedKey = current.navigationKey(),
                        onSelect = { key -> navigator.select(navigationScreen(key)) },
                    )
                }
                AppNavHost(
                    navigator = navigator,
                    container = container,
                    theme = theme,
                    snackbarHostState = snackbarHostState,
                    onThemeChange = onThemeChange,
                    onLockChanged = onLockChanged,
                    onAdd = onAdd,
                    onEdit = onEdit,
                    modifier = Modifier
                        .weight(1f)
                        .fillMaxSize(),
                )
            }
        }
    }
}

@Composable
private fun AppNavHost(
    navigator: EecNavigator,
    container: AppContainer,
    theme: ThemeMode,
    snackbarHostState: SnackbarHostState,
    onThemeChange: (ThemeMode) -> Unit,
    onLockChanged: (Boolean) -> Unit,
    onAdd: () -> Unit,
    onEdit: (TransactionListItem) -> Unit,
    modifier: Modifier = Modifier,
) {
    NavHost(
        navController = navigator.controller,
        startDestination = Screen.Dashboard.route,
        modifier = modifier,
    ) {
        composable(Screen.Dashboard.route) {
            DashboardRoute(
                container = container,
                onAdd = onAdd,
                onEdit = onEdit,
            )
        }
        composable(Screen.Transactions.route) {
            TransactionsRoute(
                container = container,
                snackbarHostState = snackbarHostState,
                onAdd = onAdd,
                onEdit = onEdit,
            )
        }
        composable(Screen.Reports.route) {
            ReportsRoute(
                container = container,
                snackbarHostState = snackbarHostState,
                onAdd = onAdd,
            )
        }
        composable(Screen.Settings.route) {
            SettingsRoute(
                container = container,
                theme = theme,
                onThemeChange = onThemeChange,
                onLockChanged = onLockChanged,
                onOpenCategories = { navigator.push(Screen.Categories) },
                snackbarHostState = snackbarHostState,
            )
        }
        composable(Screen.Categories.route) {
            CategoriesRoute(
                container = container,
                snackbarHostState = snackbarHostState,
            )
        }
    }
}

@Composable
private fun AppTopBar(current: Screen, onBack: () -> Unit) {
    val isChild = current == Screen.Categories
    TopBar(
        title = current.title(),
        navigationIcon = if (isChild) EecIcons.back else null,
        onNavigationClick = if (isChild) onBack else null,
    )
}

@Composable
private fun StartupLoading() {
    AppScaffold(topBar = { TopBar(title = AppInfo.NAME) }) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding),
            horizontalAlignment = Alignment.CenterHorizontally,
        ) {
            LoadingState(message = "جارٍ فتح البيانات")
        }
    }
}

@Composable
private fun StartupError(message: String, onRetry: () -> Unit) {
    AppScaffold(topBar = { TopBar(title = AppInfo.NAME) }) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding),
            horizontalAlignment = Alignment.CenterHorizontally,
        ) {
            ErrorState(
                title = "تعذر فتح البيانات",
                message = message,
                onRetry = onRetry,
            )
        }
    }
}
