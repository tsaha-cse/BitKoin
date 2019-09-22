package com.tushar.module.data.api

import com.google.gson.JsonArray
import com.google.gson.JsonDeserializationContext
import com.google.gson.JsonDeserializer
import com.google.gson.JsonElement
import com.google.gson.JsonParseException
import com.tushar.module.data.model.BitCoinGraphModel
import com.tushar.module.data.model.DatePrice
import com.tushar.module.data.util.toDateTimeText
import java.lang.reflect.Type

/**
 * JSON deserializer for the REST API response
 * also it can deserialize the json from local source
 */
class BitCoinGraphInfoDeserializer : JsonDeserializer<BitCoinGraphModel> {
    override fun deserialize(
        json: JsonElement?,
        typeOfT: Type?,
        context: JsonDeserializationContext?
    ): BitCoinGraphModel =
        json?.asJsonObject?.let { jsonObject ->
            val datePriceList: MutableList<DatePrice> = mutableListOf()
            val datePriceJsonArray: JsonArray? = jsonObject.get("values")?.asJsonArray
            datePriceJsonArray?.let { array ->
                array.forEach { item ->
                    val dateInUnix: Int =
                        item.asJsonObject.get("x")?.asInt ?: item.asJsonObject.get("date").asInt
                    datePriceList.add(
                        DatePrice(
                            date = dateInUnix,
                            dateText = item.asJsonObject.get("dateText")?.asString
                                ?: dateInUnix.toDateTimeText(),
                            price = item.asJsonObject.get("y")?.asDouble
                                ?: item.asJsonObject.get("price").asDouble
                        )
                    )
                }
            }
            return BitCoinGraphModel(
                name = jsonObject.get("name").asString ?: "",
                unit = jsonObject.get("unit").asString ?: "",
                period = jsonObject.get("period").asString ?: "",
                values = datePriceList
            )
        } ?: throw JsonParseException("Invalid JSON")
}

