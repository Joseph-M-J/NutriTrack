package com.example.nutritrack.data

import com.example.nutritrack.api.NutracheckService
import com.example.nutritrack.data.remote.FoodInfo
import com.example.nutritrack.util.FoodResource
import com.example.nutritrack.util.RemoteResource
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import org.jsoup.Jsoup
import timber.log.Timber
import java.net.URLDecoder

class FoodRepository constructor(private val service: NutracheckService) {

    fun fetchFoodList(query: String, page: Int): Flow<Pair<FoodResource, Boolean>> = flow {
        emit(Pair(RemoteResource.Loading(), true))
        delay(1000)
        emit(fetchNutracheckPage(query, page))
    }

    private suspend fun fetchNutracheckPage(query: String, page: Int): Pair<FoodResource, Boolean> {
        return try {
            Timber.i("Searching products, query = $query, page = $page")

            val response = service.searchProducts(query, page)
            val doc = Jsoup.parse(response)

            val elements = doc.getElementsByClass("calsinResultsArrow")

            val data = elements.mapNotNull {
                val rawLink = it.child(0).attr("href")
                val splitLink = rawLink.split("/")
                val id = splitLink[splitLink.lastIndex - 1]
                val rawTitle = splitLink[splitLink.lastIndex]
                val title = URLDecoder.decode(rawTitle, "utf-8")
                extractInfo(id, title)
            }

            val hasNextPage = doc.getElementsContainingOwnText("Next").isNotEmpty()

            Pair(RemoteResource.Success(data), hasNextPage)

        } catch (e: Exception) {
            Timber.e(e)
            Pair(RemoteResource.Error("Oh No! ${e.message}"), false)
        }
    }

    private suspend fun extractInfo(productId: String, productTitle: String): FoodInfo? {
        Timber.i("Extracting info, id = $productId, title = $productTitle")
        return try {
            val response = service.getProductInfo(
                productId = productId,
                productTitle = productTitle
            )

            val doc = Jsoup.parse(response)

            // Get breakdown table or throw
            val table = doc.getElementById("prodbreakdown") ?: throw NoSuchElementException()

            // Get portion options
            val portionOptions = table.getElementsByTag("option")

            // Throw if empty
            if (portionOptions.isEmpty()) throw NoSuchElementException()

            // Extract portions
            val portions = portionOptions.map { it.ownText() }

            val kcalList    = mutableListOf<Float>()
            val proteinList = mutableListOf<Float>()
            val carbsList   = mutableListOf<Float>()
            val fatList     = mutableListOf<Float>()

            portions.forEachIndexed { i, _ ->
                val subTable = table.getElementById("prodDetails${i+1}")
                val kcals = subTable?.select("h2 > span")?.get(0)?.ownText()?.toFloat()
                kcalList.add(kcals ?: -1f)

                val macros = subTable?.getElementsByTag("td")

                val protein = macros?.get(1)?.ownText()?.dropLast(1)?.toFloat()
                proteinList.add(protein ?: -1f)

                val carbs = macros?.get(3)?.ownText()?.dropLast(1)?.toFloat()
                carbsList.add(carbs ?: -1f)

                val fat = macros?.get(5)?.ownText()?.dropLast(1)?.toFloat()
                fatList.add(fat ?: -1f)
            }

            val img = doc.getElementsByClass("img-responsiven topMargin")[0]

//            val title = img.attr("alt")
            val srcSplit = img.attr("src").split("/")

            val imgRes = srcSplit
                .slice(srcSplit.lastIndex-1 until srcSplit.size)
                .joinToString("/")

            FoodInfo(
                title = productTitle,
                imgRes = imgRes,
                portions = portions,
                kcal = kcalList,
                protein = proteinList,
                carbs = carbsList,
                fat = fatList
            )
        } catch (e: NoSuchElementException) {
            Timber.w(e)
            null
        } catch (e: Exception) {
            Timber.e(e)
            null
        }
    }
}