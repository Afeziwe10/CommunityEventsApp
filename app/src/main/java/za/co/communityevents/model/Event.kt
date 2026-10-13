package za.co.communityevents.model

data class Event(
    val id: String = "",
    val title: String = "",
    val date: String = "", // YYYY-MM-DD
    val location: String = "",
    val description: String = "",
    val imageUrl: String = ""
)
