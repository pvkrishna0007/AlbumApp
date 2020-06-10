package com.album.mobileapp.ui

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import com.album.mobileapp.R
import com.album.mobileapp.model.Album
import com.album.mobileapp.utils.Constants
import com.album.mobileapp.utils.loadImage

class AlbumAdapter(context: Context) : RecyclerView.Adapter<AlbumAdapter.Holder>() {

    private val inflater: LayoutInflater = LayoutInflater.from(context)
    private val albumList = ArrayList<Album>()

    fun setAlbumList(albums: List<Album>) {
        albumList.clear()
        albumList.addAll(albums)
        notifyDataSetChanged()
    }

    override fun onCreateViewHolder(viewGroup: ViewGroup, i: Int): Holder {

        val view = inflater.inflate(R.layout.row_album_item, viewGroup, false)
        return Holder(view)
    }

    override fun onBindViewHolder(holder: Holder, i: Int) {

        val album: Album = albumList[i]
        holder.tvName.text = album.name
        holder.ivName.loadImage(album.getImageUrl())

        holder.itemView.setOnClickListener {
            launchDetailActivity(inflater.context, album)
        }
    }

    override fun getItemCount(): Int {
        return albumList.size
    }

    class Holder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val tvName: TextView = itemView.findViewById(R.id.tv_item)
        val ivName: ImageView = itemView.findViewById(R.id.iv_item)
    }

    private fun launchDetailActivity(context: Context, album: Album) {
        val bundle = Bundle().apply {
            putString(Constants.ALBUM_NAME, album.name)
            putString(Constants.ALBUM_ARTIST, album.artist)
            putString(Constants.ALBUM_CONTENT_URL, album.url)
            putString(Constants.ALBUM_IMAGE_URL, album.getLargeImageUrl())
        }
        context.startActivity(Intent(context, DetailActivity::class.java).apply { putExtras(bundle) })
    }
}
