package com.practicum.playlistmaker.ui

import android.content.Context
import android.icu.text.SimpleDateFormat
import android.util.TypedValue
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Icon
import androidx.compose.material3.LocalTextStyle
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.dimensionResource
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.PlatformTextStyle
import androidx.compose.ui.text.font.Font
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.LineHeightStyle
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage
import com.practicum.playlistmaker.R
import com.practicum.playlistmaker.search.domain.model.Track
import java.util.Locale

@Composable
fun TrackItem(track: Track) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 8.dp, horizontal = 12.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        AsyncImage(
            model = track.artworkUrl100,
            contentDescription = null,
            modifier = Modifier
                .padding(end = 8.dp)
                .size(45.dp)
                .clip(RoundedCornerShape(dpToPx(2f, LocalContext.current))),
            contentScale = ContentScale.Crop,
            placeholder = painterResource(R.drawable.plug_artwork_low),
            error = painterResource(R.drawable.plug_artwork_high)
        )
        Column(
            modifier = Modifier.weight(1f)
        ) {
            Text(
                text = track.trackName,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis,
                color = MaterialTheme.colorScheme.onBackground,
                fontSize = dimensionResource(R.dimen.default_text_size).value.sp,
                fontWeight = FontWeight(400),
                fontFamily = FontFamily(Font(R.font.ys_display_regular)),
                letterSpacing = 0.sp,
                style = LocalTextStyle.current.merge(
                    platformStyle = PlatformTextStyle(includeFontPadding = false),
                    lineHeightStyle = LineHeightStyle(
                        alignment = LineHeightStyle.Alignment.Center,
                        trim = LineHeightStyle.Trim.Both
                    )
                )
            )
            Row {
                Text(
                    text = track.artistName,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis,
                    color = MaterialTheme.colorScheme.secondary,
                    fontSize = dimensionResource(R.dimen.small_text_size).value.sp,
                    fontWeight = FontWeight(400),
                    fontFamily = FontFamily(Font(R.font.ys_display_regular)),
                    letterSpacing = 0.sp,
                    modifier = Modifier
                        .weight(1f, false),
                    style = LocalTextStyle.current.merge(
                        platformStyle = PlatformTextStyle(includeFontPadding = false),
                        lineHeightStyle = LineHeightStyle(
                            alignment = LineHeightStyle.Alignment.Center,
                            trim = LineHeightStyle.Trim.Both
                        )
                    )
                )
                Spacer(modifier = Modifier.width(4.dp))
                Icon(
                    painter = painterResource(id = R.drawable.ic_dot),
                    contentDescription = null,
                    modifier = Modifier.align(Alignment.CenterVertically),
                    tint = MaterialTheme.colorScheme.secondary,

                )
                Spacer(modifier = Modifier.width(4.dp))
                Text(
                    text = SimpleDateFormat("mm:ss",Locale.getDefault()).format(track.trackTimeMillis.toLong()),
                    maxLines = 1,
                    color = MaterialTheme.colorScheme.secondary,
                    fontSize = dimensionResource(R.dimen.small_text_size).value.sp,
                    fontWeight = FontWeight(400),
                    fontFamily = FontFamily(Font(R.font.ys_display_regular)),
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
        Icon(
            painter = painterResource(id = R.drawable.ic_user_agreement),
            contentDescription = null,
            modifier = Modifier.padding(start = 16.dp),
            tint = MaterialTheme.colorScheme.secondary
        )
    }
}

@Composable
private fun dpToPx(dp: Float, context: Context): Int {
    val px = TypedValue.applyDimension(
        TypedValue.COMPLEX_UNIT_DIP,
        dp,
        context.resources.displayMetrics
    ).toInt()

    return px
}

@Preview(showSystemUi = true, device = "spec:width=411dp,height=891dp", showBackground = false)
@Composable
fun TrackItemView() {
    val track = Track(
        trackId = 1234L,
        trackName = "vfdsbfdbgdfbg",
        artistName = "Morgenvnfdjsvbhfdbfgbvjblhjfdvbfdsbnvsdfjbhfdsfdfgffndvfdb",
        trackTimeMillis = "120000",
        artworkUrl100 = "",
        collectionName = "TODO()",
        releaseDate = "2024",
        primaryGenreName = "TODO()",
        country = "Russia",
        previewUrl = "TODO()",
        isFavorite = false
    )

    TrackItem(track)
}
