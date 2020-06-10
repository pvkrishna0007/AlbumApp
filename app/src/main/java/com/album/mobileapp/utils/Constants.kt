package com.album.mobileapp.utils

import android.view.View
import android.widget.ImageView
import com.album.mobileapp.R
import com.bumptech.glide.Glide

object Constants {
    const val API_KEY = "0449acff87718d36946c9f92eff9a358"
    const val PAGE_LIMIT = 10

    //Search results page starts from 1
    const val INITIAL_PAGE = 1

    const val ALBUM_IMAGE_URL = "album_image_url"
    const val ALBUM_NAME = "album_name"
    const val ALBUM_CONTENT_URL = "album_content_url"
    const val ALBUM_ARTIST = "album_artist"

}

fun ImageView.loadImage(imageUrl: String){
    Glide.with(context)
        .load(imageUrl)
        .placeholder(R.drawable.ic_place_holder)
        .error(R.drawable.ic_error)
        .into(this)
}

fun View.visibleGone(isVisible: Boolean){
    visibility = if(isVisible) View.VISIBLE else View.GONE
}

