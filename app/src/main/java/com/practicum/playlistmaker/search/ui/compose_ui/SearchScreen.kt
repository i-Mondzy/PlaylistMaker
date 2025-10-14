package com.practicum.playlistmaker.search.ui.compose_ui

import android.view.SoundEffectConstants
import android.view.View
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
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.TopAppBar
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.LocalTextStyle
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.material3.TextFieldColors
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.RectangleShape
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.platform.LocalView
import androidx.compose.ui.res.dimensionResource
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.Font
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import androidx.navigation.findNavController
import com.practicum.playlistmaker.R
import com.practicum.playlistmaker.media.ui.compose_ui.FavoritePlaceholder
import com.practicum.playlistmaker.media.ui.compose_ui.FavoriteTracks
import com.practicum.playlistmaker.media.ui.state.FavoriteState
import com.practicum.playlistmaker.player.ui.activity.PlayerFragment
import com.practicum.playlistmaker.search.domain.model.Track
import com.practicum.playlistmaker.search.ui.state.TracksState
import com.practicum.playlistmaker.search.ui.view_model.SearchViewModel
import com.practicum.playlistmaker.ui.TrackItem
import com.practicum.playlistmaker.ui.theme.PlaylistMakerTheme
import kotlinx.coroutines.launch
import org.koin.androidx.compose.koinViewModel

@Composable
fun SearchScreen(viewModel: SearchViewModel) {
    val state = viewModel.observerState().observeAsState().value
    val text = viewModel.text.collectAsState().value
    val listState = rememberLazyListState()
    val view = LocalView.current
    val navController = view.findNavController()

    PlaylistMakerTheme {
        Scaffold(
            topBar = {
                TopAppBar(
                    backgroundColor = MaterialTheme.colorScheme.background,
                    elevation = 0.dp,
                    title = {
                        Text(
                            text = stringResource(R.string.search),
                            fontSize = dimensionResource(R.dimen.primary_text_size).value.sp,
                            fontWeight = FontWeight(500),
                            fontFamily = FontFamily(Font(R.font.ys_display_medium)),
                            color = MaterialTheme.colorScheme.onBackground
                        )
                    }
                )
            }
        ) { innerPadding ->
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = innerPadding.calculateTopPadding())
            ) {
                SearchField(viewModel, text)

                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .background(MaterialTheme.colorScheme.background)
                ) {
                    when (state) {
                        is TracksState.Content -> {
                            LazyColumn(
                                state = listState
                            ) {
                                items(state.tracks) { track ->
                                    SearchTracks(viewModel, view, navController, track)
                                }
                            }
                        }

                        is TracksState.History -> {
//                            if (text.isEmpty()) {
                                LazyColumn(
                                    state = listState
                                ) {
                                    items(state.tracks) { track ->
                                        SearchTracks(viewModel, view, navController, track)
                                    }
                                }
//                            }
                        }

                        TracksState.Loading -> {
                            CircularProgressIndicator(
                                modifier = Modifier.size(44.dp), // как 44dp в XML
                                color = MaterialTheme.colorScheme.onPrimary, // аналог indeterminateTint
                                strokeWidth = 4.dp
                            )
                        }

                        TracksState.Empty -> FavoritePlaceholder()
                        TracksState.Error -> FavoritePlaceholder()

                        else -> null
                    }
                }
            }
            /*Box(
                modifier = Modifier
                    .padding(top = innerPadding.calculateTopPadding())
                    .fillMaxSize()
                    .background(Color.White)
            ) { }*/
        }
    }
}

@Composable
fun SearchField(
    viewModel: SearchViewModel, text: String
) {

    // Цвет курсора: через colors у TextFieldDefaults
    val colors = TextFieldDefaults.colors(
        focusedTextColor = MaterialTheme.colorScheme.primary,
        unfocusedTextColor = MaterialTheme.colorScheme.primary,
        cursorColor = MaterialTheme.colorScheme.onPrimary,
        focusedPlaceholderColor = MaterialTheme.colorScheme.tertiary,
        unfocusedPlaceholderColor = MaterialTheme.colorScheme.tertiary,
        focusedContainerColor = MaterialTheme.colorScheme.tertiaryContainer,
        unfocusedContainerColor = MaterialTheme.colorScheme.tertiaryContainer,
        disabledContainerColor = MaterialTheme.colorScheme.tertiaryContainer,
        focusedIndicatorColor = Color.Transparent,
        unfocusedIndicatorColor = Color.Transparent,
        disabledIndicatorColor = Color.Transparent,
        focusedLeadingIconColor = MaterialTheme.colorScheme.tertiary,
        unfocusedLeadingIconColor = MaterialTheme.colorScheme.tertiary,
    )

    // Состояние фокуса для UX (по желанию)
    val focusManager = LocalFocusManager.current

    /*Box(
        modifier = Modifier
            .fillMaxWidth()
            .padding(bottom = dimensionResource(R.dimen.default_padding).value.dp)
            .padding(horizontal = dimensionResource(R.dimen.default_padding), vertical = 8.dp)
            .background(
                color = MaterialTheme.colorScheme.tertiaryContainer,
                shape = RoundedCornerShape(8.dp) // радиусы/фон перенесите из @drawable/custom_search_field
            )
            *//*.drawBehind {
                // Если @drawable/custom_search_field — shape/vector, используйте Modifier.background(shape, color)
                // Если это сложный drawable, можно нарисовать через Image(painterResource(...)) как фон
            }*//*
    ) {*/
    TextField(
        value = text,
        onValueChange = {
            viewModel.onTextChanged(it)
            viewModel.searchDebounce(it)
        },
        singleLine = true,
        placeholder = {
            Text(
                text = stringResource(R.string.search),
                color = MaterialTheme.colorScheme.tertiary,
                fontFamily = FontFamily(Font(R.font.ys_display_regular)),
                fontWeight = FontWeight.W400,
                fontSize = dimensionResource(R.dimen.default_text_size).value.sp
            )
        },
        leadingIcon = {
//                Row(modifier = Modifier.padding(end = 8.dp)) {
            Icon(
                modifier = Modifier.padding(end = 8.dp),
                painter = painterResource(R.drawable.ic_search_edit_text),
                contentDescription = null,
                tint = MaterialTheme.colorScheme.tertiary
            )
//                }
        },
        keyboardOptions = KeyboardOptions(
            imeAction = ImeAction.Done,
            keyboardType = KeyboardType.Text
        ),
        keyboardActions = KeyboardActions(
            onDone = {
                focusManager.clearFocus()
            }
        ),
        textStyle = LocalTextStyle.current.copy(
            fontFamily = FontFamily(Font(R.font.ys_display_regular)),
            fontWeight = FontWeight.W400,
            fontSize = dimensionResource(R.dimen.default_text_size).value.sp,
            color = MaterialTheme.colorScheme.primary
        ),
        colors = colors,
        shape = RoundedCornerShape(8.dp), // повторите форму фона из custom_search_field
        modifier = Modifier
            .fillMaxWidth()
            .padding(start = 12.dp, end = 36.dp, top = 8.dp, bottom = 8.dp),
        /*trailingIcon = {
            if (value.isNotEmpty()) {
                // Кнопка очистки справа; высота по центру как у match_parent ImageView
                IconButton(
                    onClick = { onValueChange("") },
                    modifier = Modifier.padding(start = 7.dp, end = horizontalPadding)
                ) {
                    Icon(painter = clearIcon, contentDescription = null, tint = iconColor)
                }
            }
        },*/
    )
//    }
}

@Composable
fun SearchTracks(viewModel: SearchViewModel, view: View, navController: NavController, track: Track) {
    val coroutineScope = rememberCoroutineScope()
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clickable {
                view.playSoundEffect(SoundEffectConstants.CLICK)
                coroutineScope.launch {
                    viewModel.saveTrack(track)
                    navController.navigate(
                        R.id.action_searchFragment_to_playerFragment, PlayerFragment.createArgs(track)
                    )
                }
            }
    ) {
        TrackItem(track)
    }
}


@Preview(
    showBackground = false,
    showSystemUi = true,
    device = "spec:width=1280dp,height=800dp,dpi=240"
)
@Composable
fun SearchScreenPreview() {
    SearchScreen(koinViewModel())
}
