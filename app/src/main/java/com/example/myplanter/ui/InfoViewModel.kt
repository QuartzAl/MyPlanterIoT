package com.example.myplanter.ui

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.myplanter.model.MqttTopic
import com.example.myplanter.constants
import com.example.myplanter.constants.LIGHT_TOGGLE_OVERRIDE_MESSAGE
import com.example.myplanter.constants.LIGHT_VALUE_MESSAGE
import com.example.myplanter.constants.RESPOND_TOPIC_ALL
import com.example.myplanter.constants.WATER_DATE_MESSAGE
import com.example.myplanter.constants.WATER_MESSAGE
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.FlowPreview
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.debounce
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.launchIn
import kotlinx.datetime.Instant.Companion.fromEpochSeconds
import kotlinx.datetime.toLocalDateTime
import org.eclipse.paho.android.service.MqttAndroidClient
import org.eclipse.paho.client.mqttv3.IMqttActionListener
import org.eclipse.paho.client.mqttv3.IMqttDeliveryToken
import org.eclipse.paho.client.mqttv3.IMqttToken
import org.eclipse.paho.client.mqttv3.MqttCallback
import org.eclipse.paho.client.mqttv3.MqttException
import org.eclipse.paho.client.mqttv3.MqttMessage
import java.time.format.DateTimeFormatter
import java.util.Date
import javax.inject.Inject
import kotlin.math.round

@HiltViewModel
class InfoViewModel @Inject constructor(
    private val mqttClient: MqttAndroidClient,
) : ViewModel() {
    init {
        connectMqtt()
    }

    private val _uiState = MutableStateFlow(InfoUiState())
    val uiState = _uiState.asStateFlow()

    private val _lightSlider = MutableStateFlow(0.0f)
    val lightSlider = _lightSlider.asStateFlow()

    @OptIn(FlowPreview::class, ExperimentalCoroutinesApi::class)
    val lightLevel = _lightSlider
        .debounce(500)
        .distinctUntilChanged()
        .flatMapLatest {setLightValue ->
            val value = round(setLightValue * 100).toInt()
            val message = "$LIGHT_VALUE_MESSAGE$value"
            Log.d("MQTTdata", "sent value: $message")
            publish(requestTopic, message)
            return@flatMapLatest flow <Int> { emit(value) }
        }.launchIn(viewModelScope)

    private val _waterConfirm = MutableStateFlow(false)
    val waterConfirm = _waterConfirm.asStateFlow()

    fun toggleLightOverride(){
        publish(requestTopic, LIGHT_TOGGLE_OVERRIDE_MESSAGE)
    }

    fun setLightLevel(lightLevel: Float){
        _lightSlider.value = lightLevel
    }

    fun requestLastWaterDate(){
        publish(requestTopic, WATER_DATE_MESSAGE)
    }

    fun waterPlant(){
        publish(requestTopic, WATER_MESSAGE)
        requestLastWaterDate()
    }

    val subscribeList = listOf(
        MqttTopic(constants.LIGHT_TOPIC, 1),
        MqttTopic(constants.MOISTURE_TOPIC, 1),
        MqttTopic(RESPOND_TOPIC_ALL, 1),
    )

    val requestTopic = MqttTopic(constants.REQUEST_TOPIC, 1)

    private fun connectMqtt() {
        try {
            val token: IMqttToken = mqttClient.connect()
            token.actionCallback = object : IMqttActionListener {
                override fun onSuccess(asyncActionToken: IMqttToken?) {
                    Log.d("MQTT", "connection success")
                    subscribe(subscribeList)
                }

                override fun onFailure(asyncActionToken: IMqttToken?, exception: Throwable?) {
                    Log.d("MQTT", "connection failure")
                }
            }
            mqttClient.setCallback(object : MqttCallback {
                override fun connectionLost(cause: Throwable?) {
                    Log.d("MQTT", "connection lost, reconnecting...")

                    connectMqtt()
                }

                override fun messageArrived(topic: String?, message: MqttMessage?) {
                    when (topic) {
                        constants.LIGHT_TOPIC -> {
                            _uiState.value = uiState.value.copy(lux = message.toString())
                        }
                        constants.MOISTURE_TOPIC -> {
                            _uiState.value = uiState.value.copy(moisture = message.toString())
                        }
                        constants.RESPOND_OVERRIDE -> {
                            val value = message.toString() == "1"
                            _uiState.value = uiState.value.copy(lightOverride = value)
                        }
                        constants.RESPOND_LIGHT_LEVEL -> {
                            Log.d("MQTTdata", "received value: ${message.toString()}")
                            val value = message.toString().toInt()
                            _uiState.value = uiState.value.copy(lightLevel = value)
                        }
                        constants.RESPOND_DATE -> {
                            Log.d("MQTTdata", "received value: ${message.toString()}")
                            if (message != null) {
                                val date = Date(message.toString().toLong() * 1000)
                                val formattedDate = date.toString().split(" ").filterIndexed {
                                        index, _ -> index != 4
                                }.joinToString(" ")

                                Log.d("MQTTdata", "processed date2: $formattedDate")
                                _uiState.value = uiState.value.copy(lastWatered = formattedDate)
                            }

                        }
                        constants.RESPOND_TOPIC -> {
                            Log.d("MQTTdata", "received value: ${message.toString()}")
                            val payload = message.toString()
                            val split = payload.split(" ")
                            when (split[0]) {
                                WATER_MESSAGE -> {
                                    _waterConfirm.value = true
                                }
                            }

                        }
                    }
                }

                override fun deliveryComplete(token: IMqttDeliveryToken?) {
                    Log.d("MQTT", "deliveryComplete")
                }
            })
        } catch (e: MqttException) {
            e.printStackTrace()
        }
    }

    private fun subscribe(mqttTopic: List<MqttTopic>) {
        for (topic in mqttTopic){

            val token = mqttClient.subscribe(topic.name, topic.qos)
            token.actionCallback = object : IMqttActionListener {
                override fun onSuccess(asyncActionToken: IMqttToken?) {
                    Log.d("MQTT", "subscribe success")
                    if (topic.name == RESPOND_TOPIC_ALL){
                        requestLastWaterDate()
                    }
                }

                override fun onFailure(asyncActionToken: IMqttToken?, exception: Throwable?) {
                    Log.d("MQTT", "subscribe failed")
                }
            }
        }
    }

    private fun publish(topic: MqttTopic, payload:String){
        val message = MqttMessage()
        message.payload = payload.toByteArray()
        message.qos = topic.qos
        message.isRetained = false
        mqttClient.publish(topic.name, message, null,  object:IMqttActionListener{
            override fun onSuccess(asyncActionToken: IMqttToken?) {
                Log.d("MQTT", "publish success")
            }
            override fun onFailure(asyncActionToken: IMqttToken?, exception: Throwable?) {
                Log.d("MQTT", "publish failed")
            }
        })
    }

    fun unsubscribe(topic: MqttTopic){
        mqttClient.unsubscribe(topic.name, null, object:IMqttActionListener{
            override fun onSuccess(asyncActionToken: IMqttToken?) {
                Log.d("MQTT", "unsubscribe success")
            }
            override fun onFailure(asyncActionToken: IMqttToken?, exception: Throwable?) {
                Log.d("MQTT", "unsubscribe failed")
            }
        })
    }

    fun disconnectMqtt(){
        try{
            var token= mqttClient.disconnect()
            token?.actionCallback=object:IMqttActionListener{
                override fun onSuccess(asyncActionToken: IMqttToken?) {
                    Log.d("MQTT","disconnect success")
                }
                override fun onFailure(asyncActionToken: IMqttToken?, exception: Throwable?) {
                    Log.d("MQTT", "disconnect failed");
                }
            }
        }catch (e:MqttException){
        }
    }


}