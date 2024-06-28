package com.example.sproutlink.ui

data class InfoUiState(
    var lux: String = "0",
    var lastWatered: String = "0",
    var lightLevel: Int = 0,
    var lightOverride: Boolean = false,
    var temperature: String = "0",
    var pressure: String = "0",
    var moistureList: List<Float> = listOf(),
    var humidityList: List<Float> = listOf(),
    var rainList: List<Float> = listOf(),
)