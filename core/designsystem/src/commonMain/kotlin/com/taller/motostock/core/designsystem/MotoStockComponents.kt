package com.taller.motostock.core.designsystem

import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.foundation.BorderStroke
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.unit.dp

/** Superficie base para mantener la misma jerarquía visual en todas las features. */
@Composable
fun MotoStockCard(
    modifier: Modifier = Modifier,
    onClick: (() -> Unit)? = null,
    content: @Composable ColumnScope.() -> Unit
) {
    val colors = MotoStockDs.colors
    val cardColors = CardDefaults.cardColors(containerColor = colors.surfaceContainerLowest)
    val cardElevation = CardDefaults.cardElevation(defaultElevation = MotoStockDs.elevation.small)

    if (onClick == null) {
        Card(
            modifier = modifier,
            shape = MotoStockDs.shapes.medium,
            colors = cardColors,
            elevation = cardElevation,
            border = BorderStroke(1.dp, colors.outlineVariant.copy(alpha = 0.45f)),
            content = content
        )
    } else {
        Card(
            onClick = onClick,
            modifier = modifier,
            shape = MotoStockDs.shapes.medium,
            colors = cardColors,
            elevation = cardElevation,
            border = BorderStroke(1.dp, colors.outlineVariant.copy(alpha = 0.45f)),
            content = content
        )
    }
}

/** Campo de formulario con los mismos colores y estados en toda la aplicación. */
@Composable
fun MotoStockTextField(
    value: String,
    onValueChange: (String) -> Unit,
    label: String,
    modifier: Modifier = Modifier,
    placeholder: String = "",
    isError: Boolean = false,
    supportingText: String? = null,
    keyboardOptions: KeyboardOptions = KeyboardOptions.Default,
    visualTransformation: VisualTransformation = VisualTransformation.None
) {
    val colors = MotoStockDs.colors
    OutlinedTextField(
        value = value,
        onValueChange = onValueChange,
        modifier = modifier.fillMaxWidth(),
        label = { Text(label) },
        placeholder = if (placeholder.isBlank()) null else ({ Text(placeholder) }),
        singleLine = true,
        isError = isError,
        supportingText = supportingText?.let { { Text(it) } },
        keyboardOptions = keyboardOptions,
        visualTransformation = visualTransformation,
        shape = MotoStockDs.shapes.small,
        textStyle = MotoStockDs.typography.bodyMedium,
        colors = OutlinedTextFieldDefaults.colors(
            focusedBorderColor = colors.primary,
            unfocusedBorderColor = colors.outlineVariant,
            errorBorderColor = colors.error,
            focusedLabelColor = colors.primary,
            unfocusedLabelColor = colors.onSurfaceVariant,
            cursorColor = colors.primary,
            focusedContainerColor = colors.surfaceContainerLow,
            unfocusedContainerColor = colors.surfaceContainerLow,
            errorContainerColor = colors.errorContainer.copy(alpha = 0.2f)
        )
    )
}

@Composable
fun MotoStockStatusPill(
    text: String,
    containerColor: Color,
    contentColor: Color,
    modifier: Modifier = Modifier
) {
    androidx.compose.material3.Surface(
        modifier = modifier,
        shape = MotoStockDs.shapes.full,
        color = containerColor
    ) {
        Text(
            text = text,
            modifier = Modifier.padding(horizontal = 10.dp, vertical = 5.dp),
            style = MotoStockDs.typography.labelSmall,
            color = contentColor
        )
    }
}
