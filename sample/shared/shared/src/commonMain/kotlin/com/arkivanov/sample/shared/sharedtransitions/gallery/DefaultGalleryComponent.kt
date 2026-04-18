package com.arkivanov.sample.shared.sharedtransitions.gallery

import com.arkivanov.decompose.ComponentContext
import com.arkivanov.decompose.ExperimentalDecomposeApi
import com.arkivanov.decompose.router.children.transientNavStateSaver
import com.arkivanov.decompose.router.items.Items
import com.arkivanov.decompose.router.items.ItemsNavigation
import com.arkivanov.decompose.router.items.LazyChildItems
import com.arkivanov.decompose.router.items.childItems
import com.arkivanov.decompose.router.slot.ChildSlot
import com.arkivanov.decompose.router.slot.SlotNavigation
import com.arkivanov.decompose.router.slot.activate
import com.arkivanov.decompose.router.slot.childSlot
import com.arkivanov.decompose.router.slot.dismiss
import com.arkivanov.decompose.value.Value
import com.arkivanov.sample.shared.sharedtransitions.Image
import com.arkivanov.sample.shared.sharedtransitions.photo.DefaultPhotoComponent
import com.arkivanov.sample.shared.sharedtransitions.photo.PhotoComponent
import com.arkivanov.sample.shared.sharedtransitions.thumbnail.DefaultThumbnailComponent
import com.arkivanov.sample.shared.sharedtransitions.thumbnail.ThumbnailComponent
import kotlinx.serialization.Serializable

@OptIn(ExperimentalDecomposeApi::class)
class DefaultGalleryComponent(
    componentContext: ComponentContext,
    private val images: List<Image>,
    private val onFinished: () -> Unit,
) : GalleryComponent, ComponentContext by componentContext {

    private val nav = ItemsNavigation<Image>()

    override val items: LazyChildItems<Image, ThumbnailComponent> =
        childItems(
            source = nav,
            stateSaver = transientNavStateSaver(),
            initialItems = { Items(items = images) },
            childFactory = ::child,
        )

    private val photoSlotNav = SlotNavigation<Photo>()

    override val photoSlot: Value<ChildSlot<Photo, PhotoComponent>> =
        childSlot(
            source = photoSlotNav,
            serializer = Photo.serializer(),
            handleBackButton = true,
            childFactory = { config, _ ->
                DefaultPhotoComponent(
                    image = images.first { it.id == config.id },
                    onFinished = photoSlotNav::dismiss,
                )
            },
        )

    private fun child(image: Image, ctx: ComponentContext): ThumbnailComponent =
        DefaultThumbnailComponent(
            componentContext = ctx,
            image = image,
            onSelected = { photoSlotNav.activate(Photo(id = image.id)) },
        )

    override fun onCloseClicked() {
        onFinished()
    }

    @Serializable
    data class Photo(val id: Int)
}
