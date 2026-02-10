package com.example.myapplication.utils



fun normalizeMobile(input: String): String {
    val v = input.trim().removePrefix("+")
    return if (v.length == 10 && v.all { it.isDigit() }) "91$v" else v
}
