package com.arkivanov.sample.shared.sharedtransitions.thumbnail

import androidx.compose.animation.AnimatedVisibilityScope
import androidx.compose.animation.ExperimentalSharedTransitionApi
import androidx.compose.animation.SharedTransitionScope
import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import com.arkivanov.sample.shared.painterResource

@OptIn(ExperimentalSharedTransitionApi::class)
@Composable
fun SharedTransitionScope.ThumbnailContent(
    component: ThumbnailComponent,
    animatedVisibilityScope: AnimatedVisibilityScope,
    enableSharedElement: Boolean,
    modifier: Modifier = Modifier,
) {
    Image(
        painter = painterResource(component.image.resourceId),
        contentDescription = null,
        modifier = modifier
            .aspectRatio(1F)
            .then(
                if (enableSharedElement) {
                    Modifier.sharedBounds(
                        sharedContentState = rememberSharedContentState(key = component.image.id),
                        animatedVisibilityScope = animatedVisibilityScope,
                    )
                } else {
                    Modifier
                }
            )
            .clickable(onClick = component::onClicked),
        contentScale = ContentScale.Crop,
    )
}
