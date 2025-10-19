package com.practicum.playlistmaker.ui

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.LocalTextStyle
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.PlatformTextStyle
import androidx.compose.ui.text.font.Font
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.LineHeightStyle
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage
import com.practicum.playlistmaker.R
import com.practicum.playlistmaker.create_playlist.domain.model.Playlist

@Composable
fun PlaylistItem(playlist: Playlist) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
    ) {
        AsyncImage(
            model = playlist.imgPath,
            contentDescription = null,
            modifier = Modifier
                .aspectRatio(1f)
                .clip(RoundedCornerShape(8.dp)),
            contentScale = ContentScale.Crop,
            placeholder = painterResource(R.drawable.plug_artwork_high),
            error = painterResource(R.drawable.plug_artwork_high)
        )
        
        Text(
            text = playlist.namePlaylist,
            modifier = Modifier
                .padding(top = 4.dp)
                .fillMaxWidth(),
            color = MaterialTheme.colorScheme.onBackground,
            fontSize = 12.sp,
            fontWeight = FontWeight(400),
            fontFamily = FontFamily(Font(resId = R.font.ys_display_regular)),
            letterSpacing = 0.sp,
            style = LocalTextStyle.current.merge(
                platformStyle = PlatformTextStyle(includeFontPadding = false),
                lineHeightStyle = LineHeightStyle(
                    alignment = LineHeightStyle.Alignment.Center,
                    trim = LineHeightStyle.Trim.Both
                )
            )
        )

        Text(
            text = word(playlist.tracksCount.toInt()),
            modifier = Modifier
                .fillMaxWidth(),
            color = MaterialTheme.colorScheme.onBackground,
            fontSize = 12.sp,
            fontWeight = FontWeight.W400,
            fontFamily = FontFamily(Font(resId = R.font.ys_display_regular)),
            letterSpacing = 0.sp,
            style = LocalTextStyle.current.merge(
                platformStyle = PlatformTextStyle(includeFontPadding = false),
                lineHeightStyle = LineHeightStyle(
                    alignment = LineHeightStyle.Alignment.Center,
                    trim = LineHeightStyle.Trim.Both
                )
            )
        )
    }
}

@Composable
private fun word(count: Int): String {
    val lastTwoDigits = count % 100
    val lastDigit = count % 10

    val word = when {
        lastTwoDigits in 11..14 -> "треков"
        lastDigit == 1 -> "трек"
        lastDigit in 2..4 -> "трека"
        else -> "треков"
    }

    return "$count $word"
}

@Preview(showSystemUi = true, device = "spec:width=411dp,height=891dp")
@Composable
fun PlaylistItemPreview() {
    val playlist = Playlist(
        playlistId = 123L,
        namePlaylist = "Vdvfdvdf",
        description = "vfdvfdfvfvdfvdvfd",
        imgPath = "",
        trackList = listOf(),
        tracksCount = 15L
    )

    PlaylistItem(playlist)
}
