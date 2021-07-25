package com.example.nutritrack

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AccountCircle
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.List
import androidx.compose.material.icons.filled.Search
import androidx.compose.ui.graphics.vector.ImageVector

enum class Screen(val icon: ImageVector) {
    Diary(Icons.Filled.List),
    Search(Icons.Filled.Search),
    Saved(Icons.Filled.Favorite),
    Profile(Icons.Filled.AccountCircle)
}