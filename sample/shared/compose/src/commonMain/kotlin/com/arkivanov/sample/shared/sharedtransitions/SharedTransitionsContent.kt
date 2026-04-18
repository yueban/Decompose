package com.arkivanov.sample.shared.sharedtransitions

import androidx.compose.animation.ExperimentalSharedTransitionApi
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import com.arkivanov.decompose.ExperimentalDecomposeApi
import com.arkivanov.decompose.extensions.compose.experimental.stack.ChildStack
import com.arkivanov.decompose.extensions.compose.experimental.stack.animation.PredictiveBackParams
import com.arkivanov.decompose.extensions.compose.experimental.stack.animation.fade
import com.arkivanov.decompose.extensions.compose.experimental.stack.animation.plus
import com.arkivanov.decompose.extensions.compose.experimental.stack.animation.scale
import com.arkivanov.decompose.extensions.compose.experimental.stack.animation.stackAnimation
import com.arkivanov.decompose.extensions.compose.stack.animation.predictiveback.materialPredictiveBackAnimatable
import com.arkivanov.sample.shared.sharedtransitions.SharedTransitionsComponent.Child.GalleryChild
import com.arkivanov.sample.shared.sharedtransitions.gallery.GalleryContent

@OptIn(ExperimentalSharedTransitionApi::class, ExperimentalDecomposeApi::class)
@Composable
internal fun SharedTransitionsContent(
    component: SharedTransitionsComponent,
    modifier: Modifier = Modifier,
) {
    ChildStack(
        stack = component.stack,
        modifier = modifier.fillMaxSize().background(Color.Black),
        animation = stackAnimation(
            animator = fade() + scale(),
            predictiveBackParams = {
                PredictiveBackParams(
                    backHandler = component.backHandler,
                    onBack = component::onBack,
                    animatable = ::materialPredictiveBackAnimatable,
                )
            },
        ),
    ) {
        when (val child = it.instance) {
            is GalleryChild ->
                GalleryContent(
                    component = child.component,
                    modifier = Modifier.fillMaxSize(),
                )
        }
    }
}
