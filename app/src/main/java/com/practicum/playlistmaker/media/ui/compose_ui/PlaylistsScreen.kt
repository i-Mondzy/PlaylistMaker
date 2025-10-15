package com.practicum.playlistmaker.media.ui.compose_ui

import android.view.SoundEffectConstants
import android.view.View
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.lazy.grid.rememberLazyGridState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.Button
import androidx.compose.material.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.RectangleShape
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
import com.practicum.playlistmaker.create_playlist.domain.model.Playlist
import com.practicum.playlistmaker.media.ui.state.PlaylistsState
import com.practicum.playlistmaker.media.ui.view_model.PlaylistsViewModule
import com.practicum.playlistmaker.playlist.ui.fragment.PlaylistFragment
import com.practicum.playlistmaker.ui.PlaylistItem
import org.koin.androidx.compose.koinViewModel

@Composable
fun PlaylistsScreen(
    viewModel: PlaylistsViewModule
) {
    val state = viewModel.observeState().observeAsState().value
    val listState = rememberLazyGridState()
    val view = LocalView.current
    val navController = view.findNavController()

    Column {
        Box(
            modifier = Modifier
                .fillMaxWidth(),
            contentAlignment = Alignment.Center
        ) {
            Button(
                modifier = Modifier
                    .padding(top = 8.dp),
                elevation = ButtonDefaults.elevation(4.dp),
                shape = RoundedCornerShape(54.dp),
                colors = ButtonDefaults.buttonColors(MaterialTheme.colorScheme.onBackground),
                onClick = {
                    navController.navigate(R.id.action_mediaFragment_to_createPlaylist,null)
                }
            ) {
                Text(
                    text = stringResource(R.string.newPlaylist),
                    color = MaterialTheme.colorScheme.background,
                    fontSize = 14.sp,
                    fontWeight = FontWeight(500),
                    fontFamily = FontFamily(Font(R.font.ys_display_medium))
                )
            }
        }

        Box(
            modifier = Modifier
                .padding(16.dp)
                .fillMaxSize()
                .background(MaterialTheme.colorScheme.background)
        ) {
            when(state) {
                is PlaylistsState.Content -> {
                    LazyVerticalGrid(
                        columns = GridCells.Fixed(2),
                        state = listState,
                        verticalArrangement = Arrangement.spacedBy(8.dp),
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        items(state.playlists) { playlist ->
                            Playlists(view, navController, playlist)
                        }
                    }
                }
                PlaylistsState.Empty -> {
                    PlaylistsPlaceholder()
                }
                else -> null
            }
        }
    }
}

@Composable
fun Playlists(view: View, navController: NavController, playlist: Playlist) {
    Card(
        shape = RectangleShape,

    ) {
        Box(
            modifier = Modifier
                .background(MaterialTheme.colorScheme.background)
                .fillMaxWidth()
                .clickable {
                    view.playSoundEffect(SoundEffectConstants.CLICK)
                    navController.navigate(
                        R.id.action_mediaFragment_to_playlistFragment,PlaylistFragment.createArgs(playlist.playlistId)
                    )
                }
        ) {
            PlaylistItem(playlist)
        }
    }
}

@Composable
fun PlaylistsPlaceholder() {
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
                .padding(top = 118.dp, bottom = 16.dp)
                .size(120.dp, 120.dp)
                .background(MaterialTheme.colorScheme.background),
            contentDescription = null,
            painter = painterResource(imageRes)
        )

        Text(
            modifier = Modifier
                .padding(horizontal = 24.dp),
            text = stringResource(R.string.emptyPlaylist),
            fontSize = 19.sp,
            fontWeight = FontWeight(500),
            fontFamily = FontFamily(Font(R.font.ys_display_medium, FontWeight(500))),
            color = MaterialTheme.colorScheme.onBackground,
            textAlign = TextAlign.Center
        )
    }
}

@Preview(showSystemUi = true, device = "spec:width=411dp,height=891dp")
@Composable
fun PlaylistsScreenPreview() {
    PlaylistsScreen(koinViewModel())
}
