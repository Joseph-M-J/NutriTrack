package com.example.nutritrack.ui.screens

import androidx.compose.animation.ExperimentalAnimationApi
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Search
import androidx.compose.runtime.*
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.annotation.ExperimentalCoilApi
import com.example.nutritrack.data.state.SearchViewState
import com.example.nutritrack.util.FoodPreset
import com.example.nutritrack.util.RemoteResource
import com.example.nutritrack.viewmodels.SearchViewModel

@ExperimentalFoundationApi
@ExperimentalCoilApi
@ExperimentalAnimationApi
@Composable
fun SearchScreenContent(
    searchViewModel: SearchViewModel
) {
    val state by searchViewModel.viewState.collectAsState()

    SearchScreenHoist(
        state = state,
        onSearch = searchViewModel::fetchFoodList,
        onAddItem = { searchViewModel.copyLogEntry(it) }
    )
}

@ExperimentalFoundationApi
@ExperimentalCoilApi
@ExperimentalAnimationApi
@Composable
fun SearchScreenHoist(
    state: SearchViewState,
    onSearch: (String?, Int, Boolean) -> Unit,
    onAddItem: (FoodPreset) -> Unit
) {
    val searchResults = state.searchResults

    Column(modifier = Modifier.fillMaxSize()) {
        TopAppBar {
            Box (
                contentAlignment = Alignment.Center,
                modifier = Modifier.fillMaxSize()
            ) {
                LoadedEntries(
                    modifier = Modifier.align(Alignment.CenterStart),
                    count = state.loadedEntries
                )

                SearchBar(
                    onSearch = { onSearch(it, 0, false) },
                    modifier = Modifier
                        .padding(vertical = 8.dp)
                        .fillMaxWidth(0.65f)
                        .fillMaxHeight()
//                        .align(Alignment.Center)
                )
            }
        }
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            modifier = Modifier
                .padding(horizontal = 16.dp)
                .fillMaxSize()
        ) {
            when (searchResults) {
                is RemoteResource.Loading -> {
                    Box(
                        contentAlignment = Alignment.Center,
                        modifier = Modifier.fillMaxSize()
                    ) {
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
                                text = "${searchResults.message}... ☹",
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
                        foodList = searchResults.data,
                        hasNextPage = state.hasNextPage,
                        currentPage = state.currentPage,
                        onChangePage = {
                            onSearch(null, it, true)
                        },
                        onAddItem = onAddItem,
                        onDeleteItem = null
                    )
                }
            }
        }
    }
}

@Composable
fun SearchBar(
    modifier: Modifier = Modifier,
    onSearch: (String) -> Unit
) {
    var text by rememberSaveable{ mutableStateOf("") }
    val focusManager = LocalFocusManager.current

//    val colors = TextFieldDefaults.textFieldColors(
//        backgroundColor = Color.White,
//        textColor = Color.Black
//    )

    val style = TextStyle.Default.copy(
        fontSize = 20.sp,
        fontWeight = FontWeight.Bold
    )

    BasicTextField(
        value = text,
        textStyle = style,
        onValueChange = { text = it },
        singleLine = true,
        keyboardOptions = KeyboardOptions.Default.copy(imeAction = ImeAction.Done),
        keyboardActions = KeyboardActions(onDone = {
            focusManager.clearFocus()
            onSearch(text)
        }),
        decorationBox = { innerText ->
            Surface(
                shape = RoundedCornerShape(10.dp),
                modifier = modifier
            ) {
                Box(
                    contentAlignment = Alignment.Center,
                    modifier = Modifier.fillMaxSize()
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        modifier = Modifier.fillMaxSize()
                    ) {
                        Box(
                            modifier = Modifier
                                .padding(horizontal = 16.dp)
                                .weight(0.8f)
                        ) {
                            innerText()
                        }
                        Icon(
                            imageVector = Icons.Filled.Search,
                            contentDescription = "Search Icon",
                            modifier = Modifier.weight(0.2f)
                        )
                    }
                }
            }
        }
    )
}