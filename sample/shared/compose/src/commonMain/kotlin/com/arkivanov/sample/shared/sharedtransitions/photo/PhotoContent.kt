package com.arkivanov.sample.shared.sharedtransitions.photo

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.AnimatedVisibilityScope
import androidx.compose.animation.ExperimentalSharedTransitionApi
import androidx.compose.animation.SharedTransitionLayout
import androidx.compose.animation.SharedTransitionScope
import androidx.compose.animation.core.Animatable
import androidx.compose.desktop.ui.tooling.preview.Preview
import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.detectDragGestures
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.requiredSize
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.IntSize
import com.arkivanov.sample.shared.painterResource
import com.arkivanov.sample.shared.utils.TopAppBar
import com.arkivanov.sample.shared.utils.WebDocumentTitle
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch
import kotlin.math.abs
import kotlin.math.max
import kotlin.math.min
import kotlin.math.roundToInt

private const val DISMISS_THRESHOLD_FACTOR = 4f
private const val SENSITIVITY_FACTOR = 3f
private const val SCALE_FRACTION = 0.5f

@OptIn(ExperimentalSharedTransitionApi::class)
@Composable
internal fun SharedTransitionScope.PhotoContent(
    component: PhotoComponent,
    animatedVisibilityScope: AnimatedVisibilityScope,
    modifier: Modifier = Modifier,
) {
    val dragToDismissState = rememberDragToDismissState(onDismiss = component::onCloseClicked)

    WebDocumentTitle(title = "Shared Transitions ${component.image.resourceId.name}")

    Column(modifier = modifier) {
        TopAppBar(title = "Photo ${component.image.id}", onCloseClick = component::onCloseClicked)

        BoxWithConstraints(
            modifier = modifier
                .fillMaxSize()
                .drawBehind {
                    drawRect(Color.Black.copy(alpha = dragToDismissState.alpha))
                }
                .pointerInput(Unit) {
                    detectDragGestures(
                        onDrag = { change, dragAmount ->
                            change.consume()
                            dragToDismissState.onDrag(dragAmount, size)
                        },
                        onDragEnd = { dragToDismissState.onDragEnd(size) }
                    )
                }
                .clickable(
                    interactionSource = remember { MutableInteractionSource() },
                    indication = null,
                    onClick = component::onCloseClicked,
                ),
            contentAlignment = Alignment.Center
        ) {
            val painter = painterResource(component.image.resourceId)
            val contentSize = calculateFittedSize(painter.intrinsicSize, constraints.maxWidth, constraints.maxHeight)
            FullScreenImage(dragToDismissState, contentSize, component, animatedVisibilityScope)
        }
    }
}

@OptIn(ExperimentalSharedTransitionApi::class)
@Composable
private fun SharedTransitionScope.FullScreenImage(
    dragToDismissState: DragToDismissState,
    contentSize: IntSize?,
    component: PhotoComponent,
    animatedVisibilityScope: AnimatedVisibilityScope
) {
    Box(
        modifier = Modifier
            .offset { IntOffset(dragToDismissState.offsetX.roundToInt(), dragToDismissState.offsetY.roundToInt()) }
            .then(
                if (contentSize != null) {
                    val scale = dragToDismissState.scale
                    Modifier.requiredSize(
                        with(LocalDensity.current) { (contentSize.width * scale).toDp() },
                        with(LocalDensity.current) { (contentSize.height * scale).toDp() }
                    )
                } else {
                    Modifier.fillMaxSize()
                }
            )
            .sharedBounds(
                sharedContentState = rememberSharedContentState(key = component.image.id),
                animatedVisibilityScope = animatedVisibilityScope,
            )
    ) {
        Image(
            painter = painterResource(component.image.resourceId),
            contentDescription = null,
            modifier = Modifier.fillMaxSize(),
            contentScale = ContentScale.Crop,
        )
    }
}

@Composable
private fun rememberDragToDismissState(onDismiss: () -> Unit): DragToDismissState {
    val scope = rememberCoroutineScope()
    return remember(onDismiss) { DragToDismissState(scope, onDismiss) }
}

private class DragToDismissState(
    private val scope: CoroutineScope,
    private val onDismiss: () -> Unit,
) {
    private val _offsetX = Animatable(0f)
    private val _offsetY = Animatable(0f)
    private val _scale = Animatable(1f)
    private val _alpha = Animatable(1f)

    val offsetX: Float get() = _offsetX.value
    val offsetY: Float get() = _offsetY.value
    val scale: Float get() = _scale.value
    val alpha: Float get() = _alpha.value

    fun onDrag(dragAmount: Offset, containerSize: IntSize) {
        scope.launch {
            val newX = _offsetX.value + dragAmount.x
            val newY = _offsetY.value + dragAmount.y
            _offsetX.snapTo(newX)
            _offsetY.snapTo(newY)

            val progress = max(
                abs(newX) / (containerSize.width / SENSITIVITY_FACTOR),
                abs(newY) / (containerSize.height / SENSITIVITY_FACTOR)
            ).coerceIn(0f, 1f)

            _scale.snapTo((1f - progress * SCALE_FRACTION).coerceIn(1f - SCALE_FRACTION, 1f))
            _alpha.snapTo(1f - progress)
        }
    }

    fun onDragEnd(containerSize: IntSize) {
        scope.launch {
            val shouldDismiss = abs(_offsetY.value) > containerSize.height / DISMISS_THRESHOLD_FACTOR ||
                abs(_offsetX.value) > containerSize.width / DISMISS_THRESHOLD_FACTOR

            if (shouldDismiss) {
                onDismiss()
            } else {
                launch { _offsetX.animateTo(0f) }
                launch { _offsetY.animateTo(0f) }
                launch { _scale.animateTo(1f) }
                launch { _alpha.animateTo(1f) }
            }
        }
    }
}

private fun calculateFittedSize(intrinsicSize: Size, maxWidth: Int, maxHeight: Int): IntSize? {
    val (width, height) = intrinsicSize.takeIf { it.width > 0 && it.height > 0 } ?: return null
    val scaleFactor = min(maxWidth.toFloat() / width, maxHeight.toFloat() / height)
    return IntSize((width * scaleFactor).roundToInt(), (height * scaleFactor).roundToInt())
}

@OptIn(ExperimentalSharedTransitionApi::class)
@Preview
@Composable
internal fun PhotoContentPreview() {
    SharedTransitionLayout {
        AnimatedVisibility(visible = true) {
            PhotoContent(
                component = PreviewPhotoComponent(),
                animatedVisibilityScope = this,
                modifier = Modifier.fillMaxSize(),
            )
        }
    }
}
