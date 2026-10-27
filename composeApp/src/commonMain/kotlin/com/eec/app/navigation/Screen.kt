package com.eec.app.navigation

sealed interface Screen {
    val route: String

    data object Dashboard : Screen {
        override val route: String = "dashboard"
    }

    data object Transactions : Screen {
        override val route: String = "transactions"
    }

    data object Reports : Screen {
        override val route: String = "reports"
    }

    data object Settings : Screen {
        override val route: String = "settings"
    }

    data object Categories : Screen {
        override val route: String = "categories"
    }

    companion object {
        fun fromRoute(route: String?): Screen = when (route) {
            Transactions.route -> Transactions
            Reports.route -> Reports
            Settings.route -> Settings
            Categories.route -> Categories
            else -> Dashboard
        }
    }
}
