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
import coil.compose.ImagePainter
import coil.compose.rememberImagePainter
import com.example.nutritrack.R
import com.example.nutritrack.api.NutracheckService
import com.example.nutritrack.data.local.fakeSearchData
import com.example.nutritrack.data.remote.FoodInfo
import com.example.nutritrack.ui.theme.NutriTrackTheme
import com.example.nutritrack.util.FoodResource
import com.example.nutritrack.util.LogEntry
import com.example.nutritrack.util.RemoteResource
import com.example.nutritrack.viewmodels.SearchViewModel
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
        showUnitMenu = state.showUnitMenu,
        onToggleUnitMenu = searchViewModel::toggleUnitMenu,
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
    showUnitMenu: Boolean,
    onToggleUnitMenu: () -> Unit,
    onSelectItem: (Int) -> Unit,
    onSearch: (String) -> Unit,
    onRetrySearch: () -> Unit,
    onUpdateQuantity: (Float) -> Unit,
    displayStats: List<Float>,
    onUnitSelected: (Int) -> Unit,
    onAddItem: (LogEntry) -> Unit
) {
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
//                    Text(
//                        text = "Loading",
//                        fontSize = 20.sp,
//                        color = Color.LightGray
//                    )
                    CircularProgressIndicator(
                        modifier = Modifier.size(150.dp)
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
                    onMoreUnits = onToggleUnitMenu,
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
                onToggleUnitMenu()
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
                text = "No Results",
                fontSize = 20.sp,
                color = Color.Gray
            )
        }
    } else {
        // TODO(Reset scroll state)
        LazyColumn(
            horizontalAlignment = Alignment.CenterHorizontally
        ) {

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

            item {
                Divider(
                    color = Color.LightGray,
                    thickness = 3.dp,
                    modifier = Modifier
                        .padding(vertical = 48.dp)
                )
                Text(
                    text = "End of results",
                    fontSize = 20.sp,
                    fontStyle = FontStyle.Italic
                )
            }
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
                Box(modifier = Modifier.weight(0.4f)) {
                    FoodImage(imgRes = foodInfo.imgRes)
                }

                Text(
                    text = foodInfo.title,
                    fontSize = 15.sp,
                    fontWeight = FontWeight.Bold,
                    textAlign = TextAlign.Center,
                    overflow = TextOverflow.Ellipsis,
                    modifier = Modifier
                        .padding(5.dp)
                        .weight(0.6f)
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

    Surface(
        color = Color.White,
        modifier = Modifier.size(imageSize + 20.dp)
    ) {
        if (imgRes == null) {
            Column(
                verticalArrangement = Arrangement.SpaceBetween,
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
            val painter = rememberImagePainter(
                data = NutracheckService.IMAGE_URI + imgRes,
                builder = {
                    crossfade(true)
                    placeholder(R.drawable.image_empty)
//                    transformations(CircleCropTransformation())
                }
            )

            Surface(
    //            shape = RoundedCornerShape(5.dp),
                color = Color.White,
                modifier = Modifier.size(imageSize)
            ) {
                when (painter.state) {
                    is ImagePainter.State.Loading -> {
                        CircularProgressIndicator()
                    }
                    is ImagePainter.State.Error -> {
                        Timber.e("Can't Load image: $imgRes")

                        Column (
                            horizontalAlignment = Alignment.CenterHorizontally
                        ) {
                            Image(
                                painter = painterResource(id = R.drawable.image_network_error),
                                contentDescription = "Image Network Error",
                                alpha = 0.5f,
                                modifier = Modifier.size(imageSize)
                            )
                            Text(
                                text = "Network Error",
                                fontSize = 10.sp,
                                color = Color.LightGray
                            )
                        }
                    }
                    else -> Image(
                        painter = painter,
                        contentDescription = "Food Image",
                        alpha = if (painter.state is ImagePainter.State.Empty) 0.3f else 1.0f
                    )
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
                modifier = Modifier
                    .padding(horizontal = 4.dp)
            ) {
                Text(
                    units[selectedUnit],
                    fontSize = 20.sp,
                    fontWeight = FontWeight.Bold,
                    textAlign = TextAlign.End,
                    overflow = TextOverflow.Ellipsis,
                    maxLines = 1,
                    fontStyle =
                        if (units.size > 1) {
                            FontStyle.Italic
                        } else {
                            FontStyle.Normal
                        },
                    modifier = Modifier.fillMaxWidth(0.5f)
                )

                Icon(
                    imageVector = Icons.Filled.MoreVert,
                    contentDescription = "More Units",
                    modifier = Modifier.clickable { onClickMore() }
                )
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
                                textAlign = TextAlign.Center,
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
        mutableStateOf(RemoteResource.Success(fakeSearchData))
    }
    var displayStats by remember { mutableStateOf(emptyList<Float>())}
    var showUnitMenu by remember { mutableStateOf(false) }

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
                showUnitMenu = showUnitMenu,
                onToggleUnitMenu = { showUnitMenu = !showUnitMenu},
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