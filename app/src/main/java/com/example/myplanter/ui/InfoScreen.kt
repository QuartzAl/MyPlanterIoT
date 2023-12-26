package com.example.myplanter.ui

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Slider
import androidx.compose.material3.Switch
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Alignment.Companion.CenterHorizontally
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.example.myplanter.ui.theme.MyPlanterTheme
import kotlinx.coroutines.flow.StateFlow

@Composable
fun InfoScreen(
    infoViewModel: InfoViewModel = hiltViewModel()
    ) {

    val infoUiState by infoViewModel.uiState.collectAsState()
    val sliderValue by infoViewModel.lightSlider.collectAsState()

    MainActivityContent(
        lux = infoUiState.lux,
        moisture = infoUiState.moisture,
        lastWatered = infoUiState.lastWatered,
        onRequestLastWaterDate = infoViewModel::requestLastWaterDate,
        onWater = infoViewModel::waterPlant,
        lightLevel = infoUiState.lightLevel,
        sliderValue = sliderValue,
        onLightLevelChange = infoViewModel::setLightLevel,
        lightOverride = infoUiState.lightOverride,
        onLightOverrideChange = infoViewModel::toggleLightOverride
    )

}

@Composable
fun MainActivityContent(
    lux: String,
    moisture: String,
    lastWatered: String,
    onRequestLastWaterDate: () -> Unit,
    onWater: () -> Unit,
    lightLevel: Int,
    onLightLevelChange: (Float) -> Unit,
    lightOverride: Boolean,
    onLightOverrideChange: () -> Unit,
    modifier: Modifier = Modifier,
    sliderValue: Float
) {
    Column(
        modifier = modifier.padding(24.dp),
        horizontalAlignment = CenterHorizontally
    ) {
        Text(
            text = "Plant info",
            style = MaterialTheme.typography.titleLarge
        )
        Spacer(modifier = Modifier.padding(8.dp))
        Row {
            Moisture(moisture, Modifier.weight(1f))
            Spacer(modifier = Modifier.padding(8.dp))
            Light(lux, Modifier.weight(1f))
        }
        Spacer(modifier = Modifier.padding(8.dp))
        LastWaterDate(lastWatered, onWater, onRequestLastWaterDate)
        Spacer(modifier = Modifier.padding(8.dp))
        GrowLightControl(
            lightOverride,
            onLightOverrideChange,
            lightLevel,
            onLightLevelChange,
            sliderValue
        )
    }
}

@Composable
fun Moisture(moisture: String, modifier: Modifier = Modifier) {
    Card (
        modifier = modifier
            .shadow(4.dp)
    ) {
        Column (modifier = Modifier.padding(16.dp)) {
            Text(text = "Soil moisture")
            Text(text = moisture)
        }
    }
}

@Composable
fun Light(lux: String, modifier: Modifier = Modifier) {
    Card(
        modifier = modifier
            .shadow(4.dp)
    ) {
        Column (modifier = Modifier.padding(16.dp)) {
            Text(text = "Ambient light")
            Row {
                Text(text = lux)
                Text(text = " lux")
            }
        }
    }
}

@Composable
fun LastWaterDate(
    lastWatered: String,
    onWater: () -> Unit ,
    onRequestLastWaterDate: () -> Unit,
    modifier: Modifier = Modifier
) {
    Card (
        modifier = modifier
            .shadow(4.dp)
            .fillMaxWidth()
    ){
        Column(
            Modifier
                .padding(16.dp)
                .fillMaxWidth(),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(text = "Last watered")
            Spacer(modifier = Modifier.padding(8.dp))
            Text(text = lastWatered)
            Row (
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 8.dp),
            ) {
                Button(
                    onClick = { onWater() },
                    modifier = Modifier.weight(1f)
                ) {
                    Text(text = "Water")
                }
                Spacer(modifier = Modifier.padding(8.dp))
                Button(
                    onClick = { onRequestLastWaterDate() },
                    modifier = Modifier.weight(1f)
                ) {
                    Text(text = "Update Data")
                }
            }
        }
    }
}

@Composable
fun GrowLightControl(
    override: Boolean,
    onOverrideChange: () -> Unit,
    lightLevel: Int,
    onLightLevelChange: (Float) -> Unit,
    sliderValue: Float,
    modifier: Modifier = Modifier
) {
    Card (
        modifier = modifier
            .fillMaxWidth()
            .shadow(4.dp)
    ){
        Column (
            modifier = Modifier
                .padding(8.dp),
            horizontalAlignment = CenterHorizontally
        ) {
            Text(text = "Grow light")
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(text = "Override")
                Spacer(modifier = Modifier.weight(1f))
                Switch(
                    checked = override,
                    onCheckedChange = { onOverrideChange() }

                )
            }
            Spacer(modifier = Modifier.padding(8.dp))
            Column(
                modifier = Modifier
                    .fillMaxWidth(),

            ) {
                Text(text = "Light level")
                if (!override) Text(text = "Currently in auto mode")
                else Text(
                    text = "$lightLevel %",
                    modifier = Modifier.fillMaxWidth(),
                    textAlign = TextAlign.Center
                )
                Slider(
                    value = sliderValue,
                    onValueChange = { onLightLevelChange(it) },
                    enabled = override
                )
            }
        }
    }

}

@Preview(showBackground = true)
@Composable
fun GreetingPreview() {
    MyPlanterTheme {
        MainActivityContent(
            lux = "100",
            moisture = "50",
            lastWatered = "2021-09-01",
            lightLevel = 50,
            onLightLevelChange = { },
            lightOverride = true,
            onLightOverrideChange = { },
            sliderValue = 0.5f,
            onWater = { },
            onRequestLastWaterDate = { }
        )
    }
}