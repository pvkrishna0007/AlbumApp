package com.album.mobileapp.ui

import android.content.Intent
import android.graphics.Paint.UNDERLINE_TEXT_FLAG
import android.net.Uri
import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import com.album.mobileapp.R
import com.album.mobileapp.utils.Constants
import com.album.mobileapp.utils.loadImage
import kotlinx.android.synthetic.main.activity_detail.*


class DetailActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_detail)

        title = getString(R.string.detail_page_title)

        val extras = intent.extras
        if(extras != null){
            iv_album.loadImage(extras.getString(Constants.ALBUM_IMAGE_URL)!!)
            et_name.setText(extras.getString(Constants.ALBUM_NAME))
            et_artist.setText(extras.getString(Constants.ALBUM_ARTIST))
            val contentUrl = extras.getString(Constants.ALBUM_CONTENT_URL)

            tv_link.apply { paintFlags = paintFlags or UNDERLINE_TEXT_FLAG }
            tv_link.text = contentUrl

            tv_link.setOnClickListener {
                startActivity(Intent(Intent.ACTION_VIEW)
                    .apply { data = Uri.parse(contentUrl) })
            }
        }
    }
}
