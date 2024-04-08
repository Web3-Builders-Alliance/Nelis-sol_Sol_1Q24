package spl.cards.app.util

import com.squareup.moshi.FromJson
import com.squareup.moshi.ToJson
import java.math.BigInteger

class BigIntegerJsonAdapter {
    @ToJson
    fun toJson(value: BigInteger): String {
        return value.toString()
    }

    @FromJson
    fun fromJson(value: String): BigInteger {
        return BigInteger(value)
    }
}
