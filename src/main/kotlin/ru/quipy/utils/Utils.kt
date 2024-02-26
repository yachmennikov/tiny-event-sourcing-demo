package ru.quipy.utils

fun formatNumber(value: Int): String {
    return value.toString().replace("_", ".")
}