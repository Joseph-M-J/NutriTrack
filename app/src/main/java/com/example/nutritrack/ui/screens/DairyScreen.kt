package com.example.nutritrack.ui.screens

import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.layout.Arrangement.Absolute.SpaceBetween
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Alignment.Companion.Center
import androidx.compose.ui.Alignment.Companion.CenterHorizontally
import androidx.compose.ui.Alignment.Companion.CenterVertically
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.RectangleShape
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.nutritrack.data.local.DiaryViewState
import com.example.nutritrack.ui.theme.NutriTrackTheme
import com.example.nutritrack.ui.theme.mealCategoryColors
import com.example.nutritrack.util.LogEntry
import com.example.nutritrack.util.MealCategory
import com.example.nutritrack.viewmodels.DiaryViewModel
import com.example.nutritrack.viewmodels.SearchViewModel

@Composable
fun DiaryScreenContent(
    diaryViewModel: DiaryViewModel,
    searchViewModel: SearchViewModel
) {
    val state by diaryViewModel.viewState.collectAsState()

    DiaryScreenHoist(
        state = state,
        onAddEntry = {
//            diaryViewModel.addLogEntry(it, searchViewModel.pasteLogEntry())
            diaryViewModel.addLogEntry(it, Pair("Testing", 100.0f))
        }
    )
}

@Composable
fun DiaryScreenHoist(
    state: DiaryViewState,
    onAddEntry: (MealCategory) -> Unit
) {
    Column(
        verticalArrangement = Arrangement.SpaceBetween,
        horizontalAlignment = CenterHorizontally,
        modifier = Modifier
            .padding(16.dp)
            .fillMaxSize()
    ) {
        // TODO(Label the columns)
        //Row{}

        LazyColumn(
            horizontalAlignment = CenterHorizontally,
            modifier = Modifier
                .padding(16.dp)
                .weight(0.9f)
        ) {
            state.currentLog.forEach {
                val category = it.key
                val entries = it.value
                val color = mealCategoryColors.getOrElse(category) { Color.Gray }

                item {
                    Surface(
                        shape = RoundedCornerShape(5.dp),
                        color = color.copy(alpha=0.5f),
                        modifier = Modifier
                            .height(30.dp)
                            .fillMaxWidth(0.5f)
                            .border(
                                width = 3.dp,
                                brush = Brush.verticalGradient(
                                    listOf(
                                        color,
                                        Color.DarkGray
                                    )
                                ),
                                shape = RoundedCornerShape(5.dp)
                            )
                            .clickable { onAddEntry(category) }
                    ) {
                        Box(
                            contentAlignment = Center,
                            modifier = Modifier
                                .fillMaxSize()
                        ) {
                            Text(
                                text = category.name,
                                color = Color.Black,
                                fontWeight = FontWeight.Bold
                            )
                        }
                    }


                    Spacer(modifier = Modifier.height(8.dp))

                    Box(
                        modifier = Modifier
                            .padding(4.dp)
                            .defaultMinSize(minHeight = 50.dp)
                            .fillMaxWidth()
                            .border(
                                shape = RectangleShape,
                                width = 3.dp,
                                color = color.copy(alpha = 0.3f)
                            )
                    ) {
                        if (entries.isEmpty()) {
                            Text(
                                text = "Nothing Yet...",
                                color = Color.LightGray,
                                modifier = Modifier.padding(8.dp)
                            )
                        } else {
                            Column (
                                horizontalAlignment = CenterHorizontally
                            ){
                                entries.forEach { (name, kcal) ->
                                    Row(
                                        verticalAlignment = CenterVertically,
                                        horizontalArrangement = SpaceBetween,
                                        modifier = Modifier
                                            .padding(8.dp)
                                            .fillMaxWidth()
                                    ) {
                                        Text(
                                            text = name
                                        )
                                        Text(
                                            text = "%.1f".format(kcal)
                                        )
                                    }
                                    Divider()
                                }

                                Text(
                                    text =
                                        "Subtotal = " +
                                        "%.1f".format(state.subTotalKcal[category]),
                                    modifier = Modifier.padding(8.dp)
                                )
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(24.dp))
                }
//                LazyColumn(
//                    modifier = Modifier
//                        .border(
//                            width = 2.dp,
//                            color = MealCategory.color(category)
//                        )
//                ) {
//
//                }
            }
        }

        Divider(
            thickness = 5.dp,
            modifier = Modifier.fillMaxWidth()
        )

        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier
                .weight(0.1f)
        ) {
            Text(
                text = "Total = ",
                fontSize = 30.sp
            )
            Text(
                text = "%.1f".format(state.totalKcal),
                fontWeight = FontWeight.Bold,
                fontSize = 30.sp
            )
        }
    }
}

@Preview
@Composable
fun DiaryScreenPreview() {
    var state by remember { mutableStateOf(DiaryViewState()) }

    val getSubTotalKcal: (MealCategory) -> Float = { category ->
        state.currentLog.getOrElse(category) { emptyList() }
            .map { it.second }
            .sum()
    }

    val getTotalKcal: () -> Float = {
        state.currentLog.keys
            .map { getSubTotalKcal(it) }
            .sum()
    }

    val onAddEntry: (MealCategory, LogEntry) -> Unit = { category, entry ->
        val entries = state.currentLog.getOrElse(category) { emptyList() }

        state = state.copy(
            currentLog = state.currentLog.map {
                if (it.key == category) {
                    category to entries.plus(entry)
                } else {
                    it.toPair()
                }
            }.toMap()
        )
    }


    NutriTrackTheme {
        Scaffold(
            bottomBar = { BottomNavigation {} }
        ) { innerPadding ->

            Box(
                modifier = Modifier
                    .padding(innerPadding)
                    .fillMaxSize()
            ) {
                DiaryScreenHoist(
                    state = state,
                    onAddEntry = {
                        onAddEntry(it, Pair("Test entry", 100.2944f))
                    }
                )
            }
        }
    }
}