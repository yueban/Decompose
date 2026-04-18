package com.arkivanov.sample.shared.sharedtransitions.gallery

import com.arkivanov.decompose.router.items.LazyChildItems
import com.arkivanov.decompose.router.slot.ChildSlot
import com.arkivanov.decompose.value.MutableValue
import com.arkivanov.decompose.value.Value
import com.arkivanov.sample.shared.ImageResourceId
import com.arkivanov.sample.shared.SimpleLazyChildItems
import com.arkivanov.sample.shared.sharedtransitions.Image
import com.arkivanov.sample.shared.sharedtransitions.gallery.DefaultGalleryComponent.Photo
import com.arkivanov.sample.shared.sharedtransitions.photo.PhotoComponent
import com.arkivanov.sample.shared.sharedtransitions.thumbnail.PreviewThumbnailComponent
import com.arkivanov.sample.shared.sharedtransitions.thumbnail.ThumbnailComponent

class PreviewGalleryComponent : GalleryComponent {

    override val items: LazyChildItems<Image, ThumbnailComponent> =
        SimpleLazyChildItems(
            List(10) { index ->
                PreviewThumbnailComponent(
                    image = Image(
                        id = index,
                        resourceId = ImageResourceId.entries[index % ImageResourceId.entries.size],
                    ),
                )
            }.associateBy { it.image },
        )
    override val photoSlot: Value<ChildSlot<Photo, PhotoComponent>> = MutableValue(ChildSlot())

    override fun onCloseClicked() {}
}
