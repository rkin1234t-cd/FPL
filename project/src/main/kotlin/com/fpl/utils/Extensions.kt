package com.fpl.utils

import java.util.*

// Расширение для Map, чтобы работать с множественными значениями
fun <K, V> mutableMapOf(): MutableMap<K, V> = HashMap()

// Дополнительные утилиты для работы с UUID
fun String.toUUIDOrNull(): UUID? {
    return try {
        UUID.fromString(this)
    } catch (e: IllegalArgumentException) {
        null
    }
}

// Утилиты для работы с цветами в Minecraft
fun String.colorize(): String {
    return this.replace('&', '§')
}

fun String.stripColor(): String {
    return this.replace(Regex("§[0-9a-fk-or]"), "")
}