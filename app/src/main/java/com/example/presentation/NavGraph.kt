package com.example.presentation

import androidx.compose.runtime.Composable
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.example.presentation.home.HomeScreen
import com.example.presentation.editor.EditorScreen
import com.example.presentation.settings.SettingsScreen
import kotlinx.serialization.Serializable

import androidx.navigation.toRoute

@Serializable object HomeRoute
@Serializable data class EditorRoute(val documentId: String)
@Serializable data class ExportRoute(val documentId: String)
@Serializable object SettingsRoute

@Composable
fun DocForgeNavGraph(
    navController: NavHostController = rememberNavController()
) {
    NavHost(
        navController = navController,
        startDestination = HomeRoute
    ) {
        composable<HomeRoute> {
            HomeScreen(
                onNavigateToEditor = { documentId ->
                    if (documentId != null) {
                        navController.navigate(EditorRoute(documentId))
                    }
                },
                onNavigateToSettings = {
                    navController.navigate(SettingsRoute)
                }
            )
        }
        composable<EditorRoute> {
            val route = it.toRoute<EditorRoute>()
            EditorScreen(
                onNavigateBack = { navController.popBackStack() },
                documentId = route.documentId
            )
        }
        composable<ExportRoute> {
            // ExportScreen implementation placeholder
        }
        composable<SettingsRoute> {
            SettingsScreen(onNavigateBack = { navController.popBackStack() })
        }
    }
}
