package com.eec.design.component

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Slider
import androidx.compose.material3.SliderDefaults
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import com.eec.design.theme.ColorHex
import com.eec.design.theme.EecSizes
import com.eec.design.theme.EecTheme
import com.eec.design.theme.HsvColor

@Composable
fun ColorPicker(
    color: Color,
    onColorSelected: (String) -> Unit,
    hueLabel: String,
    saturationLabel: String,
    brightnessLabel: String,
    hexLabel: String,
    hexSupport: String,
    previewLabel: String,
    modifier: Modifier = Modifier,
) {
    var hsv by remember { mutableStateOf(ColorHex.toHsv(color)) }
    var hexText by remember { mutableStateOf(ColorHex.format(color)) }

    LaunchedEffect(color) {
        val incoming = ColorHex.format(color)
        if (incoming != hexText) {
            hsv = ColorHex.toHsv(color)
            hexText = incoming
        }
    }

    fun emit(next: HsvColor) {
        hsv = next
        val hex = ColorHex.format(ColorHex.toColor(next))
        hexText = hex
        onColorSelected(hex)
    }

    Column(modifier = modifier.fillMaxWidth(), verticalArrangement = Arrangement.spacedBy(EecTheme.spacing.xs)) {
        ColorPreview(color = ColorHex.toColor(hsv), hex = hexText, label = previewLabel)
        HsvSlider(
            label = hueLabel,
            value = hsv.hue,
            range = 0f..HUE_MAX,
            display = "${hsv.hue.toInt()}°",
        ) { emit(hsv.copy(hue = it)) }
        HsvSlider(
            label = saturationLabel,
            value = hsv.saturation,
            range = 0f..1f,
            display = "${(hsv.saturation * PERCENT).toInt()}%",
        ) { emit(hsv.copy(saturation = it)) }
        HsvSlider(
            label = brightnessLabel,
            value = hsv.value,
            range = 0f..1f,
            display = "${(hsv.value * PERCENT).toInt()}%",
        ) { emit(hsv.copy(value = it)) }
        EecTextField(
            value = hexText,
            onValueChange = { raw ->
                hexText = raw
                ColorHex.parse(raw)?.let { parsed ->
                    hsv = ColorHex.toHsv(parsed)
                    onColorSelected(ColorHex.format(parsed))
                }
            },
            label = hexLabel,
            placeholder = "#0E7C6B",
            isError = ColorHex.parse(hexText) == null,
            supportingText = hexSupport,
        )
    }
}

@Composable
private fun ColorPreview(color: Color, hex: String, label: String) {
    Surface(
        modifier = Modifier.fillMaxWidth(),
        shape = EecTheme.shapes.card,
        color = color,
        border = BorderStroke(EecSizes.border, EecTheme.colors.outline),
    ) {
        Row(
            modifier = Modifier.padding(EecTheme.spacing.md),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            Text(
                text = label,
                style = EecTheme.typography.bodyStrong,
                color = ColorHex.onColorFor(color),
            )
            Spacer(modifier = Modifier.weight(1f))
            Text(
                text = hex,
                style = EecTheme.typography.label,
                color = ColorHex.onColorFor(color),
            )
        }
    }
}

@Composable
private fun HsvSlider(
    label: String,
    value: Float,
    range: ClosedFloatingPointRange<Float>,
    display: String,
    onValueChange: (Float) -> Unit,
) {
    Column {
        Row(modifier = Modifier.fillMaxWidth()) {
            Text(
                text = label,
                style = EecTheme.typography.label,
                color = EecTheme.colors.onSurfaceVariant,
            )
            Spacer(modifier = Modifier.weight(1f))
            Text(
                text = display,
                style = EecTheme.typography.label,
                color = EecTheme.colors.onBackground,
            )
        }
        Slider(
            value = value,
            onValueChange = onValueChange,
            valueRange = range,
            colors = SliderDefaults.colors(
                thumbColor = EecTheme.colors.primary,
                activeTrackColor = EecTheme.colors.primary,
                inactiveTrackColor = EecTheme.colors.surfaceVariant,
            ),
        )
    }
}

private const val HUE_MAX: Float = 360f
private const val PERCENT: Float = 100f
