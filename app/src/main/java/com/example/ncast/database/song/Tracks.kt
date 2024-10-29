data class Tracks(
    val items: List<Item>,
) {
    data class Item(
        val album: Album,
        val id: String,
        val name: String,
        val preview_url: String?,
        val artists: List<Artist>,
    )

    data class Album(
        val images: List<Image>
    )

    data class Artist(
        val name: String,
    )

    data class Image(
        val url: String
    )
}
