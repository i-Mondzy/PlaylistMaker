package com.practicum.playlistmaker.media.ui.compose_ui

import android.view.SoundEffectConstants
import android.view.View
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalView
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.Font
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import androidx.navigation.findNavController
import com.practicum.playlistmaker.R
import com.practicum.playlistmaker.media.ui.state.FavoriteState
import com.practicum.playlistmaker.media.ui.view_model.FavoriteViewModule
import com.practicum.playlistmaker.player.ui.activity.PlayerFragment
import com.practicum.playlistmaker.search.domain.model.Track
import com.practicum.playlistmaker.ui.TrackItem
import org.koin.androidx.compose.koinViewModel

@Composable
fun FavoriteScreen(
    viewModel: FavoriteViewModule
) {
    val state = viewModel.observerState().observeAsState().value
    val listState = rememberLazyListState()
    val view = LocalView.current
    val navController = view.findNavController()

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
    ) {
        when(state) {
            is FavoriteState.Content -> {
                LazyColumn(
                    state = listState
                ) {
                    items(state.tracks) { track ->
                        FavoriteTracks(view, navController, track)
                    }
                }
            }
            FavoriteState.Empty -> {
                FavoritePlaceholder()
            }
            else -> null
        }
    }
}

@Composable
fun FavoriteTracks(view: View, navController: NavController, track: Track) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clickable {
                view.playSoundEffect(SoundEffectConstants.CLICK)
                navController.navigate(
                    R.id.action_mediaFragment_to_playerFragment, PlayerFragment.createArgs(track)
                )
            }
    ) {
        TrackItem(track)
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

@Preview(showBackground = false, showSystemUi = true, device = "id:medium_phone")
@Composable
fun FavoriteScreenPreview() {
    FavoriteScreen(koinViewModel())
}
