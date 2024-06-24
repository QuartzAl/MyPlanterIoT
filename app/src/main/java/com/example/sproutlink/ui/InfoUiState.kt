package com.example.sproutlink.ui

data class InfoUiState(
    val lux: String = "0",
    val lastWatered: String = "0",
    val lightLevel: Int = 0,
    val lightOverride: Boolean = false,
    val temperature: String = "0",
    val pressure: String = "0",
    val moistureList: List<Float> = listOf(),
    val humidityList: List<Float> = listOf(),
    val rainList: List<Float> = listOf(),
)