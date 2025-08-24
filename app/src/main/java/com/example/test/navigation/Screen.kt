package com.example.test.navigation

sealed class Screen(val route: String) {
    object SignIn : Screen("signIn")
    object SignUp : Screen("signUp")
    object Dashboard : Screen("dashboard")
    object Settings : Screen("settings")
    object Categories : Screen("categories")
    object AddCategory : Screen("addCategory")
    object EditCategory : Screen("addCategory/{categoryId}") {
        fun createRoute(categoryId: Int) = "addCategory/$categoryId"
    }
    object Accounts : Screen("accounts")
    object AddAccount : Screen("addAccount")
    object EditAccount : Screen("addAccount/{accountId}") {
        fun createRoute(accountId: Int) = "addAccount/$accountId"
    }
    object Budgets : Screen("budgets")
    object AddEditBudget : Screen("addEditBudget")
    object EditBudget : Screen("addEditBudget/{budgetId}") {
        fun createRoute(budgetId: Int) = "addEditBudget/$budgetId"
    }
    object Transactions : Screen("transactions")
    object AddTransaction : Screen("addTransaction")
    object EditTransaction : Screen("addTransaction/{transactionId}") {
        fun createRoute(transactionId: Int) = "addTransaction/$transactionId"
    }
    object Notifications : Screen("notifications")
    object Reports : Screen("reports")
}
