package no.kristiania.android.programming.exam.data.gsontypes.locations.single

data class Image(
    val caption: String,
    val height: Long,
    val id: Long,
    val likes: List<Long>,
    val servingUrl: String,
    val uploadedByUserDisplayName: String,
    val uploadedByUserId: Long,
    val uploadedDate: Long,
    val width: Long
)