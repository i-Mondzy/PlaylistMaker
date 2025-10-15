package com.practicum.playlistmaker.search.ui.compose_ui

import android.view.SoundEffectConstants
import android.view.View
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.Button
import androidx.compose.material.ButtonDefaults
import androidx.compose.material.TopAppBar
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.platform.LocalView
import androidx.compose.ui.res.dimensionResource
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.Font
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import androidx.navigation.findNavController
import com.practicum.playlistmaker.R
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
                    .fillMaxSize()
                    .padding(top = innerPadding.calculateTopPadding()),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                SearchField(viewModel, text)

                when (state) {
                    is TracksState.Content -> {
                        LazyColumn(
                            modifier = Modifier
                                .padding(top = 16.dp),
                            state = listState
                        ) {
                            items(state.tracks) { track ->
                                SearchTracks(viewModel, view, navController, track)
                            }
                        }
                    }

                    is TracksState.History -> {
                        Text(
                            modifier = Modifier
                                .padding(top = 40.dp, bottom = 20.dp),
                            text = stringResource(R.string.you_search),
                            color = MaterialTheme.colorScheme.onBackground,
                            fontSize = 19.sp,
                            fontFamily = FontFamily(Font(R.font.ys_display_medium)),
                            fontWeight = FontWeight.W500
                        )

                        LazyColumn(
                            state = listState
                        ) {
                            items(state.tracks) { track ->
                                SearchTracks(viewModel, view, navController, track)
                            }
                        }

                        if (state.tracks.isNotEmpty()) {
                            ClearButton(viewModel)
                        }
                    }

                    TracksState.Loading -> {
                        CircularProgressIndicator(
                            modifier = Modifier
                                .padding(top = 124.dp)
                                .size(44.dp),
                            color = MaterialTheme.colorScheme.onPrimary,
                            strokeWidth = 4.dp
                        )
                    }

                    TracksState.Empty -> NothingSearchPlaceholder()
                    TracksState.Error -> ErrorPlaceholder(viewModel)

                    else -> null
                }
            }
        }
    }
}

@Composable
fun ErrorPlaceholder(viewModel: SearchViewModel) {
    val imageRes = if (isSystemInDarkTheme()) {
        R.drawable.plug_something_wrong_night
    } else {
        R.drawable.plug_something_wrong_day
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
            text = stringResource(R.string.no_internet),
            fontSize = 19.sp,
            fontWeight = FontWeight(500),
            fontFamily = FontFamily(Font(R.font.ys_display_medium, FontWeight(500))),
            color = MaterialTheme.colorScheme.onBackground,
            textAlign = TextAlign.Center
        )

        Button(
            modifier = Modifier
                .padding(top = 24.dp),
            elevation = ButtonDefaults.elevation(4.dp),
            shape = RoundedCornerShape(54.dp),
            colors = ButtonDefaults.buttonColors(MaterialTheme.colorScheme.onBackground),
            onClick = {
                viewModel.updateSearch()
            }
        ) {
            Text(
                text = stringResource(R.string.update),
                color = MaterialTheme.colorScheme.background,
                fontSize = 14.sp,
                fontWeight = FontWeight(500),
                fontFamily = FontFamily(Font(R.font.ys_display_medium))
            )
        }
    }
}

@Composable
fun NothingSearchPlaceholder() {
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
            text = stringResource(R.string.nothing_found),
            fontSize = 19.sp,
            fontWeight = FontWeight(500),
            fontFamily = FontFamily(Font(R.font.ys_display_medium, FontWeight(500))),
            color = MaterialTheme.colorScheme.onBackground,
            textAlign = TextAlign.Center
        )


    }
}

@Composable
fun SearchField(
    viewModel: SearchViewModel, text: String
) {

    val focusManager = LocalFocusManager.current
    val colors = TextFieldDefaults.colors(
        focusedTextColor = MaterialTheme.colorScheme.primary,
        unfocusedTextColor = MaterialTheme.colorScheme.primary,
        cursorColor = MaterialTheme.colorScheme.onPrimary,
        focusedPlaceholderColor = MaterialTheme.colorScheme.tertiary,
        unfocusedPlaceholderColor = MaterialTheme.colorScheme.tertiary,
        focusedContainerColor = Color.Transparent,
        unfocusedContainerColor = Color.Transparent,
        disabledContainerColor = Color.Transparent,
        focusedIndicatorColor = Color.Transparent,
        unfocusedIndicatorColor = Color.Transparent,
        disabledIndicatorColor = Color.Transparent,
        focusedLeadingIconColor = MaterialTheme.colorScheme.tertiary,
        unfocusedLeadingIconColor = MaterialTheme.colorScheme.tertiary,
    )

    Box(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = dimensionResource(R.dimen.default_padding), vertical = 8.dp)
            .height(36.dp)
            .background(
                color = MaterialTheme.colorScheme.tertiaryContainer,
                shape = RoundedCornerShape(8.dp)
            )
    ) {
        BasicTextField(
            modifier = Modifier.fillMaxWidth(),
            value = text,
            onValueChange = {
                viewModel.onTextChanged(it)
                viewModel.searchDebounce(it)
            },
            singleLine = true,
            textStyle = TextStyle(
                color = MaterialTheme.colorScheme.onBackground,
                fontFamily = FontFamily(Font(R.font.ys_display_regular)),
                fontWeight = FontWeight.W400,
                fontSize = dimensionResource(R.dimen.default_text_size).value.sp
            ),
            cursorBrush = SolidColor(MaterialTheme.colorScheme.onPrimary),
            keyboardOptions = KeyboardOptions(
                imeAction = ImeAction.Done,
                keyboardType = KeyboardType.Text
            ),
            keyboardActions = KeyboardActions(
                onDone = {
                    focusManager.clearFocus()
                }
            ),
            decorationBox = { innerTextField ->
                TextFieldDefaults.DecorationBox(
                    value = text,
                    innerTextField = innerTextField,
                    enabled = true,
                    singleLine = true,
                    colors = colors,
                    visualTransformation = VisualTransformation.None,
                    interactionSource = remember { MutableInteractionSource() },
                    contentPadding = PaddingValues(0.dp), // кастомный отступ
                    leadingIcon = {
                        Icon(
                            painter = painterResource(R.drawable.ic_search_edit_text),
                            contentDescription = null,
                            tint = MaterialTheme.colorScheme.tertiary
                        )
                    },
                    trailingIcon = {
                        if (text.isNotEmpty()) {
                            IconButton(
                                onClick = { viewModel.onTextChanged("") },
                            ) {
                                Icon(
                                    painter = painterResource(R.drawable.ic_clear),
                                    contentDescription = null,
                                    tint = MaterialTheme.colorScheme.tertiary
                                )
                            }
                        }
                    },
                    placeholder = {
                        Text(
                            text = stringResource(R.string.search),
                            color = MaterialTheme.colorScheme.tertiary,
                            fontFamily = FontFamily(Font(R.font.ys_display_regular)),
                            fontWeight = FontWeight.W400,
                            fontSize = dimensionResource(R.dimen.default_text_size).value.sp
                        )
                    }
                )
            }
        )
    }
}

@Composable
fun SearchTracks(
    viewModel: SearchViewModel,
    view: View,
    navController: NavController,
    track: Track
) {
    val coroutineScope = rememberCoroutineScope()
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clickable {
                view.playSoundEffect(SoundEffectConstants.CLICK)
                coroutineScope.launch {
                    viewModel.saveTrack(track)
                    navController.navigate(
                        R.id.action_searchFragment_to_playerFragment,
                        PlayerFragment.createArgs(track)
                    )
                }
            }
    ) {
        TrackItem(track)
    }
}

@Composable
fun ClearButton(viewModel: SearchViewModel) {
    Box(
        modifier = Modifier
            .fillMaxWidth(),
        contentAlignment = Alignment.Center
    ) {
        Button(
            modifier = Modifier
                .padding(top = 24.dp),
            elevation = ButtonDefaults.elevation(4.dp),
            shape = RoundedCornerShape(54.dp),
            colors = ButtonDefaults.buttonColors(MaterialTheme.colorScheme.onBackground),
            onClick = {
                viewModel.clearHistory()
            }
        ) {
            Text(
                text = stringResource(R.string.clear_history),
                color = MaterialTheme.colorScheme.background,
                fontSize = 14.sp,
                fontWeight = FontWeight(500),
                fontFamily = FontFamily(Font(R.font.ys_display_medium))
            )
        }
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
