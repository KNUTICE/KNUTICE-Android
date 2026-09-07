package com.doyoonkim.common.util

import android.util.Log

inline fun <reified T : Enum<T>> valueOrNull(name: String): T? {
    return try {
        enumValueOf<T>(name)
    } catch (e: IllegalArgumentException) {
        Log.e("${T::class.simpleName}", "name $name is not a name of valid enum entry")
        null
    }
}
