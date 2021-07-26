package com.example.nutritrack.ui.screens

import androidx.compose.foundation.*
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
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.nutritrack.data.local.DiaryViewState
import com.example.nutritrack.data.model.LogsEntity
import com.example.nutritrack.util.testLog
import com.example.nutritrack.ui.theme.NutriTrackTheme
import com.example.nutritrack.ui.theme.mealCategoryColors
import com.example.nutritrack.util.LogEntry
import com.example.nutritrack.util.MealCategory
import com.example.nutritrack.viewmodels.DiaryViewModel
import com.example.nutritrack.viewmodels.SearchViewModel

@ExperimentalFoundationApi
@Composable
fun DiaryScreenContent(
    diaryViewModel: DiaryViewModel,
    searchViewModel: SearchViewModel
) {
    val state by diaryViewModel.viewState.collectAsState()

    DiaryScreenHoist(
        state = state,
        onAddEntry = { category ->
            searchViewModel.pasteLogEntry()?.also { (title, kcal) ->
                diaryViewModel.updateLog(
                    entity = LogsEntity(
                        category = category,
                        title = title,
                        kcal = kcal,
                        date = diaryViewModel.date
                    ),
                    add = true
                )
            }
            // diaryViewModel.addLogEntry(it, Pair("Testing", 500.0f))
        },
        onRemoveEntry = { diaryViewModel.updateLog(entity = it, add = false) },
        onLongPressEntry = diaryViewModel::selectEntity
    )
}

@ExperimentalFoundationApi
@Composable
fun DiaryScreenHoist(
    state: DiaryViewState,
    onAddEntry: (MealCategory) -> Unit,
    onRemoveEntry: (LogsEntity) -> Unit,
    onLongPressEntry: (Long) -> Unit
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
            state.currentLog.forEach { meal ->
                val category = meal.key
                val entries = meal.value
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
                                entries.forEach { entity ->
                                    val selected = state.selectedId == entity.id

                                    Row(
                                        verticalAlignment = CenterVertically,
                                        horizontalArrangement = SpaceBetween,
                                        modifier = Modifier
                                            .combinedClickable(
                                                onClick = {
                                                    if (selected) onRemoveEntry(entity)
                                                },
                                                onLongClick = { onLongPressEntry(entity.id) }
                                            )
                                            .background(
                                                if (selected) {
                                                    Brush.verticalGradient(
                                                        startY = 15.0f,
                                                        colors = listOf(
                                                            Color.White,
                                                            Color.Red.copy(alpha = 0.2f)
                                                        )
                                                    )
                                                } else {
                                                    Brush.verticalGradient(listOf(
                                                        Color.White,
                                                        Color.White
                                                    ))
                                                }
                                            )
                                            .padding(8.dp)
                                            .fillMaxWidth()

                                    ) {
                                        Text(
                                            text = entity.title,
                                            modifier = Modifier.weight(0.7f)
                                        )
                                        Text(
                                            text = "%.1f".format(entity.kcal),
                                            textAlign = TextAlign.Center,
                                            modifier = Modifier.weight(0.3f)
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

//@ExperimentalFoundationApi
//@Preview
//@Composable
//fun DiaryScreenPreview() {
//    var state by remember { mutableStateOf(
//        DiaryViewState(
//        currentLog = testLog
////        selectedEntry = testLog[MealCategory.BREAKFAST]?.getOrNull(0)
//    )
//    ) }
//
//    val getSubTotalKcal: (MealCategory) -> Float = { category ->
//        state.currentLog.getOrElse(category) { emptyList() }
//            .map { it.second }
//            .sum()
//    }
//
//    val getTotalKcal: () -> Float = {
//        state.currentLog.keys
//            .map { getSubTotalKcal(it) }
//            .sum()
//    }
//
//    val onModifyEntry: (MealCategory, LogEntry, Boolean) -> Unit = { category, entry, add ->
//        val entries = state.currentLog.getOrElse(category) { emptyList() }
//
//        state = state.copy(
//            currentLog = state.currentLog.map {
//                if (it.key == category) {
//                    category to if (add) entries.plus(entry) else entries.minus(entry)
//                } else {
//                    it.toPair()
//                }
//            }.toMap()
//        )
//    }
//
//
//    val selectEntry: (LogEntry) -> Unit = { entry ->
//        state = state.copy(
//            selectedId = if (state.selectedId == entry) null else entry
//        )
//    }
//
//
//    NutriTrackTheme {
//        Scaffold(
//            bottomBar = { BottomNavigation {} }
//        ) { innerPadding ->
//
//            Box(
//                modifier = Modifier
//                    .padding(innerPadding)
//                    .fillMaxSize()
//            ) {
//                DiaryScreenHoist(
//                    state = state,
//                    onAddEntry = { category ->
//                        onModifyEntry(category, Pair("Test entry", 100.2944f), true)
//                    },
//                    onRemoveEntry = { category, entry ->
//                        onModifyEntry(category, entry, false)
//                    },
//                    onLongPressEntry = selectEntry
//                )
//            }
//        }
//    }
//}