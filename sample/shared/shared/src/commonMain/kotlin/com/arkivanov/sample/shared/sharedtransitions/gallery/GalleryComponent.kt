package com.arkivanov.sample.shared.sharedtransitions.gallery

import com.arkivanov.decompose.ExperimentalDecomposeApi
import com.arkivanov.decompose.router.items.LazyChildItems
import com.arkivanov.decompose.router.slot.ChildSlot
import com.arkivanov.decompose.value.Value
import com.arkivanov.sample.shared.sharedtransitions.Image
import com.arkivanov.sample.shared.sharedtransitions.gallery.DefaultGalleryComponent.Photo
import com.arkivanov.sample.shared.sharedtransitions.photo.PhotoComponent
import com.arkivanov.sample.shared.sharedtransitions.thumbnail.ThumbnailComponent

interface GalleryComponent {

    @OptIn(ExperimentalDecomposeApi::class)
    val items: LazyChildItems<Image, ThumbnailComponent>

    val photoSlot: Value<ChildSlot<Photo, PhotoComponent>>

    fun onCloseClicked()
}
