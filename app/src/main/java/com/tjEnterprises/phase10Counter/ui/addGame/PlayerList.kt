package com.tjEnterprises.phase10Counter.ui.addGame

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.layout.wrapContentWidth
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.snapshots.SnapshotStateList
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.tjEnterprises.phase10Counter.R

@Composable
internal fun PlayersList(
    modifier: Modifier,
    tempPlayerNames: SnapshotStateList<String>,
    removeTempPlayerName: (Int) -> Unit
) {
    // TODO Make this use adaptive grid cells
    LazyColumn(
        modifier = modifier, horizontalAlignment = Alignment.CenterHorizontally
    ) {
        itemsIndexed(tempPlayerNames) { index, item ->
            BoxWithConstraints {
                val maxWidth = maxWidth
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.Center,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text(
                        text = item,
                        modifier = Modifier
                            .widthIn(max = maxWidth.minus(64.dp))
                            .wrapContentWidth()
                            .padding(start = 16.dp)
                    )
                    IconButton(
                        onClick = { removeTempPlayerName(index) },
                        modifier = Modifier.padding(end = 16.dp, start = 16.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Default.Delete, contentDescription = stringResource(
                                id = R.string.deletePlayer
                            )
                        )
                    }
                }
            }
        }
    }
}

@Preview(showBackground = true, widthDp = 400)
@Preview(showBackground = true, widthDp = 500, heightDp = 500)
@Composable
fun PlayerListPreview() {
    val tempPlayerNames = remember {
        mutableStateListOf<String>(
            "Player 1",
            "Player 2",
            "Player 3",
            "Plaaaaaaaaaaaaaaaaayyyyyyyyyyyyyyyyeeeeeeeeeeeeeeeerrrrrrrrrrrrrrrrr",
            "Plaaaaaaaaaaaaaaaaayyyyyyyyyyyyyyyyeeeeee"
        )
    }
    PlayersList(modifier = Modifier, tempPlayerNames = tempPlayerNames, removeTempPlayerName = {})
}