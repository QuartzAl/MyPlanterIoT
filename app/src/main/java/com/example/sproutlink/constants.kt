package com.example.sproutlink

object constants {
    const val TAG = "MQTT"
    const val SERVER_URI = "tcp://192.168.1.150:1883"
    const val CLIENT_ID = "androidkt"
    const val MAX_DATA_POINTS = 20

    private const val MAIN_TOPIC = "planter"

    const val LIGHT_TOPIC = "$MAIN_TOPIC/light"
    const val MOISTURE_TOPIC = "$MAIN_TOPIC/moisture"
    const val HUMIDITY_TOPIC = "$MAIN_TOPIC/humidity"
    const val TEMPERATURE_TOPIC = "$MAIN_TOPIC/temperature"
    const val PRESSURE_TOPIC = "$MAIN_TOPIC/pressure"
    const val RAIN_TOPIC = "$MAIN_TOPIC/rain"

    const val REQUEST_TOPIC = "$MAIN_TOPIC/request"
    const val WATER_DATE_MESSAGE = "D"
    const val WATER_MESSAGE = "W"
    const val LIGHT_TOGGLE_OVERRIDE_MESSAGE = "L"
    const val LIGHT_VALUE_MESSAGE = "V"

    const val RESPOND_TOPIC = "$MAIN_TOPIC/respond"
    const val RESPOND_TOPIC_ALL = "$RESPOND_TOPIC/#"
    const val RESPOND_OVERRIDE = "$RESPOND_TOPIC/override"
    const val RESPOND_LIGHT_LEVEL = "$RESPOND_TOPIC/light"
    const val RESPOND_DATE = "$RESPOND_TOPIC/date"


}