package com.practicum.playlistmaker.settings.ui.compose_ui

import android.view.SoundEffectConstants
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.TopAppBar
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.painter.Painter
import androidx.compose.ui.platform.LocalView
import androidx.compose.ui.res.dimensionResource
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.Font
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.practicum.playlistmaker.R
import com.practicum.playlistmaker.settings.ui.view_model.SettingsViewModel
import com.practicum.playlistmaker.ui.theme.PlaylistMakerTheme

@Composable
fun SettingsScreen(
    viewModel: SettingsViewModel
) {
    val state = viewModel.observeTheme().observeAsState().value
    val view = LocalView.current

    PlaylistMakerTheme {
        Scaffold(
            topBar = {
                TopAppBar(
                    backgroundColor = MaterialTheme.colorScheme.background,
                    elevation = 0.dp,
                    title = {
                        Text(
                            text = stringResource(R.string.settings),
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
                    .padding(top = innerPadding.calculateTopPadding())
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .background(MaterialTheme.colorScheme.background)
                ) {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = dimensionResource(R.dimen.default_padding))
                            .padding(top = 24.dp)
                    ) {
                        ThemeSwitchRow(
                            checked = state!!,
                            onCheckedChange = { viewModel.renderSwitchState(it) }
                        )


                        SettingsButton(
                            text = stringResource(R.string.share),
                            icon = painterResource(R.drawable.ic_share),
                            contentPaddingEnd = 2.dp,
                            onClick = {
                                view.playSoundEffect(SoundEffectConstants.CLICK)
                                viewModel.renderShareLink(view.context.getString(R.string.shareUrl))
                            }
                        )

                        SettingsButton(
                            text = stringResource(R.string.support),
                            icon = painterResource(R.drawable.ic_support),
                            onClick = {
                                viewModel.renderEmailSupport(
                                    view.context.getString(R.string.supportAddressMail),
                                    view.context.getString(R.string.supportTitleMail),
                                    view.context.getString(R.string.supportBodyMail)
                                )
                            }
                        )

                        SettingsButton(
                            text = stringResource(R.string.user_agreement),
                            icon = painterResource(R.drawable.ic_user_agreement),
                            contentPaddingEnd = 6.dp,
                            onClick = { viewModel.renderUserAgreement(view.context.getString(R.string.userAgreementUrl)) }
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun ThemeSwitchRow(
    checked: Boolean,
    onCheckedChange: (Boolean) -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Text(
            modifier = Modifier
                .padding(vertical = 20.dp),
            text = stringResource(R.string.theme),
            fontWeight = FontWeight.W400,
            fontFamily = FontFamily(Font(R.font.ys_display_regular)),
            fontSize = dimensionResource(R.dimen.default_text_size).value.sp,
            color = MaterialTheme.colorScheme.onBackground,
            letterSpacing = 0.sp
        )

        CustomSwitch(
            checked = checked,
            onCheckedChange = onCheckedChange
        )
    }
}

@Composable
fun CustomSwitch(
    checked: Boolean,
    onCheckedChange: (Boolean) -> Unit
) {
    val trackWidth = 32.dp
    val trackHeight = 12.dp
    val thumbDiameter = 18.dp

    Box(
        modifier = Modifier
            .size(width = trackWidth, height = thumbDiameter)
            .clickable { onCheckedChange(!checked) }
    ) {
        Box(
            modifier = Modifier
                .size(width = trackWidth, height = trackHeight)
                .align(Alignment.Center)
                .background(
                    color = if (checked) MaterialTheme.colorScheme.tertiaryFixed else MaterialTheme.colorScheme.tertiaryFixed,
                    shape = RoundedCornerShape(percent = 50)
                )
        )

        Box(
            modifier = Modifier
                .size(thumbDiameter)
                .align(Alignment.CenterStart)
                .offset(
                    x = if (checked) (trackWidth - thumbDiameter) else 0.dp
                )
                .background(
                    color = if (checked) MaterialTheme.colorScheme.tertiaryFixedDim else MaterialTheme.colorScheme.tertiaryFixedDim,
                    shape = CircleShape
                )
        )
    }
}

@Composable
private fun SettingsButton(
    text: String,
    icon: Painter,
    contentPaddingEnd: Dp = 0.dp,
    onClick: () -> Unit
) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(
                onClick = onClick
            )
    ) {
        Text(
            modifier = Modifier
                .align(Alignment.CenterStart)
                .padding(vertical = 20.dp),
            text = text,
            fontWeight = FontWeight.W400,
            fontFamily = FontFamily(Font(R.font.ys_display_regular)),
            fontSize = dimensionResource(R.dimen.default_text_size).value.sp,
            color = MaterialTheme.colorScheme.onBackground,
            letterSpacing = 0.sp
        )

        Icon(
            modifier = Modifier
                .padding(contentPaddingEnd)
                .align(Alignment.CenterEnd),
            painter = icon,
            contentDescription = null,
            tint = MaterialTheme.colorScheme.secondary
        )
    }
}
