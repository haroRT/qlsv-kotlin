package com.kma.qlsv.utils

fun String.formatDate(): String {
    val digits = this.filter { it.isDigit() }.take(8) // Chỉ lấy 8 chữ số (DDMMYYYY)
    return when (digits.length) {
        in 1..2 -> digits // Chỉ hiển thị DD
        in 3..4 -> "${digits.substring(0, 2)}/${digits.substring(2)}" // DD/MM
        in 5..8 -> "${digits.substring(0, 2)}/${digits.substring(2, 4)}/${digits.substring(4)}" // DD/MM/YYYY
        else -> ""
    }
}