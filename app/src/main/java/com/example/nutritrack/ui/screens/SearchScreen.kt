package com.example.nutritrack.ui.screens

import androidx.compose.animation.animateContentSize
import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.MoreVert
import androidx.compose.runtime.*
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.nutritrack.R
import com.example.nutritrack.api.NutracheckService
import com.example.nutritrack.data.local.fakeSearchData
import com.example.nutritrack.data.remote.FoodInfo
import com.example.nutritrack.ui.theme.NutriTrackTheme
import com.example.nutritrack.util.FoodResource
import com.example.nutritrack.util.LogEntry
import com.example.nutritrack.util.RemoteResource
import com.example.nutritrack.viewmodels.SearchViewModel
import com.google.accompanist.coil.rememberCoilPainter
import com.google.accompanist.imageloading.ImageLoadState
import timber.log.Timber

@Composable
fun SearchScreenContent(
    searchViewModel: SearchViewModel,
    onAddItem: (LogEntry) -> Unit
) {
    val state by searchViewModel.viewState.collectAsState()

    SearchScreenHoist(
        searchResults = state.searchResults,
        selectedItem = state.selectedItem,
        selectedUnit = state.selectedUnit,
        onSelectItem = searchViewModel::selectItem,
        onSearch = searchViewModel::fetchFoodList,
        onRetrySearch = { searchViewModel.fetchFoodList(null, true) },
        onUpdateQuantity = searchViewModel::updateDisplayStats,
        displayStats = state.displayStats,
        onUnitSelected = searchViewModel::selectUnit,
        onAddItem = onAddItem
    )
}

@Composable
fun SearchScreenHoist(
    searchResults: FoodResource,
    selectedItem: Int,
    selectedUnit: Int,
    onSelectItem: (Int) -> Unit,
    onSearch: (String) -> Unit,
    onRetrySearch: () -> Unit,
    onUpdateQuantity: (Float) -> Unit,
    displayStats: List<Float>,
    onUnitSelected: (Int) -> Unit,
    onAddItem: (LogEntry) -> Unit
) {
    var showUnitMenu by rememberSaveable { mutableStateOf(false) }

    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = Modifier
            .padding(16.dp)
            .fillMaxSize()
    ) {
        SearchBar(onSearch = onSearch)

        Spacer(modifier = Modifier.height(16.dp))

        when (searchResults) {
            is RemoteResource.Loading -> {
                Box(
                    contentAlignment = Alignment.Center,
                    modifier = Modifier.fillMaxSize()
                ) {
                    Text(
                        text = "Loading",
                        fontSize = 20.sp,
                        color = Color.LightGray
                    )
                    CircularProgressIndicator(
                        modifier = Modifier.size(100.dp)
                    )
                }
            }
            is RemoteResource.Error -> {
                Box(
                    contentAlignment = Alignment.Center,
                    modifier = Modifier.fillMaxSize()
                ) {
                    Column(
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Text(
                            text = "${searchResults.message}... ☹️",
                            fontSize = 20.sp,
                            textAlign = TextAlign.Center
                        )

                        Spacer(modifier = Modifier.height(16.dp))

                        OutlinedButton(
                            onClick = { onRetrySearch() }
                        ) {
                            Text("Try Again?")
                        }
                    }
                }
            }
            is RemoteResource.Success -> {
                FoodList(
                    foodList = searchResults.data,
                    selectedItem = selectedItem,
                    selectedUnit = selectedUnit,
                    onSelectItem = onSelectItem,
                    onUpdateQuantity = onUpdateQuantity,
                    displayStats = displayStats,
                    onMoreUnits = { showUnitMenu = true },
                    onAddItem = onAddItem
                )
            }
        }
    }

    if (searchResults is RemoteResource.Success && showUnitMenu) {
        FullscreenUnitMenu(
            units = searchResults.data[selectedItem].portions,
            selectedUnit = selectedUnit,
            onUnitSelected = {
                onUnitSelected(it)
                showUnitMenu = false
            }
        )
    }
}

@Composable
fun SearchBar(
    onSearch: (String) -> Unit
) {
    var text by rememberSaveable { mutableStateOf("") }
    //var noSearchResults by remember { mutableStateOf(false) }

    OutlinedTextField(
        value = text,
        onValueChange = { text = it },
        label = { Text("Search") },
        singleLine = true,
        keyboardOptions = KeyboardOptions.Default.copy(imeAction = ImeAction.Done),
        keyboardActions = KeyboardActions(onDone = { onSearch(text) }),
        // isError = noSearchResults,
        modifier = Modifier.fillMaxWidth()
    )
}

@Composable
fun FoodList(
    foodList: List<FoodInfo>,
    selectedItem: Int,
    selectedUnit: Int,
    onSelectItem: (Int) -> Unit,
    onUpdateQuantity: (Float) -> Unit,
    displayStats: List<Float>,
    onMoreUnits: () -> Unit,
    onAddItem: (LogEntry) -> Unit
) {
    if (foodList.isEmpty()) {
        Box (
            contentAlignment = Alignment.Center,
            modifier = Modifier.fillMaxSize()
        ) {
            Text(
                text = "Search For Food",
                fontSize = 20.sp,
                color = Color.Gray
            )
        }
    } else {
        // TODO(Reset scroll state)
        LazyColumn {

            itemsIndexed(foodList) { index, data ->
                Spacer(modifier = Modifier.height(8.dp))

                FoodCard(
                    foodInfo = data,
                    selectedUnit = selectedUnit,
                    expanded = index == selectedItem,
                    onClick = { onSelectItem(index) },
                    onUpdateQuantity = onUpdateQuantity,
                    displayStats = displayStats,
                    onMoreUnits = onMoreUnits,
                    onAddItem = onAddItem
                )
            }

//        // TODO(If appending)
//        if () {
//            item {
//                CircularProgressIndicator(
//                    modifier = Modifier
//                        .fillMaxWidth()
//                        .wrapContentWidth(Alignment.CenterHorizontally)
//                )
//            }
//        }
        }
    }
}

@Composable
fun FoodCard(
    foodInfo: FoodInfo,
    selectedUnit: Int,
    expanded: Boolean = false,
    onClick: () -> Unit,
    onUpdateQuantity: (Float) -> Unit,
    displayStats: List<Float>,
    onMoreUnits: () -> Unit,
    onAddItem: (LogEntry) -> Unit
) {
    val kcal = displayStats.getOrElse(0) { 0.0f }

    Card(
        shape = RoundedCornerShape(5.dp),
        elevation = if (expanded) 10.dp else 5.dp,
        modifier = Modifier
            .fillMaxWidth()
            .border(2.dp, Color.LightGray)
            .clickable { onClick() }
            .animateContentSize(
                animationSpec = tween(
                    durationMillis = 150,
                    easing = LinearEasing
                )
            )
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            modifier = Modifier.padding(16.dp)
        ) {
            Row (
                verticalAlignment = Alignment.CenterVertically
            ) {
                FoodImage(imgRes = foodInfo.imgRes)

                Text(
                    text = foodInfo.title,
                    fontSize = 15.sp,
                    fontWeight = FontWeight.Bold,
                    textAlign = TextAlign.Center,
                    overflow = TextOverflow.Ellipsis,
                    modifier = Modifier.padding(5.dp)
                )
            }

            if (expanded) {
                Spacer(modifier = Modifier.height(8.dp))

                Column (
                    horizontalAlignment = Alignment.CenterHorizontally
                ){
                    QuantityField(
                        units = foodInfo.portions,
                        selectedUnit = selectedUnit,
                        onValueChange = onUpdateQuantity,
                        onClickMore = onMoreUnits,
                        onAddItem = { onAddItem(Pair(foodInfo.title, kcal)) }
                    )

                    Spacer(modifier = Modifier.height(8.dp))

                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Row (
                            verticalAlignment = Alignment.CenterVertically,
                            modifier = Modifier.weight(0.8f)
                        ){
                            Text(
                                text = "⚡Kcal",
                                fontSize = 25.sp
                            )
                            Text(
                                text = "%.1f".format(kcal),
                                fontSize = 30.sp,
                                overflow = TextOverflow.Ellipsis,
                                fontWeight = FontWeight.Bold,
                                modifier = Modifier.padding(horizontal = 8.dp)
                            )
                        }

                        OutlinedButton(
                            onClick = { onAddItem(Pair(foodInfo.title, kcal)) },
                            modifier = Modifier.weight(0.2f)
                        ) {
                            Text("Add")
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun FoodImage(imgRes: String?) {
    val imageSize = 100.dp

    if (imgRes == null) {
        Column (
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Image(
                painter = painterResource(id = R.drawable.image_missing),
                contentDescription = "Image Missing",
                alpha = 0.5f,
                modifier = Modifier.size(imageSize)
            )
            Text(
                text = "Image Missing",
                fontSize = 10.sp,
                color = Color.LightGray
            )
        }
    } else {
        val request = NutracheckService.IMAGE_URI + imgRes
        val painter = rememberCoilPainter(request, fadeIn = true)

        Surface(
//            shape = RoundedCornerShape(5.dp),
            color = Color.White,
            modifier = Modifier.size(imageSize)
        ) {
            when (painter.loadState) {
                is ImageLoadState.Success -> {
                    Image(
                        painter = painter,
                        contentDescription = "Food Image",
                    )
                }
                is ImageLoadState.Loading -> {
                    CircularProgressIndicator()
                }
                is ImageLoadState.Error -> {
                    Timber.e("Can't Load image")
                }
                is ImageLoadState.Empty -> {
                    Timber.e("Empty image")
                }
            }
        }
    }
}

@Composable
fun QuantityField(
    units: List<String>,
    selectedUnit: Int,
    onValueChange: (Float) -> Unit,
    onClickMore: () -> Unit,
    onAddItem: () -> Unit
) {
    var quantityText by rememberSaveable { mutableStateOf("") }
    var valid by rememberSaveable { mutableStateOf(true) }
    var errorMessage by remember { mutableStateOf("") }

    OutlinedTextField(
        value = quantityText,
        onValueChange = {
            quantityText = it
            valid = try {
                errorMessage = ""
                val quantity = it.toFloat()
                if (quantity > 10_000) {
                    throw IllegalArgumentException("Too Big")
                }
                if (quantity <= 0) {
                    throw IllegalArgumentException("Too Small")
                }
                onValueChange(quantity)
                true
            } catch (e: NumberFormatException) {
                errorMessage = "Invalid Number"
                false
            } catch (e: IllegalArgumentException) {
                errorMessage = e.message ?: ""
                false
            }
        },
        keyboardOptions = KeyboardOptions.Default.copy(
            keyboardType = KeyboardType.Number,
            imeAction = ImeAction.Done
        ),
        keyboardActions = KeyboardActions(onDone = { onAddItem() }),
        singleLine = true,
        label = { Text(errorMessage) },
        placeholder = { Text("Quantity") },
        isError = !valid,
        leadingIcon = {
            Text(
                // TODO(Work out how the fuck to use emoji2)
                text = "⚖️",
                fontSize = 25.sp,
                modifier = Modifier
                    .padding(3.dp)
            )
        },
        trailingIcon = {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.padding(horizontal = 4.dp)
            ) {
                Text(
                    units[selectedUnit],
                    fontSize = 20.sp,
                    fontWeight = FontWeight.Bold,
                    textAlign = TextAlign.Center,
                    fontStyle =
                        if (units.size > 1) {
                            FontStyle.Italic
                        } else {
                            FontStyle.Normal
                        }
                )

                if (units.size > 1) {
                    Icon(
                        imageVector = Icons.Filled.MoreVert,
                        contentDescription = "More Units",
                        modifier = Modifier.clickable { onClickMore() }
                    )
                }
            }
        },
        modifier = Modifier.fillMaxWidth()
    )
}

@Composable
fun FullscreenUnitMenu(
    units: List<String>,
    selectedUnit: Int,
    onUnitSelected: (Int) -> Unit
) {
    Surface(
        color = Color.Black.copy(alpha = 0.8f),
        modifier = Modifier
            .fillMaxSize()
    ) {
        Box(
            modifier = Modifier
                .padding(32.dp)
                .fillMaxSize()
        ) {
            Text(
                text = "Select Unit",
                color = Color.White,
                fontSize = 40.sp,
                fontWeight = FontWeight.Bold,
                textDecoration = TextDecoration.Underline,
                modifier = Modifier
                    .padding(16.dp)
                    .align(Alignment.TopCenter)
            )
            Column(
                modifier = Modifier
                    .align(Alignment.Center)
            ) {
                Column {
                    units.forEachIndexed { index, unit ->
                        Box(
                            contentAlignment = Alignment.Center,
                            modifier = Modifier
                                .background(Color.DarkGray)
                                .border(
                                    width = 2.dp,
                                    shape = RoundedCornerShape(5.dp),
                                    color =
                                    if (index == selectedUnit) {
                                        Color.Cyan.copy(alpha = 0.75f)
                                    } else {
                                        Color.Gray
                                    }
                                )
                                .fillMaxWidth()
                                .clickable { onUnitSelected(index) }
                        ) {
                            Text(
                                text = unit,
                                color = Color.White,
                                fontSize = 25.sp,
                                modifier = Modifier.padding(5.dp)
                            )
                        }

                        if (index != units.size-1) {
                            Spacer(modifier = Modifier.height(16.dp))
                        }
                    }
                }
            }
        }
    }
}

@Preview
@Composable
fun SearchScreenPreview() {
    var selectedItem by remember { mutableStateOf(-1) }
    var selectedUnit by remember { mutableStateOf(0) }
    var quantity by remember { mutableStateOf(1.0f) }
    var searchResults: FoodResource by remember {
        mutableStateOf(RemoteResource.Success(emptyList()))
    }
    var displayStats by remember { mutableStateOf(emptyList<Float>())}

    val updateStats: (Float) -> Unit = {
        if (searchResults is RemoteResource.Success && selectedItem >= 0) {
            val foodInfo = fakeSearchData.getOrNull(selectedItem)

            displayStats = if (foodInfo == null) {
                emptyList()
            } else {
                listOf(
                    foodInfo.kcal[selectedUnit] * quantity
                )
            }
        }
    }

    NutriTrackTheme {
        Scaffold(
            bottomBar = { BottomNavigation {} }
        ) {

            SearchScreenHoist(
                searchResults = searchResults,
                selectedItem = selectedItem,
                selectedUnit = selectedUnit,
                onSelectItem = {
                    selectedItem = if (selectedItem != it) it else -1
                    selectedUnit = 0
                    updateStats(quantity)
                },
                onSearch = { searchResults = RemoteResource.Success(fakeSearchData) },
                onRetrySearch = {},
                onUpdateQuantity = {
                    quantity = it
                    updateStats(quantity)
                },
                displayStats = displayStats,
                onUnitSelected = {
                    selectedUnit = it
                    updateStats(quantity)
                 },
                onAddItem = { println("Adding item: $it") }
            )
        }
    }
}