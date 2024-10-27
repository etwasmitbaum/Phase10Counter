package com.tjEnterprises.phase10Counter.ui.game

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.MoreVert
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.PopupProperties
import com.tjEnterprises.phase10Counter.R
import com.tjEnterprises.phase10Counter.data.local.models.PointHistoryItem

@Composable
fun PointHistoryDropDown(
    pointHistory: List<PointHistoryItem>,
    sumOfPoints: Long,
    defaultExpanded: Boolean = false,
    modifier: Modifier = Modifier
) {

    // state of the menu
    var expanded by rememberSaveable() {
        mutableStateOf(defaultExpanded)
    }

    Box(
        contentAlignment = Alignment.Center,
        modifier = modifier
            .widthIn(1.dp, Dp.Infinity)
            .wrapContentSize()
            .padding(start = 16.dp)
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.End,
            modifier = modifier
        ) {
            Text(
                text = sumOfPoints.toString()
            )   // total points
            IconButton(onClick = {
                expanded = true
            }) {
                Icon(
                    imageVector = Icons.Default.MoreVert,
                    contentDescription = stringResource(id = R.string.showPointHistory)
                )
            }
        }

        // drop down menu
        DropdownMenu(
            expanded = expanded,
            onDismissRequest = {
                expanded = false
            },
            modifier = Modifier
                .widthIn(min = 1.dp, max = 128.dp)
                .wrapContentSize(),
            properties = PopupProperties(usePlatformDefaultWidth = false)
        ) {
            val lastElement = pointHistory.lastIndex
            // adding items
            pointHistory.forEachIndexed { idx, item ->
                Text(
                    text = item.point.toString(),
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 8.dp, vertical = 4.dp),
                    textAlign = TextAlign.Center
                )
                // don't place a divider at the bottom
                if (idx != lastElement) {
                    HorizontalDivider()
                }
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
fun PointHistoryDropDownPreview() {
    PointHistoryDropDown(
        pointHistory = listOf(
            PointHistoryItem(70L, 1L),
            PointHistoryItem(720L, 2L),
            PointHistoryItem(10L, 3L),
            PointHistoryItem(98L, 4L)
        ), sumOfPoints = 790L
    )
}

@Preview(showBackground = true, widthDp = 200, heightDp = 200)
@Composable
fun PointHistoryDropDownExpandedPreview() {
    PointHistoryDropDown(
        pointHistory = listOf(
            PointHistoryItem(70L, 1L),
            PointHistoryItem(720L, 2L),
            PointHistoryItem(10L, 3L),
            PointHistoryItem(98L, 4L)
        ), sumOfPoints = 790L, defaultExpanded = true
    )
}