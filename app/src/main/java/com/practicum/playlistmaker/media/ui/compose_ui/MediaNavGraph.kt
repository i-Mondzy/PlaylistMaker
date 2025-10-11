package com.practicum.playlistmaker.media.ui.compose_ui

import androidx.compose.runtime.Composable
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.google.gson.Gson
import com.practicum.playlistmaker.search.domain.model.Track

/*@Composable
fun MediaNavGraph(
    startDestination: String = "favorites",
    gson: Gson,
    movies: List<Track>
) {
    val navController = rememberNavController()

    NavHost(navController = navController, startDestination = "home") {
        composable("favorites") {
            FavoriteScreen(
                onNavigateToDetails = {
                    navController.navigate("details")
                }
            )
        }
        composable("playlists") {
            PlaylistsScreen(
                onNavigateBack = {
                    navController.popBackStack()
                }
            )
        }
    }
}*/
