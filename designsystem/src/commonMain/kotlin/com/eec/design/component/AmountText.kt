package com.eec.design.component

import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.TextStyle
import com.eec.design.theme.EecTheme

enum class AmountSign {
    None,
    Positive,
    Negative,
}

@Composable
fun AmountText(
    text: String,
    modifier: Modifier = Modifier,
    sign: AmountSign = AmountSign.None,
    style: TextStyle = EecTheme.typography.bodyStrong,
    color: Color = EecTheme.colors.onBackground,
) {
    val prefix = when (sign) {
        AmountSign.None -> ""
        AmountSign.Positive -> "+"
        AmountSign.Negative -> "-"
    }
    Text(
        text = prefix + text,
        modifier = modifier,
        style = style,
        color = color,
        maxLines = 1,
    )
}
