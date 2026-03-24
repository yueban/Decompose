package com.arkivanov.decompose.extensions.compose.experimental.stack.animation

import androidx.compose.animation.core.FiniteAnimationSpec
import androidx.compose.animation.core.tween
import androidx.compose.foundation.gestures.Orientation
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.layout
import com.arkivanov.decompose.ExperimentalDecomposeApi

/**
 * A simple sliding animation. Children enter from one side and exit to another side.
 */
@ExperimentalDecomposeApi
fun slide(
    animationSpec: FiniteAnimationSpec<Float> = tween(),
    orientation: Orientation = Orientation.Horizontal,
    reverseDirection: Boolean = false,
    ): StackAnimator =
    stackAnimator(animationSpec = animationSpec) { factor, _ ->
        when (orientation) {
            Orientation.Horizontal -> Modifier.offsetXFactor(factor = factor, reverseDirection = reverseDirection)
            Orientation.Vertical -> Modifier.offsetYFactor(factor = factor, reverseDirection = reverseDirection)
        }
    }

private fun Modifier.offsetXFactor(factor: Float, reverseDirection: Boolean): Modifier =
    layout { measurable, constraints ->
        val placeable = measurable.measure(constraints)

        layout(placeable.width, placeable.height) {
            val x = placeable.width.toFloat() * if (reverseDirection) -factor else factor
            placeable.placeRelative(x = x.toInt(), y = 0)
        }
    }

private fun Modifier.offsetYFactor(factor: Float, reverseDirection: Boolean): Modifier =
    layout { measurable, constraints ->
        val placeable = measurable.measure(constraints)

        layout(placeable.width, placeable.height) {
            val y = placeable.height.toFloat() * if (reverseDirection) -factor else factor
            placeable.placeRelative(x = 0, y = y.toInt())
        }
    }
