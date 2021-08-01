package com.example.nutritrack.ui.screens

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.ExperimentalAnimationApi
import androidx.compose.animation.animateContentSize
import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.tween
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Image
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.Orientation
import androidx.compose.foundation.gestures.draggable
import androidx.compose.foundation.gestures.rememberDraggableState
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.annotation.ExperimentalCoilApi
import coil.compose.ImagePainter
import coil.compose.rememberImagePainter
import com.example.nutritrack.R
import com.example.nutritrack.api.NutracheckService
import com.example.nutritrack.data.model.FoodEntity
import com.example.nutritrack.data.state.fakeSearchData
import com.example.nutritrack.util.LogEntry
import timber.log.Timber
import kotlin.math.abs
import kotlin.math.sign

@Composable
fun LoadedEntries(
    modifier: Modifier = Modifier,
    count: Int = 0
) {
    val color = if (count > 0) {
        Color(0.718f, 0.933f, 0.725f, 1.0f)
    } else {
        Color.Gray.copy(alpha = 0.5f)
    }
    Row(
        verticalAlignment = Alignment.CenterVertically,
        modifier = modifier
    ) {
        Text(
            text = count.toString(),
            fontSize = 20.sp,
            fontWeight = FontWeight.Bold,
            color = color
        )
        Spacer(modifier = Modifier.width(4.dp))
        Icon(
            imageVector = Icons.Filled.ShoppingCart,
            contentDescription = "Loaded Entries",
            tint = color
        )
    }
}

@ExperimentalCoilApi
@ExperimentalAnimationApi
@Composable
fun FoodList(
    foodList: List<FoodEntity>,
    hasNextPage: Boolean,
    currentPage: Int,
    onChangePage: (Int) -> Unit,
    onAddItem: (LogEntry) -> Unit
) {
    var expandedCard by rememberSaveable { mutableStateOf(-1) }

    if (foodList.isEmpty()) {
        Box (
            contentAlignment = Alignment.Center,
            modifier = Modifier.fillMaxSize()
        ) {
            Text(
                text = "No Items...\uD83E\uDD37",
                fontSize = 20.sp,
                color = Color.Gray
            )
        }
    } else {
        LazyColumn(
            horizontalAlignment = Alignment.CenterHorizontally
        ) {

            itemsIndexed(foodList) { index, entity ->
                Spacer(modifier = Modifier.height(8.dp))

                FoodCard(
                    foodEntity = entity,
                    expanded = index == expandedCard,
                    onSelected = { expandedCard = index },
                    onAddItem = { onAddItem(it) }
                )
            }

            item {
                Spacer(modifier = Modifier.height(100.dp))

                Text(
                    text = "End Of Page",
                    fontSize = 15.sp,
                    fontStyle = FontStyle.Italic,
                    modifier = Modifier.padding(8.dp)
                )

                Divider(
                    color = Color.LightGray,
                    thickness = 2.dp,
                )

                Spacer(modifier = Modifier.height(16.dp))

                Row(
                    horizontalArrangement = Arrangement.SpaceAround,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Button(
                        onClick = { onChangePage(currentPage + 1) },
                        enabled = currentPage != 1
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
                            text = currentPage.toString(),
                            fontSize = 20.sp
                        )
                    }

                    Button(
                        onClick = { onChangePage(currentPage - 1) },
                        enabled = hasNextPage
                    ) {
                        Text("Next")
                    }
                }

                Spacer(modifier = Modifier.height(16.dp))
            }
        }
    }
}

@ExperimentalAnimationApi
@ExperimentalCoilApi
@Composable
fun FoodCard(
    foodEntity: FoodEntity,
    expanded: Boolean,
    onSelected: () -> Unit,
    onAddItem: (LogEntry) -> Unit
) {
    var showExtra by rememberSaveable { mutableStateOf(false) }

    var selectedUnit by rememberSaveable { mutableStateOf(0) }
    var quantity by rememberSaveable { mutableStateOf(1.0f) }

    val displayKcal = foodEntity.kcal[selectedUnit] * quantity

    Card(
        shape = RoundedCornerShape(bottomStart = 30.dp, topEnd = 45.dp),
        border = BorderStroke(
            width = 3.dp,
            brush = Brush.verticalGradient(
                colors = listOf(Color.Transparent, Color.LightGray),
                startY = 0.0f,
                endY = 40.0f
            )),
        elevation = 8.dp,
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onSelected() }
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
                Box(
                    modifier = Modifier.weight(0.4f)
                ) {
                    FoodImage(imgRes = foodEntity.imgRes)
                }

                Text(
                    text = foodEntity.title,
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
                    PortionScaleRow(
                        units = foodEntity.portions,
                        quantity = quantity,
                        onUpdateQuantity = { quantity = it },
                        selectedUnit = selectedUnit,
                        onUpdateSelectedUnit = { selectedUnit = it },
                        onDone = {
                            onAddItem(LogEntry(foodEntity.title, displayKcal))
                        }
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
                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                            ){
                                Icon(
                                    imageVector = Icons.Filled.LocalFireDepartment,
                                    contentDescription = "Kcal",
                                    tint = Color(255, 152, 0, 255),
                                    modifier = Modifier.size(30.dp)
                                )
                                Text(
                                    text = "Kcal",
                                    fontSize = 25.sp
                                )
                            }
                            Text(
                                text = "%.1f".format(displayKcal),
                                fontSize = 30.sp,
                                overflow = TextOverflow.Ellipsis,
                                fontWeight = FontWeight.Bold,
                                modifier = Modifier.padding(horizontal = 8.dp)
                            )
                        }

                        Icon(
                            imageVector = if (showExtra) {
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
                    val displayProtein = foodEntity.protein[selectedUnit] * quantity
                    val displayCarbs = foodEntity.carbs[selectedUnit] * quantity
                    val displayFat = foodEntity.fat[selectedUnit] * quantity

                    Spacer(modifier = Modifier.height(16.dp))

                    Row (
                        verticalAlignment = Alignment.CenterVertically,
                        modifier = Modifier.fillMaxWidth()
                    ){
                        val modifier = Modifier.weight(1 / 3.0f)

                        Macronutrient(modifier, displayProtein, "\uD83E\uDD69Protein")

                        Macronutrient(modifier, displayCarbs, "\uD83E\uDD50Carbs")

                        Macronutrient(modifier, displayFat, "\uD83E\uDDC8Fat")
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

@ExperimentalCoilApi
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

@ExperimentalAnimationApi
@Composable
fun PortionScaleRow(
    units: List<String>,
    quantity: Float,
    onUpdateQuantity: (Float) -> Unit,
    selectedUnit: Int,
    onUpdateSelectedUnit: (Int) -> Unit,
    onDone: () -> Unit
) {
    var maximiseUnit by rememberSaveable { mutableStateOf(false) }

    var dragBuffer by rememberSaveable { mutableStateOf(0.0f) }
    val dragDeadzone = 50.0f

    val portion = units[selectedUnit]
    val manyUnits = units.size > 1

    Row(
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceAround,
        modifier = Modifier
            .height(50.dp)
            .fillMaxWidth()
    ) {
        Surface(
            shape = RoundedCornerShape(10.dp),
            border = BorderStroke(width = 2.dp, color = Color.LightGray),
            elevation = 5.dp,
            modifier = Modifier
                .weight(0.8f)
                .fillMaxHeight()
        ) {
            Box (
                contentAlignment = Alignment.Center
            ){
                QuantityUnitField(
                    quantity = quantity,
                    unit = portion,
                    maximiseUnit = maximiseUnit,
                    onUpdate = onUpdateQuantity,
                    onDone = onDone
                )
            }
        }

        Surface(
            color = Color.LightGray,
            shape = CircleShape,
            modifier = Modifier
                .padding(4.dp)
                .weight(0.2f)
                .fillMaxHeight()
                .clickable(enabled = false) {}
                .draggable(
                    startDragImmediately = true,
                    onDragStarted = {
                        maximiseUnit = true
                    },
                    state = rememberDraggableState { delta ->
                        // Avoid double up on direction change
                        if (sign(delta) != sign(dragBuffer)) {
                            dragBuffer = 0.0f
                        }

                        dragBuffer += delta

                        // After deadzone step through units
                        if (abs(dragBuffer) > dragDeadzone) {
                            var i = selectedUnit + sign(delta).toInt()

                            // Wrap around
                            if (i < 0) {
                                i = units.lastIndex
                            } else if (i >= units.size) {
                                i = 0
                            }

                            onUpdateSelectedUnit(i)

                            dragBuffer = 0.0f
                        }
                    },
                    orientation = Orientation.Vertical,
                    onDragStopped = {
                        maximiseUnit = false
                    }
                )
        ) {
            Icon(
                imageVector = if (manyUnits) {
                    Icons.Filled.UnfoldMore
                } else {
                    Icons.Filled.MoreHoriz
                },
                contentDescription = "Drag to cycle units",
                modifier = Modifier
                    .size(45.dp)
            )
        }

    }
}

@ExperimentalAnimationApi
@Composable
fun QuantityUnitField(
    quantity: Float,
    unit: String,
    maximiseUnit: Boolean,
    onUpdate: (Float) -> Unit,
    onDone: () -> Unit
) {
    val rawText = quantity.toString()
    var text by rememberSaveable { mutableStateOf(if (rawText == "1.0") "1" else rawText) }

    var valid by rememberSaveable { mutableStateOf(true) }
    var errorMessage by rememberSaveable { mutableStateOf("") }

    val focusManager = LocalFocusManager.current

    var style = TextStyle.Default.copy(
        fontSize = 20.sp,
        fontWeight = FontWeight.Bold
    )

    if (!valid) {
        style = style.copy(
            color = Color.Red,
            fontStyle = FontStyle.Italic
        )
    }

    BasicTextField(
        value = text,
        textStyle = style,
        onValueChange = {
            text = it

            valid = try {
                val _quantity = it.toFloat()

                if (_quantity > 10_000) {
                    throw IllegalArgumentException("Too Large")
                }
                if (_quantity <= 0) {
                    throw IllegalArgumentException("Too Small")
                }

                onUpdate(_quantity)
                errorMessage = ""
                true

            } catch (e: NumberFormatException) {
                errorMessage = "Invalid"
                false

            } catch (e: IllegalArgumentException) {
                errorMessage = e.message ?: ""
                false
            }
        },
        singleLine = true,
        keyboardOptions = KeyboardOptions.Default.copy(
            keyboardType = KeyboardType.Number,
            imeAction = ImeAction.Done
        ),
        keyboardActions = KeyboardActions(onDone = {
            if (valid) {
                focusManager.clearFocus()
                onDone()
            }
        }),
        decorationBox = { innerText ->
            Row (
                verticalAlignment = Alignment.CenterVertically
            ){

                AnimatedVisibility(visible = !maximiseUnit) {
                    Box(
                        contentAlignment = Alignment.CenterStart,
                        modifier = Modifier
                            .weight(0.5f)
                            .fillMaxHeight()
                    ) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Icon(
                                imageVector = if (valid) {
                                    Icons.Filled.Edit
                                } else {
                                    Icons.Filled.Warning
                                },
                                contentDescription = "Edit status",
                                tint = if (valid) {
                                    Color.Gray
                                } else {
                                    Color.Red
                                },
                                modifier = Modifier
                                    .size(35.dp)
                                    .padding(horizontal = 4.dp)
                            )

                            innerText()

                            Divider(
                                modifier = Modifier
                                    .width(2.dp)
                                    .fillMaxHeight()
                            )
                        }
                    }
                }

                Text(
                    text = if (errorMessage.isBlank()) unit else errorMessage,
                    fontSize = 20.sp,
                    textAlign = TextAlign.Center,
                    overflow = TextOverflow.Ellipsis,
                    maxLines = 1,
                    modifier = Modifier
                        .padding(horizontal = 8.dp)
                        .weight(0.5f)
                        .fillMaxWidth()
                )
            }
        }
    )
}

//@Preview(showBackground = true)
//@Composable
//fun PreviewLoadedEntries() {
//    LoadedEntries(count=1)
//}

//@ExperimentalAnimationApi
//@Preview(showBackground = true)
//@Composable
//fun PreviewQuantityTextField() {
//    var maximiseUnit by remember { mutableStateOf(false) }
//    Column {
//        QuantityUnitField(
//            unit = "A Very Long Unit Name",
//            maximiseUnit = maximiseUnit,
//            onDone = {}
//        )
//        Spacer(modifier = Modifier.height(16.dp))
//        Button(onClick = { maximiseUnit = !maximiseUnit}) {
//            Text("Flip")
//        }
//    }
//}

//@ExperimentalAnimationApi
//@Preview(showBackground = true)
//@Composable
//fun PreviewPortionScaleRowMany() {
//    PortionScaleRow(
//        units = listOf("A Very Very Long Unit Name", "g", "ml", "serving", "1/2 cup", "brownie (25g)"),
//        onDone = { p, q -> Timber.i("Portion=$p, Quantity=$q")}
//    )
//}

//@ExperimentalAnimationApi
//@Preview(showBackground = true)
//@Composable
//fun PreviewPortionScaleRowSingle() {
//    PortionScaleRow(
//        units = listOf("Just the one"),
//        onDone = { p, q -> Timber.i("Portion=$p, Quantity=$q")}
//    )
//}

//@ExperimentalCoilApi
//@ExperimentalAnimationApi
//@Preview
//@Composable
//fun PreviewFoodCard() {
//    FoodCard(
//        foodEntity = FoodEntity(
//            title = "Asda Semi Skimmed British Milk 2272ml",
//            imgRes = "93/565893.png",
//            portions = listOf("40ml for Tea/Coffee", "100ml", "125ml for Cereal"),
//            kcal = listOf(20.0f, 50.0f, 63.0f),
//            protein = listOf(1.4f, 3.6f, 4.5f),
//            carbs = listOf(1.9f, 4.8f, 6.0f),
//            fat = listOf(0.7f, 1.8f, 2.2f)
//        ),
//        onAddItem = {}
//    )
//}

@ExperimentalCoilApi
@ExperimentalAnimationApi
@Preview(showBackground = true)
@Composable
fun PreviewFoodList() {
    FoodList(
        foodList = fakeSearchData,
        hasNextPage = true,
        currentPage = 0,
        onChangePage = { Timber.i("Changing to page $it")},
        onAddItem = { Timber.i("Adding item $it") }
    )
}