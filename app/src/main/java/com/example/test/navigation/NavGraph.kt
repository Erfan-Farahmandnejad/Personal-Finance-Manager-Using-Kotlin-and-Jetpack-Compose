package com.example.test.navigation

import androidx.compose.runtime.Composable
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.compose.*
import androidx.navigation.NavType
import androidx.navigation.navArgument
import com.example.test.view.auth.SignInScreen
import com.example.test.view.auth.SignUpScreen
import com.example.test.view.settings.SettingsScreen
import com.example.test.view.categories.CategoryManagerScreen
import com.example.test.view.categories.AddEditCategoryScreen
import com.example.test.view.accounts.AccountManagerScreen
import com.example.test.view.accounts.AddEditAccountScreen
import com.example.test.view.budgeting.BudgetManagerScreen
import com.example.test.view.budgeting.AddEditBudgetScreen
import com.example.test.view.transactions.TransactionListScreen
import com.example.test.view.transactions.AddTransactionScreen
import com.example.test.view.notifications.NotificationScreen
import com.example.test.view.reports.ReportScreen
import com.example.test.view.dashboard.DashboardScreen

@Composable
fun NavGraph(startDestination: String = Screen.SignIn.route) {
    val navController = rememberNavController()
    NavHost(navController, startDestination) {

        composable(Screen.SignIn.route) {
            SignInScreen(
                onSignInSuccess = {
                    navController.navigate(Screen.Dashboard.route) {
                        popUpTo(Screen.SignIn.route) { inclusive = true } // clear backstack
                    }
                },
                viewModel = hiltViewModel(),
                onNavigateToSignUp = {
                    navController.navigate(Screen.SignUp.route)
                }
            )
        }

        composable(Screen.SignUp.route) {
            SignUpScreen(
                onSignUpSuccess = {
                    navController.navigate(Screen.Dashboard.route) {
                        popUpTo(Screen.SignUp.route) { inclusive = true }
                    }
                },
                viewModel = hiltViewModel(),
                onNavigateToSignIn = { 
                    navController.navigate(Screen.SignIn.route) {
                        popUpTo(Screen.SignUp.route) { inclusive = true }
                    }
                }
            )
        }

        composable(Screen.Dashboard.route) {
            DashboardScreen(
                onAddTransaction = {
                    navController.navigate(Screen.AddTransaction.route)
                },
                onNavigate = { route ->
                    navController.navigate(route) {
                        popUpTo(Screen.Dashboard.route)
                    }
                }
            )
        }

        composable(Screen.Settings.route) {
            SettingsScreen(
                viewModel = hiltViewModel(),
                onNavigate = { route ->
                    navController.navigate(route) {
                        popUpTo(Screen.Dashboard.route)
                    }
                }
            )
        }

        composable(Screen.Categories.route) {
            CategoryManagerScreen(
                onAddCategory = { navController.navigate(Screen.AddCategory.route) },
                onNavigate = { route ->
                    navController.navigate(route) {
                        popUpTo(Screen.Dashboard.route)
                    }
                }
            )
        }

        composable(Screen.AddCategory.route) {
            AddEditCategoryScreen(
                onSave = { 
                    navController.navigate(Screen.Categories.route) {
                        popUpTo(Screen.Categories.route) { inclusive = true }
                    }
                }
            )
        }

        composable(
            route = Screen.EditCategory.route,
            arguments = listOf(
                navArgument("categoryId") { type = NavType.IntType }
            )
        ) { backStackEntry ->
            val categoryId = backStackEntry.arguments?.getInt("categoryId")
            AddEditCategoryScreen(
                categoryId = categoryId,
                onSave = { 
                    navController.navigate(Screen.Categories.route) {
                        popUpTo(Screen.Categories.route) { inclusive = true }
                    }
                }
            )
        }

        composable(Screen.Accounts.route) {
            AccountManagerScreen(
                onAddAccount = { navController.navigate(Screen.AddAccount.route) },
                onNavigate = { route ->
                    navController.navigate(route) {
                        popUpTo(Screen.Dashboard.route)
                    }
                }
            )
        }

        composable(Screen.AddAccount.route) {
            AddEditAccountScreen(
                onSave = {
                    navController.navigate(Screen.Accounts.route) {
                        popUpTo(Screen.Accounts.route) { inclusive = true }
                    }
                }
            )
        }

        composable(
            route = Screen.EditAccount.route,
            arguments = listOf(
                navArgument("accountId") { type = NavType.IntType }
            )
        ) { backStackEntry ->
            val accountId = backStackEntry.arguments?.getInt("accountId") ?: return@composable
            AddEditAccountScreen(
                accountId = accountId,
                onSave = {
                    navController.navigate(Screen.Accounts.route) {
                        popUpTo(Screen.Accounts.route) { inclusive = true }
                    }
                }
            )
        }

        composable(Screen.Budgets.route) {
            BudgetManagerScreen(
                onAddBudget = { navController.navigate(Screen.AddEditBudget.route) },
                onNavigate = { route ->
                    navController.navigate(route) {
                        popUpTo(Screen.Dashboard.route)
                    }
                }
            )
        }

        composable(Screen.AddEditBudget.route) {
            AddEditBudgetScreen(
                onSave = { 
                    navController.navigate(Screen.Budgets.route) {
                        popUpTo(Screen.Budgets.route) { inclusive = true }
                    }
                }
            )
        }

        composable(
            route = Screen.EditBudget.route,
            arguments = listOf(
                navArgument("budgetId") { type = NavType.IntType }
            )
        ) { backStackEntry ->
            val budgetId = backStackEntry.arguments?.getInt("budgetId")
            AddEditBudgetScreen(
                budgetId = budgetId,
                onSave = { 
                    navController.navigate(Screen.Budgets.route) {
                        popUpTo(Screen.Budgets.route) { inclusive = true }
                    }
                }
            )
        }

        composable(Screen.Transactions.route) {
            TransactionListScreen(
                onAddTransaction = { navController.navigate(Screen.AddTransaction.route) },
                onNavigate = { route ->
                    navController.navigate(route) {
                        popUpTo(Screen.Dashboard.route)
                    }
                }
            )
        }

        composable(Screen.AddTransaction.route) {
            AddTransactionScreen(
                onSave = {
                    navController.navigate(Screen.Transactions.route) {
                        popUpTo(Screen.Transactions.route) { inclusive = true }
                    }
                }
            )
        }

        composable(
            route = Screen.EditTransaction.route,
            arguments = listOf(
                navArgument("transactionId") { type = NavType.IntType }
            )
        ) { backStackEntry ->
            val transactionId = backStackEntry.arguments?.getInt("transactionId") ?: return@composable
            AddTransactionScreen(
                transactionId = transactionId,
                onSave = {
                    navController.navigate(Screen.Transactions.route) {
                        popUpTo(Screen.Transactions.route) { inclusive = true }
                    }
                }
            )
        }

        composable(Screen.Notifications.route) {
            NotificationScreen(
                onNavigate = { route ->
                    navController.navigate(route) {
                        popUpTo(Screen.Dashboard.route)
                    }
                }
            )
        }

        composable(Screen.Reports.route) {
            ReportScreen(
                onNavigate = { route ->
                    navController.navigate(route) {
                        popUpTo(Screen.Dashboard.route)
                    }
                }
            )
        }
    }
}
