package com.practicum.playlistmaker.ui

import android.icu.text.SimpleDateFormat
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
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
                .size(45.dp)
                .padding(end = 8.dp),
            contentScale = ContentScale.Crop,
            placeholder = painterResource(R.drawable.plug_artwork_low)
        )
        Column(
            modifier = Modifier.weight(1f)
        ) {
            Text(
                text = track.trackName,
                style = MaterialTheme.typography.bodyLarge,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis,
                color = MaterialTheme.colorScheme.onBackground
            )
            Row {
                Text(
                    text = track.artistName,
                    style = MaterialTheme.typography.bodySmall,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis,
                    color = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.6f)
                )
                Spacer(modifier = Modifier.width(4.dp))
                Image(
                    painter = painterResource(id = R.drawable.ic_dot),
                    contentDescription = null,
                    modifier = Modifier.align(Alignment.CenterVertically)
                )
                Spacer(modifier = Modifier.width(4.dp))
                Text(
                    text = SimpleDateFormat(
                        "mm:ss",
                        Locale.getDefault()
                    ).format(track.trackTimeMillis.toLong()),
                    style = MaterialTheme.typography.bodySmall,
                    maxLines = 1,
                    color = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.6f)
                )
            }
        }
        Image(
            painter = painterResource(id = R.drawable.ic_user_agreement),
            contentDescription = null,
            modifier = Modifier.padding(start = 16.dp),
            alpha = 0.6f
        )
    }
}

@Preview(showSystemUi = true, device = "spec:width=411dp,height=891dp", showBackground = false)
@Composable
fun TrackItemView() {
    val track = Track(
        trackId = 1234L,
        trackName = "Няня",
        artistName = "Morgen",
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
