package com.tjEnterprises.phase10Counter.ui.editGame

import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel

@Composable
fun EditGameScreen(
    modifier: Modifier = Modifier,
    gameId: Long,
    viewModel: EditGameViewModel = hiltViewModel(),
    openDrawer: () -> Unit
){
    Text(text = "EditGameScreen")
}