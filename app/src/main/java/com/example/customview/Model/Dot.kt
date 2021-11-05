package com.example.customview.Model

data class Dot(
    val rowIndex: Int = 0,
    val columnIndex: Int = 0,
    val leftPoint: Int = 0,
    val rightPoint: Int = 0,
    val topPoint: Int = 0,
    val bottomPoint: Int = 0,
    val key: String? = null
)