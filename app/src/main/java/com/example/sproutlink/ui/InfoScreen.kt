package com.example.sproutlink.ui

import android.graphics.Typeface
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Slider
import androidx.compose.material3.Switch
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Alignment.Companion.CenterHorizontally
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import com.example.sproutlink.R
import com.example.sproutlink.ui.theme.MyPlanterTheme
import com.patrykandpatrick.vico.compose.cartesian.CartesianChartHost
import com.patrykandpatrick.vico.compose.cartesian.axis.rememberAxisLabelComponent
import com.patrykandpatrick.vico.compose.cartesian.axis.rememberBottomAxis
import com.patrykandpatrick.vico.compose.cartesian.axis.rememberStartAxis
import com.patrykandpatrick.vico.compose.cartesian.layer.rememberLineCartesianLayer
import com.patrykandpatrick.vico.compose.cartesian.layer.rememberLineSpec
import com.patrykandpatrick.vico.compose.cartesian.rememberCartesianChart
import com.patrykandpatrick.vico.compose.common.component.rememberShapeComponent
import com.patrykandpatrick.vico.compose.common.component.rememberTextComponent
import com.patrykandpatrick.vico.compose.common.of
import com.patrykandpatrick.vico.compose.common.rememberLegendItem
import com.patrykandpatrick.vico.compose.common.rememberVerticalLegend
import com.patrykandpatrick.vico.compose.common.shader.color
import com.patrykandpatrick.vico.compose.common.shape.rounded
import com.patrykandpatrick.vico.compose.common.vicoTheme
import com.patrykandpatrick.vico.core.cartesian.CartesianDrawContext
import com.patrykandpatrick.vico.core.cartesian.CartesianMeasureContext
import com.patrykandpatrick.vico.core.cartesian.data.CartesianChartModelProducer
import com.patrykandpatrick.vico.core.common.Dimensions
import com.patrykandpatrick.vico.core.common.shader.DynamicShader
import com.patrykandpatrick.vico.core.common.shape.Shape
import java.util.Locale

@Composable
fun InfoScreen(
    infoViewModel: InfoViewModel = hiltViewModel()
) {

    val infoUiState by infoViewModel.uiState.collectAsState()
    val sliderValue by infoViewModel.lightSlider.collectAsState()
    val modelProducer = infoViewModel._modelProducer


    MainActivityContent(
        uiState = infoUiState,
        onRequestLastWaterDate = infoViewModel::requestLastWaterDate,
        onWater = infoViewModel::waterPlant,
        sliderValue = sliderValue,
        onLightLevelChange = infoViewModel::setLightLevel,
        onLightOverrideChange = infoViewModel::toggleLightOverride,
        modelProducer = modelProducer
    )

}

@Composable
fun MainActivityContent(
    uiState: InfoUiState,
    onRequestLastWaterDate: () -> Unit,
    onWater: () -> Unit,
    onLightLevelChange: (Float) -> Unit,
    onLightOverrideChange: () -> Unit,
    modifier: Modifier = Modifier,
    sliderValue: Float,
    modelProducer: CartesianChartModelProducer
) {
    Column(
        modifier = modifier
            .padding(24.dp)
            .verticalScroll(rememberScrollState()),
        horizontalAlignment = CenterHorizontally

    ) {
        Text(
            text = "Plant info",
            style = MaterialTheme.typography.titleLarge
        )
        Graph(modelProducer = modelProducer, modifier = Modifier)
        Spacer(modifier = Modifier.padding(8.dp))
        Row {
            TextBox(
                header = "Light Level",
                data = uiState.lux,
                unit = " lux",
                modifier = Modifier.weight(1f)
            )
            Spacer(modifier = Modifier.padding(8.dp))
            TextBox(
                header = "Soil Moisture",
                data = (uiState.moistureList.lastOrNull() ?: "0").toString(),
                unit = " %",
                modifier = Modifier.weight(1f)
            )
        }

        Spacer(modifier = Modifier.padding(8.dp))
        LastWaterDate(uiState.lastWatered, onWater, onRequestLastWaterDate)
        Spacer(modifier = Modifier.padding(8.dp))
        GrowLightControl(
            uiState.lightOverride,
            onLightOverrideChange,
            uiState.lightLevel,
            onLightLevelChange,
            sliderValue
        )
        Spacer(modifier = Modifier.padding(7.dp))
        Text(
            text = "Environment info",
            style = MaterialTheme.typography.titleLarge
        )
        Spacer(modifier = Modifier.padding(7.dp))
        Row {
            TextBox(
                header = "Temperature",
                data = uiState.temperature,
                unit = " C",
                modifier = Modifier.weight(1f)
            )
            Spacer(modifier = Modifier.padding(7.dp))
            TextBox(
                header = "Humidity",
                data = (uiState.humidityList.lastOrNull() ?: "0").toString(),
                unit = " %",
                modifier = Modifier.weight(1f)
            )

        }
        Spacer(modifier = Modifier.padding(7.dp))
        Row {
            TextBox(
                header = "Pressure",
                data = uiState.pressure,
                unit = " hPA",
                modifier = Modifier.weight(1f)
            )
            Spacer(modifier = Modifier.padding(7.dp))
            TextBox(
                header = "Rain",
                data = String.format("%.2f", (uiState.rainList.lastOrNull() ?: 0.0)),
                unit = " %",
                modifier = Modifier.weight(1f)
            )
        }
    }
}


@Composable
fun Graph(modelProducer: CartesianChartModelProducer, modifier: Modifier) {
    CartesianChartHost(
        chart = rememberCartesianChart(
            rememberLineCartesianLayer(
                lines=
                    chartColors.map {color ->
                        rememberLineSpec(shader = DynamicShader.color(color), backgroundShader = null)
                    }

            ),
            startAxis = rememberStartAxis(
                label = rememberAxisLabelComponent(
                    color = Color.Black,
                    background = rememberShapeComponent(
                        shape = Shape.rounded(4.dp),
                        color = Color(0xfffab94d)
                    ),
                    padding = Dimensions.of(horizontal = 8.dp, vertical = 2.dp),
                    margins = Dimensions.of(all = 4.dp),
                ),
            ),
            bottomAxis = rememberBottomAxis(),
            legend = rememberLegend(),
        ),
        modelProducer = modelProducer,
        marker = rememberMarker(),
        modifier = modifier,
    )
}


private val chartColors = listOf(Color(0xffb983ff), Color(0xff91b1fd), Color(0xff8fdaff))
private val chartNames =
    listOf(R.string.moisture_legend, R.string.humidity_legend, R.string.rain_legend)

@Composable
private fun rememberLegend() =
    rememberVerticalLegend<CartesianMeasureContext, CartesianDrawContext>(
        items =
        chartNames.mapIndexed { index, name ->
            rememberLegendItem(
                icon = rememberShapeComponent(Shape.Pill, chartColors[index]),
                label = rememberTextComponent(
                    color = vicoTheme.textColor,
                    textSize = 12.sp,
                    typeface = Typeface.MONOSPACE,
                ),
                labelText = stringResource(name),
            )
        },
        iconSize = 8.dp,
        iconPadding = 8.dp,
        spacing = 4.dp,
        padding = Dimensions.of(top = 8.dp),
    )


@Composable
fun TextBox(header: String, data: String, unit: String = "", modifier: Modifier = Modifier) {
    Card(
        modifier = modifier
            .shadow(4.dp)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Text(text = header)
            Row {
                Text(text = data)
                Text(text = unit)
            }
        }
    }
}

@Composable
fun Moisture(moisture: String, modifier: Modifier = Modifier) {
    Card(
        modifier = modifier
            .shadow(4.dp)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
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
        Column(modifier = Modifier.padding(16.dp)) {
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
    onWater: () -> Unit,
    onRequestLastWaterDate: () -> Unit,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier
            .shadow(4.dp)
            .fillMaxWidth()
    ) {
        Column(
            Modifier
                .padding(16.dp)
                .fillMaxWidth(),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(text = "Last watered")
            Spacer(modifier = Modifier.padding(8.dp))
            Text(text = lastWatered)
            Row(
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
    Card(
        modifier = modifier
            .fillMaxWidth()
            .shadow(4.dp)
    ) {
        Column(
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
            uiState = InfoUiState(),
            onLightLevelChange = { },
            onLightOverrideChange = { },
            sliderValue = 0.5f,
            onWater = { },
            onRequestLastWaterDate = { },
            modelProducer = remember { CartesianChartModelProducer.build() }
        )
    }
}