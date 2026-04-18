package com.arkivanov.sample.shared.sharedtransitions

import com.arkivanov.decompose.ComponentContext
import com.arkivanov.decompose.ExperimentalDecomposeApi
import com.arkivanov.decompose.router.stack.ChildStack
import com.arkivanov.decompose.router.stack.StackNavigation
import com.arkivanov.decompose.router.stack.childStack
import com.arkivanov.decompose.router.stack.childStackWebNavigation
import com.arkivanov.decompose.router.stack.pop
import com.arkivanov.decompose.router.webhistory.WebNavigation
import com.arkivanov.decompose.value.Value
import com.arkivanov.sample.shared.ImageResourceId
import com.arkivanov.sample.shared.path
import com.arkivanov.sample.shared.sharedtransitions.SharedTransitionsComponent.Child
import com.arkivanov.sample.shared.sharedtransitions.SharedTransitionsComponent.Child.GalleryChild
import com.arkivanov.sample.shared.sharedtransitions.gallery.DefaultGalleryComponent
import kotlinx.serialization.Serializable

class DefaultSharedTransitionsComponent(
    componentContext: ComponentContext,
    private val onFinished: () -> Unit,
) : SharedTransitionsComponent, ComponentContext by componentContext {

    private val images =
        List(100) { index ->
            Image(
                id = index,
                resourceId = ImageResourceId.entries[index % ImageResourceId.entries.size],
            )
        }

    private val nav = StackNavigation<Config>()

    private val _stack =
        childStack(
            source = nav,
            serializer = Config.serializer(),
            initialStack = { listOf(Config.Gallery) },
            handleBackButton = true,
            childFactory = ::child,
        )

    override val stack: Value<ChildStack<*, Child>> = _stack

    @OptIn(ExperimentalDecomposeApi::class)
    override val webNavigation: WebNavigation<*> =
        childStackWebNavigation(
            navigator = nav,
            stack = _stack,
            serializer = Config.serializer(),
            pathMapper = { it.configuration.path() },
        )

    private fun child(config: Config, ctx: ComponentContext): Child =
        when (config) {
            is Config.Gallery ->
                GalleryChild(
                    DefaultGalleryComponent(
                        componentContext = ctx,
                        images = images,
                        onFinished = onFinished,
                    )
                )
        }

    override fun onBack() {
        nav.pop()
    }

    @Serializable
    private sealed interface Config {
        @Serializable
        data object Gallery : Config
    }
}
