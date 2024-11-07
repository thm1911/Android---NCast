import com.example.ncast.model.newAlbumRelease.NewAlbumRelease

data class Tracks(
    val items: List<Item>,
) {
    data class Item(
        val album: Album,
        val id: String,
        val name: String,
        val artists: List<NewAlbumRelease.Item.Artist>,
    )

    data class Album(
        val images: List<NewAlbumRelease.Item.Image>
    )
}
