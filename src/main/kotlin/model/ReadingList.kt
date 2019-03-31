package model

external interface ReadingList {
    val status: String?
    val list: Map<String, ListItem>
}
