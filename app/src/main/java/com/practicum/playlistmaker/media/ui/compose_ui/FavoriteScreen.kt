package com.practicum.playlistmaker.media.ui.compose_ui

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.dimensionResource
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.Font
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.ViewModel
import androidx.navigation.NavBackStackEntry
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.google.gson.Gson
import com.practicum.playlistmaker.R
import com.practicum.playlistmaker.media.ui.state.FavoriteState
import com.practicum.playlistmaker.media.ui.view_model.FavoriteViewModule
import com.practicum.playlistmaker.search.domain.model.Track
import com.practicum.playlistmaker.ui.TrackItem

@Composable
fun FavoriteScreen(
    viewModel: FavoriteViewModule,
    startDestination: String = "favorite"
) {
    val state = viewModel.observerState().observeAsState().value
    val navController = rememberNavController()

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
    ) {
        when(state) {
            is FavoriteState.Content -> {
                NavHost(navController, startDestination) {
                    composable("favorite") { navBackStackEntry ->
                        LazyColumn(

                        ) {
                            items(state.tracks) { item ->
                                TrackItem(item)
                            }
                        }
                    }

                    composable(
                        "player/{track}",
                        arguments = listOf(navArgument("track") { type = NavType.StringType })
                    ) { navBackStackEntry ->
//                        Отобразить плеер
                    }
                }
            }
            FavoriteState.Empty -> {
                FavoritePlaceholder()
            }

            null -> "TODO()"
        }
    }
}

@Composable
fun FavoritePlaceholder() {
    val imageRes = if (isSystemInDarkTheme()) {
        R.drawable.plug_nothing_found_night
    } else {
        R.drawable.plug_nothing_found_day
    }

    Column(
        modifier = Modifier
            .fillMaxWidth(),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Image(
            modifier = Modifier
                .padding(top = 118.dp)
                .size(120.dp, 120.dp)
                .background(MaterialTheme.colorScheme.background),
            contentDescription = null,
            painter = painterResource(imageRes),
        )

        Text(
            text = stringResource(R.string.emptyFavorite),
            fontSize = 19.sp,
            fontWeight = FontWeight(500),
            fontFamily = FontFamily(Font(R.font.ys_display_medium, FontWeight(500))),
            color = MaterialTheme.colorScheme.onBackground,
            textAlign = TextAlign.Center
        )
    }
}

/*@Composable
fun FavoriteNavGraph(
    startDestination: String = "favorite"
) {
    val navController = rememberNavController()

    NavHost(navController = navController, startDestination = startDestination) {
        composable("favorite") {
            FavoriteScreen()
        }
        composable("playlists") {
            PlaylistsScreen()
        }
    }
}*/

@Preview(showBackground = false, showSystemUi = true, device = "id:medium_phone")
@Composable
fun FavoriteScreenPreview() {
//    FavoriteScreen()
}
