package com.example.nutritrack.data.model

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(
    tableName = "food",
    primaryKeys = ["title", "img_res"]
)
data class FoodEntity(
    @ColumnInfo(name = "title") val title: String,

    @ColumnInfo(name = "img_res") val imgRes: String,

    @ColumnInfo(name = "portions_list") val portions: List<String> = emptyList(),

    @ColumnInfo(name = "kcal_list") val kcal: List<Float> = emptyList(),

    @ColumnInfo(name = "protein_list") val protein: List<Float> = emptyList(),

    @ColumnInfo(name = "carbs_list") val carbs: List<Float> = emptyList(),

    @ColumnInfo(name = "fat_list") val fat: List<Float> = emptyList()
) {
    fun generatePortions(): FoodEntity {
        val newPortions = portions.toMutableList()
        val newKcal     = kcal.toMutableList()
        val newProtein  = protein.toMutableList()
        val newCarbs    = carbs.toMutableList()
        val newFat      = fat.toMutableList()

        portions.forEachIndexed { index, portion ->
            val loc = normRgx.find(portion.lowercase())

            if (loc != null) {
                val num = loc.value.takeWhile { it.isDigit() }

                // New name for portion
                val name = loc.value.replace(num, "")

                // Factor to scale the macronutrients
                val scale = num.toFloat()

                if (!newPortions.contains(name)) {
                    newPortions.add(name)
                    newKcal.add(kcal[index] / scale)
                    newProtein.add(protein[index] / scale)
                    newCarbs.add(carbs[index] / scale)
                    newFat.add(fat[index] / scale)
                }
            }
        }

        return FoodEntity(
            title = title,
            imgRes = imgRes,
            portions = newPortions,
            kcal = newKcal,
            protein = newProtein,
            carbs = newCarbs,
            fat = newFat
        )
    }

    companion object {
        private val normRgx = """\d+(g|ml)""".toRegex()
//        private val normRegex = """(?!=\()\d+(g|ml)(?!\))""".toRegex()
        private val valueRgx = """\d+""".toRegex()
    }
}
