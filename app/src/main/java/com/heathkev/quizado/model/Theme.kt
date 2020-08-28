package com.heathkev.quizado.model

enum class Theme(val storageKey: String) {
    LIGHT("light"),
    DARK("dark"),
    SYSTEM("system"),
    BATTERY_SAVER("battery_saver")
}

fun themeFromStorageKey(storageKey: String): Theme? {
    return Theme.values().firstOrNull { it.storageKey == storageKey }
}
