package com.tushar.module.data.api

import com.google.gson.JsonArray
import com.google.gson.JsonDeserializationContext
import com.google.gson.JsonDeserializer
import com.google.gson.JsonElement
import com.google.gson.JsonParseException
import com.tushar.module.data.model.BitCoinGraphInfo
import com.tushar.module.data.model.DatePrice
import com.tushar.module.data.util.toDateTimeText
import java.lang.reflect.Type

class BitCoinGraphInfoDeserializer : JsonDeserializer<BitCoinGraphInfo> {
    override fun deserialize(
        json: JsonElement?,
        typeOfT: Type?,
        context: JsonDeserializationContext?
    ): BitCoinGraphInfo =
        json?.asJsonObject?.let { jsonObject ->
            val datePriceList: MutableList<DatePrice> = mutableListOf()
            val datePriceJsonArray: JsonArray? = jsonObject.get("values")?.asJsonArray
            datePriceJsonArray?.let { array ->
                array.forEach { item ->
                    val dateInUnix: Int = item.asJsonObject.get("x").asInt
                    datePriceList.add(
                        DatePrice(
                            date = dateInUnix,
                            dateText = dateInUnix.toDateTimeText(),
                            price = item.asJsonObject.get("y").asDouble
                        )
                    )
                }
            }
            return BitCoinGraphInfo(
                name = jsonObject.get("name").asString ?: "",
                unit = jsonObject.get("unit").asString ?: "",
                period = jsonObject.get("period").asString ?: "",
                values = datePriceList,
                timeStamp = System.currentTimeMillis()
            )
        } ?: throw JsonParseException("Invalid JSON")
}

