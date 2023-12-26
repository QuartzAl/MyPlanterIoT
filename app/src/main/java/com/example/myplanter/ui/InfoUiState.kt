package com.example.myplanter.ui

data class InfoUiState(
    val lux: String = "0",
    val moisture: String = "0",
    val lastWatered: String = "0",
    val lightLevel: Int = 0,
    val lightOverride: Boolean = false
)