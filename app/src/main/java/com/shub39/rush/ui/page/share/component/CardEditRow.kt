package com.shub39.rush.ui.page.share.component

import androidx.compose.foundation.layout.Row
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import com.shub39.rush.R
import com.shub39.rush.database.SettingsDataStore
import kotlinx.coroutines.launch

@Composable
fun CardEditRow(
    modifier: Modifier,
    corners: Boolean = false,
    colors: Boolean = false,
    tint: Color
) {
    val context = LocalContext.current
    val coroutineScope = rememberCoroutineScope()

    val cardColorFlow = remember { SettingsDataStore.getCardColorFlow(context) }
    val cardCornersFlow = remember { SettingsDataStore.getCardRoundnessFlow(context) }

    val cardColorType by cardColorFlow.collectAsState(initial = "")
    val cardCornersType by cardCornersFlow.collectAsState(initial = "")

    Row(
        modifier = modifier
    ) {
        if (corners) {
            IconButton(
                onClick = {
                    coroutineScope.launch {
                        when (cardCornersType) {
                            "Rounded" -> SettingsDataStore.updateCardRoundness(
                                context,
                                "Flat"
                            )

                            else -> SettingsDataStore.updateCardRoundness(
                                context,
                                "Rounded"
                            )
                        }
                    }
                }
            ) {
                Icon(
                    painter = when (cardCornersType) {
                        "Rounded" -> painterResource(id = R.drawable.baseline_circle_24)
                        else -> painterResource(id = R.drawable.baseline_square_24)
                    },
                    contentDescription = null,
                    tint = tint
                )
            }
        }

        if (colors) {
            IconButton(
                onClick = {
                    coroutineScope.launch {
                        when (cardColorType) {
                            "Vibrant" -> SettingsDataStore.updateCardColor(context, "Muted")
                            "Muted" -> SettingsDataStore.updateCardColor(context, "Custom")
                            "Custom" -> SettingsDataStore.updateCardColor(context, "Default")
                            else -> SettingsDataStore.updateCardColor(context, "Vibrant")
                        }
                    }
                }
            ) {
                Icon(
                    painter = when (cardColorType) {
                        "Vibrant" -> painterResource(id = R.drawable.round_remove_red_eye_24)
                        "Muted" -> painterResource(id = R.drawable.round_lens_blur_24)
                        "Custom" -> painterResource(id = R.drawable.baseline_edit_square_24)
                        else -> painterResource(id = R.drawable.round_disabled_by_default_24)
                    },
                    contentDescription = null,
                    tint = tint
                )
            }
        }

    }
}