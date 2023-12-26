package com.example.myplanter

object constants {
    const val TAG = "MQTT"
    const val SERVER_URI = "tcp://broker.hivemq.com:1883"
    const val CLIENT_ID = "androidkt"

    private const val MAIN_TOPIC = "planter2.0"

    const val LIGHT_TOPIC = "$MAIN_TOPIC/light"
    const val MOISTURE_TOPIC = "$MAIN_TOPIC/moisture"

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