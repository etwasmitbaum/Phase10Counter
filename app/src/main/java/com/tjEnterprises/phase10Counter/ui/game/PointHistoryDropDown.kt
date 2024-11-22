package com.tjEnterprises.phase10Counter.ui.game

import android.widget.Toast
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.defaultMinSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowDropDown
import androidx.compose.material.icons.filled.Info
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TextField
import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.PopupProperties
import androidx.core.text.isDigitsOnly
import com.tjEnterprises.phase10Counter.R
import com.tjEnterprises.phase10Counter.data.local.models.PointHistoryItem

@Composable
fun PointHistoryDropDown(
    pointHistory: List<PointHistoryItem>,
    sumOfPoints: Long,
    defaultExpanded: Boolean = false,
    deletePointHistoryItem: (PointHistoryItem) -> Unit,
    updatePointHistoryItem: (PointHistoryItem) -> Unit,
    modifier: Modifier = Modifier
) {

    // state of the menu
    var expanded by rememberSaveable {
        mutableStateOf(defaultExpanded)
    }


    Row(verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.End,
        modifier = modifier.clickable(
            interactionSource = remember { MutableInteractionSource() }, indication = null
        ) { expanded = true }) {
        Text(
            text = sumOfPoints.toString()
        )
        IconButton(onClick = {
            expanded = true
        }) {
            Icon(
                imageVector = Icons.Default.ArrowDropDown,
                contentDescription = stringResource(id = R.string.showPointHistory)
            )
        }


        DropdownMenu(
            expanded = expanded,
            onDismissRequest = {
                expanded = false
            },
            shape = RoundedCornerShape(20),
            modifier = Modifier
                .widthIn(min = 1.dp, max = 128.dp)
                .background(color = MaterialTheme.colorScheme.surfaceContainerHighest)
                .wrapContentSize(),
            properties = PopupProperties(usePlatformDefaultWidth = false)
        ) {

            val lastElement = pointHistory.lastIndex

            // Display "0" when no point history exists, so the dropdown menu shows something
            if (pointHistory.isEmpty()) {
                Text(
                    text = "0",
                    modifier = Modifier
                        .clip(shape = RoundedCornerShape(50))
                        .fillMaxWidth()
                        .padding(horizontal = 8.dp, vertical = 4.dp),
                    textAlign = TextAlign.Center
                )
            }

            pointHistory.forEachIndexed { idx, item ->

                val showEditDeleteDialog = rememberSaveable { mutableStateOf(false) }

                if (showEditDeleteDialog.value) {
                    EditDeletePointHistoryDialog(
                        showDialog = showEditDeleteDialog,
                        pointHistoryItem = item,
                        deletePointHistoryItem = deletePointHistoryItem,
                        updatePointHistoryItem = updatePointHistoryItem
                    )
                }

                Text(text = item.point.toString(),
                    style = TextStyle(fontSize = 16.sp),
                    modifier = Modifier
                        .defaultMinSize(minWidth = 65.dp)
                        .clip(shape = RoundedCornerShape(50))
                        .clickable { showEditDeleteDialog.value = true }
                        .fillMaxWidth()
                        .padding(horizontal = 8.dp, vertical = 6.dp),
                    textAlign = TextAlign.Center)
                // don't place a divider at the bottom
                if (idx != lastElement) {
                    HorizontalDivider()
                }
            }

        }

    }
}

@Composable
fun EditDeletePointHistoryDialog(
    showDialog: MutableState<Boolean>,
    pointHistoryItem: PointHistoryItem,
    deletePointHistoryItem: (PointHistoryItem) -> Unit,
    updatePointHistoryItem: (PointHistoryItem) -> Unit
) {

    val context = LocalContext.current

    var text by rememberSaveable {
        mutableStateOf(pointHistoryItem.point.toString())
    }

    AlertDialog(icon = {
        Icon(Icons.Default.Info, contentDescription = null)
    }, title = {
        Text(text = stringResource(id = R.string.editValue))
    }, text = {
        TextField(value = text,
            onValueChange = { text = it },
            label = { Text(stringResource(id = R.string.points)) },
            maxLines = 1,
            singleLine = true,
            keyboardOptions = KeyboardOptions(
                keyboardType = KeyboardType.Number, imeAction = ImeAction.Next
            )
        )
    }, onDismissRequest = {
        if (text.isNotBlank() && text.isDigitsOnly()) {
            updatePointHistoryItem(
                PointHistoryItem(
                    point = text.toLong(), pointId = pointHistoryItem.pointId
                )
            )
            showDialog.value = false
        } else {
            Toast.makeText(
                context, context.getString(R.string.onlyNumbersAreAllows), Toast.LENGTH_SHORT
            ).show()
        }

    }, confirmButton = {
        TextButton(onClick = {
            if (text.isNotBlank() && text.isDigitsOnly()) {
                updatePointHistoryItem(
                    PointHistoryItem(
                        point = text.toLong(), pointId = pointHistoryItem.pointId
                    )
                )
                showDialog.value = false
            } else {
                Toast.makeText(
                    context, context.getString(R.string.onlyNumbersAreAllows), Toast.LENGTH_SHORT
                ).show()
            }
        }) {
            Text(text = stringResource(id = R.string.confirm))
        }

    }, dismissButton = {
        TextButton(onClick = {
            deletePointHistoryItem(pointHistoryItem)
            showDialog.value = false
            Toast.makeText(
                context,
                context.getString(R.string.deleted) + ": " + pointHistoryItem.point.toString(),
                Toast.LENGTH_SHORT
            ).show()
        }) {
            Text(text = stringResource(id = R.string.delete))
        }
    })
}


@Preview(showBackground = true)
@Preview(showBackground = true, widthDp = 200, heightDp = 400)
@Composable
fun PointHistoryDropDownPreview() {
    PointHistoryDropDown(pointHistory = listOf(
        PointHistoryItem(70L, 1L),
        PointHistoryItem(720L, 2L),
        PointHistoryItem(10L, 3L),
        PointHistoryItem(98L, 4L)
    ), sumOfPoints = 790L, deletePointHistoryItem = {}, updatePointHistoryItem = {})
}

@Preview(showBackground = true)
@Composable
fun EditDeletePointHistoryDialogPreview() {
    val show = rememberSaveable {
        mutableStateOf(true)
    }
    EditDeletePointHistoryDialog(showDialog = show,
        pointHistoryItem = PointHistoryItem(45L, 1L),
        deletePointHistoryItem = {},
        updatePointHistoryItem = {})
}