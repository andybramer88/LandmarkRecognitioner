package com.example.landmarkrecognitioner

data class WikipediaSummary(
    val title: String,
    val displaytitle: String,
    val extract: String,
    val thumbnail: Thumbnail? = null
)

data class Thumbnail(
    val source: String,
    val width: Int,
    val height: Int
)
