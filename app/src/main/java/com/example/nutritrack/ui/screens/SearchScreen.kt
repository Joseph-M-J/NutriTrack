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
import androidx.compose.material.icons.filled.ExpandLess
import androidx.compose.material.icons.filled.ExpandMore
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
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.ImagePainter
import coil.compose.rememberImagePainter
import com.example.nutritrack.R
import com.example.nutritrack.api.NutracheckService
import com.example.nutritrack.data.local.SearchViewState
import com.example.nutritrack.data.local.fakeSearchData
import com.example.nutritrack.data.remote.FoodInfo
import com.example.nutritrack.ui.theme.NutriTrackTheme
import com.example.nutritrack.util.FoodResource
import com.example.nutritrack.util.LogEntry
import com.example.nutritrack.util.MealCategory
import com.example.nutritrack.util.RemoteResource
import com.example.nutritrack.viewmodels.SearchViewModel
import timber.log.Timber

@Composable
fun SearchScreenContent(
    searchViewModel: SearchViewModel,
    onAddItem: () -> Unit
) {
    val state by searchViewModel.viewState.collectAsState()

    SearchScreenHoist(
        state = state,
        onToggleUnitMenu = searchViewModel::toggleUnitMenu,
        onSelectItem = searchViewModel::selectItem,
        onSearch = searchViewModel::fetchFoodList,
        onUpdateQuantity = searchViewModel::updateDisplayStats,
        onUnitSelected = searchViewModel::selectUnit,
        onAddItem = {
            searchViewModel.copyLogEntry(it)
            onAddItem()
        }
    )

}

@Composable
fun SearchScreenHoist(
    state: SearchViewState,
    onToggleUnitMenu: () -> Unit,
    onSelectItem: (Int) -> Unit,
    onSearch: (String?, Int, Boolean) -> Unit,
    onUpdateQuantity: (Float) -> Unit,
    onUnitSelected: (Int) -> Unit,
    onAddItem: (LogEntry) -> Unit
) {
    val searchResults = state.searchResults

    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = Modifier
            .padding(16.dp)
            .fillMaxSize()
    ) {
        SearchBar(onSearch = {
            onSearch(it, NutracheckService.FIRST_PAGE, false) }
        )

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
                            onClick = { onSearch(null, state.currentPage, true) }
                        ) {
                            Text("Try Again?")
                        }
                    }
                }
            }
            is RemoteResource.Success -> {
                FoodList(
                    state = state,
                    onSearch = { onSearch(null, it, true) },
                    onSelectItem = onSelectItem,
                    onUpdateQuantity = onUpdateQuantity,
                    onMoreUnits = onToggleUnitMenu,
                    onAddItem = onAddItem
                )
            }
        }
    }


    if (
        searchResults is RemoteResource.Success &&
        state.showUnitMenu
    ) {
        FullscreenUnitMenu(
            units = searchResults.data[state.selectedItem].portions,
            selectedUnit = state.selectedUnit,
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
    // TODO(Why doesn't this not save?)
    var text by rememberSaveable{ mutableStateOf("") }
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
    state: SearchViewState,
    onSearch: (Int) -> Unit,
    onSelectItem: (Int) -> Unit,
    onUpdateQuantity: (Float) -> Unit,
    onMoreUnits: () -> Unit,
    onAddItem: (LogEntry) -> Unit
) {
    // We don't call FoodList unless we have a successful search
    @Suppress("UNCHECKED_CAST")
    val foodList = (
        state.searchResults
        as RemoteResource.Success<List<FoodInfo>>
    ).data

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
                    selectedUnit = state.selectedUnit,
                    expanded = index == state.selectedItem,
                    onClick = { onSelectItem(index) },
                    onUpdateQuantity = onUpdateQuantity,
                    displayStats = state.displayStats,
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
                Row(
                    horizontalArrangement = Arrangement.SpaceAround,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Button(
                        onClick = { onSearch(state.currentPage-1) },
                        enabled = state.currentPage != NutracheckService.FIRST_PAGE
                    ) {
                        Text("Back")
                    }
                    Column(
                        horizontalAlignment = Alignment.CenterHorizontally,
                        modifier = Modifier.padding(horizontal = 16.dp)
                    ) {
                        Text(
                            text = "page",
                            fontSize = 15.sp,
                            color = Color.LightGray
                        )
                        Text(
                            text = (state.currentPage + 1).toString(),
                            fontSize = 20.sp
                        )
                    }
                    Button(
                        onClick = { onSearch(state.currentPage+1) },
                        enabled = state.hasNextPage
                    ) {
                        Text("Next")
                    }
                }
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
    var showExtra by rememberSaveable { mutableStateOf(false) }
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
                Spacer(modifier = Modifier.height(16.dp))

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

                    Spacer(modifier = Modifier.height(16.dp))

                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Row (
                            verticalAlignment = Alignment.CenterVertically,
                            modifier = Modifier.weight(0.75f)
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

                        Icon(
                            imageVector =
                                if (showExtra) {
                                    Icons.Filled.ExpandLess
                                } else {
                                    Icons.Filled.ExpandMore
                                },
                            contentDescription = "Toggle Extra",
                            modifier = Modifier
                                .weight(0.15f)
                                .size(30.dp)
                                .border(
                                    width = 1.dp,
                                    color = Color.LightGray,
                                    shape = RoundedCornerShape(10.dp)
                                )
                                .clickable { showExtra = !showExtra }
                        )
                    }
                }

                if (showExtra) {
                    val protein = displayStats.getOrElse(1) { 0.0f }
                    val carbs = displayStats.getOrElse(2) { 0.0f }
                    val fat = displayStats.getOrElse(3) { 0.0f }

                    Spacer(modifier = Modifier.height(16.dp))

                    Row (
                        verticalAlignment = Alignment.CenterVertically,
                        modifier = Modifier.fillMaxWidth()
                    ){
                        val modifier = Modifier.weight(1 / 3.0f)

                        Macronutrient(modifier, protein, "\uD83E\uDD69Protein")

                        Macronutrient(modifier, carbs, "\uD83E\uDD50Carbs")

                        Macronutrient(modifier, fat, "\uD83E\uDDC8Fat")
                    }
                }
            }
        }
    }
}

@Composable
fun Macronutrient(
    modifier: Modifier,
    value: Float,
    label: String
) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = modifier
    ) {
        Text(
            text = "%.2f".format(value) + "g",
            fontSize = 20.sp
        )
        Text(
            text = label,
            color = Color.Gray,
            fontSize = 20.sp
        )
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
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                modifier = Modifier
                    .align(Alignment.Center)
            ) {
                Text(
                    text = "Select Unit",
                    color = Color.White,
                    fontSize = 35.sp,
                    fontWeight = FontWeight.Bold,
                    modifier = Modifier
                        .padding(16.dp)
                )
                LazyColumn {
                    itemsIndexed(units) { index, unit ->
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

                        if (index != units.lastIndex) {
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
    var state by remember { mutableStateOf(SearchViewState(
        selectedItem = 0,
        searchResults = RemoteResource.Success(fakeSearchData)
    )) }

    val updateStats: (Float) -> Unit = { quantity ->
        if (
            state.searchResults is RemoteResource.Success &&
            state.selectedItem >= 0
        ) {

            val foodInfo = (
                state.searchResults
                as RemoteResource.Success<List<FoodInfo>>
            ).data.getOrNull(state.selectedItem)

            state = state.copy(
                quantity = quantity,
                displayStats =
                    if (foodInfo == null) {
                        emptyList()
                    } else {
                        listOf(
                            foodInfo.kcal[state.selectedUnit],
                            foodInfo.protein[state.selectedUnit],
                            foodInfo.carbs[state.selectedUnit],
                            foodInfo.fat[state.selectedUnit],
                        ).map { x -> x * quantity}
                    }
            )
        }
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
                SearchScreenHoist(
                    state = state,
                    onToggleUnitMenu = {
                        state = state.copy(
                            showUnitMenu = !state.showUnitMenu
                        )
                    },
                    onSelectItem = {
                        Timber.i("Here")
                        if (state.selectedItem != it) {
                            state = state.copy(
                                selectedItem = it,
                                selectedUnit = 0
                            )
                            updateStats(1.0f)
                        }
                    },
                    onSearch = { _, _, _ ->
                        state = state.copy(
                            searchResults = RemoteResource.Success(fakeSearchData)
                        )
                    },
                    onUpdateQuantity = {
                        state = state.copy(
                            quantity = it
                        )
                        updateStats(state.quantity)
                    },
                    onUnitSelected = {
                        state = state.copy(
                            selectedUnit = it
                        )
                        updateStats(state.quantity)
                    },
                    onAddItem = { println("Adding item: $it") }
                )
            }
        }
    }
}