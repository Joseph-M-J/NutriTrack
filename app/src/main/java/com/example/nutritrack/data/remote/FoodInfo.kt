package com.example.nutritrack.data.remote

import com.example.nutritrack.R
import timber.log.Timber

data class FoodInfo(
    val title:  String = "Missing Entry",
    val imgRes: String = R.drawable.image_missing.toString(),
    val portions: List<String> = emptyList(),
    val kcal:     List<Float>  = emptyList(),
    val protein:  List<Float>  = emptyList(),
    val carbs:    List<Float>  = emptyList(),
    val fat:      List<Float>  = emptyList()
) {
    fun normalized(): FoodInfo {
        val newPortions = mutableListOf<String>()
        val newKcals    = mutableListOf<Float>()
        val newProtein  = mutableListOf<Float>()
        val newCarbs    = mutableListOf<Float>()
        val newFat      = mutableListOf<Float>()

        portions.forEachIndexed { index, portion ->
            val portionLower = portion.lowercase()

            Timber.i("'$portionLower'")

            var normalizedName = portionLower
            var portionSize = 1.0f

            if (normRegex.containsMatchIn(portionLower)) {
                Timber.i("Regex matched")

                val res1 = normRegex.find(portionLower)!!
                val res2 = valueRegex.find(res1.value)!!

                // Re-name portion size
                normalizedName = res1.value.slice(res2.range.last+1 until res1.value.length)

                // Normalize the macronutrients
                portionSize = res2.value.toFloat()
            }

            if (!newPortions.contains(normalizedName)) {
                newPortions.add(normalizedName)
                newKcals.add(kcal[index] / portionSize)
                newProtein.add(protein[index] / portionSize)
                newCarbs.add(carbs[index] / portionSize)
                newFat.add(fat[index] / portionSize)
            }
        }

        return FoodInfo(
            title,
            imgRes,
            newPortions,
            newKcals,
            newProtein,
            newCarbs,
            newFat
        )
    }

    companion object {
        private val normRegex = """(?!=\()\d+(g|ml)(?!\))""".toRegex()
        private val valueRegex = """\d+""".toRegex()
    }
}
