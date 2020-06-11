package com.album.mobileapp.model

import com.fasterxml.jackson.annotation.JsonAlias
import com.fasterxml.jackson.annotation.JsonInclude
import com.fasterxml.jackson.annotation.JsonProperty
import com.fasterxml.jackson.annotation.JsonPropertyOrder

/**
 *  http://ws.audioscrobbler.com/2.0/?page=1&limit=1&method=album.search&album=believe&api_key=0449acff87718d36946c9f92eff9a358&format=json
 */
@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonPropertyOrder("results")
class AlbumModel {
    @JsonProperty("results")
    var results: Results? = null

    fun getAlbums(): ArrayList<Album>{
        return results?.albumMatches?.album?:ArrayList()
    }

    fun appendAlbums(albumModel: AlbumModel?): AlbumModel {
        if(albumModel != null)
            getAlbums().addAll(albumModel.getAlbums())
        return this
    }
}

@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonPropertyOrder("name", "artist", "url", "image", "streamable", "mbid")
class Album {
    @JsonProperty("name")
    var name: String? = null
    @JsonProperty("artist")
    var artist: String? = null
    @JsonProperty("url")
    var url: String? = null
    @JsonProperty("image")
    var image: List<Image>? = null
    @JsonProperty("streamable")
    var streamable: String? = null
    @JsonProperty("mbid")
    var mbid: String? = null

    fun getImageUrl(): String{
        return image?.get(1)?.text!!
    }

    fun getLargeImageUrl(): String{
        return image?.get(3)?.text!!
    }
}

@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonPropertyOrder("album")
class AlbumMatches {
    @JsonProperty("album")
    @JsonAlias("artist","track")
    var album: ArrayList<Album>? = null
}

@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonPropertyOrder("for")
class Attr {
    @JsonProperty("for")
    var _for: String? = null
}

@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonPropertyOrder("#text", "size")
class Image {
    @JsonProperty("#text")
    var text: String? = null
    @JsonProperty("size")
    var size: String? = null
}

@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonPropertyOrder("#text", "role", "searchTerms", "startPage")
class OpenSearchQuery {
    @JsonProperty("#text")
    var text: String? = null
    @JsonProperty("role")
    var role: String? = null
    @JsonProperty("searchTerms")
    var searchTerms: String? = null
    @JsonProperty("startPage")
    var startPage: String? = null
}

@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonPropertyOrder(
    "opensearch:Query",
    "opensearch:totalResults",
    "opensearch:startIndex",
    "opensearch:itemsPerPage",
    "albummatches",
    "@attr"
)
class Results {
    @JsonProperty("opensearch:Query")
    var openSearchQuery: OpenSearchQuery? = null
    @JsonProperty("opensearch:totalResults")
    var openSearchTotalResults: String? = null
    @JsonProperty("opensearch:startIndex")
    var openSearchStartIndex: String? = null
    @JsonProperty("opensearch:itemsPerPage")
    var openSearchItemsPerPage: String? = null
    @JsonProperty("albummatches")
    @JsonAlias("trackmatches","artistmatches")
    var albumMatches: AlbumMatches? = null
    @JsonProperty("@attr")
    var attr: Attr? = null
}