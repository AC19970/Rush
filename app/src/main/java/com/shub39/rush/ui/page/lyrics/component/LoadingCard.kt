package com.shub39.rush.ui.page.lyrics.component

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.shub39.rush.R
import com.shub39.rush.viewmodel.RushViewModel

@Composable
fun LoadingCard(
    rushViewModel: RushViewModel,
    colors: Pair<Color, Color>
) {
    val fetching by rushViewModel.isFetchingLyrics.collectAsState()
    val searching by rushViewModel.isSearchingLyrics.collectAsState()
    val searchTerm by rushViewModel.searchQuery.collectAsState()
    val fetchTerm by rushViewModel.fetchQuery.collectAsState()

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(32.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        if (fetching || searching) {
            Text(
                text = if (fetching) {
                    "${stringResource(R.string.fetching)} \n$fetchTerm"
                } else {
                    "${stringResource(R.string.searching)} \n$searchTerm"
                },
                textAlign = TextAlign.Center
            )
        }

        Spacer(modifier = Modifier.height(16.dp))

        CircularProgressIndicator(
            strokeCap = StrokeCap.Round,
            color = colors.first
        )
    }
}