package com.example.nutritrack.data.remote

data class FoodInfo(
    val title:  String = "Missing Entry",
    val imgRes: String? = null,
    val portions: List<String> = emptyList(),
    val kcal:     List<Float>  = emptyList(),
    val protein:  List<Float>  = emptyList(),
    val carbs:    List<Float>  = emptyList(),
    val fat:      List<Float>  = emptyList()
) {
    fun generatePortions(): FoodInfo {
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

        return FoodInfo(
            title,
            imgRes,
            newPortions,
            newKcal,
            newProtein,
            newCarbs,
            newFat
        )
    }

    companion object {
        private val normRgx = """\d+(g|ml)""".toRegex()
//        private val normRegex = """(?!=\()\d+(g|ml)(?!\))""".toRegex()
        private val valueRgx = """\d+""".toRegex()
    }
}
