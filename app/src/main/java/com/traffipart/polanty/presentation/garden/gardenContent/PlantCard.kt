package com.traffipart.polanty.presentation.garden.gardenContent

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Card
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.unit.dp
import coil3.compose.AsyncImage
import com.traffipart.polanty.domain.model.plant.Plant
import com.traffipart.polanty.presentation.theme.spacing

/**
 * Reusable card composable for displaying an individual plant's thumbnail image and display name.
 * Used consistently across the "All Plants" tab in the garden screen and in space details screens.
 *
 * @param plant The [Plant] domain model containing the details to render.
 * @param onClick Callback triggered when the plant card is tapped.
 * @param modifier The [Modifier] to be applied to the card layout.
 */
@Composable
fun PlantCard(
    plant: Plant,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
) {
    Card(
        modifier = modifier.fillMaxWidth(),
        onClick = onClick,
    ) {
        Column(
            modifier = Modifier.padding(MaterialTheme.spacing.medium),
        ) {
            plant.imageUri?.let { imageUri ->
                AsyncImage(
                    model = imageUri,
                    contentDescription = plant.displayName,
                    modifier =
                        Modifier
                            .fillMaxWidth()
                            .height(180.dp),
                    contentScale = ContentScale.Crop,
                )
            }
            Text(
                text = plant.displayName,
                style = MaterialTheme.typography.titleMedium,
                modifier = Modifier.padding(top = MaterialTheme.spacing.small),
            )
        }
    }
}
