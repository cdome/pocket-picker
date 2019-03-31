package model

external interface ListItem {
    val item_id: String
    val resolved_id: String
    val status: String
    val favorite: String
    val time_added: String
    val time_read: String?
    val time_favorited: String?
    val time_to_read: Int?
    val word_count: String?
    val given_url: String
    val given_title: String
}