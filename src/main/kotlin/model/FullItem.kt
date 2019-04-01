package model

import utils.FormatUtils

private val format = FormatUtils()

private val allItemsSchema = arrayOf(
    TableSchema("ID", "item_id", true),
    TableSchema("Title", "title", false, "<a href='{{url}}'>{{title}}</a>"),
    TableSchema("Url", "url", true),
    TableSchema("Source", "source"),
    TableSchema("Added", "dateAdded", false),
    TableSchema("Archived", "dateRead", false),
    TableSchema("Favorite", "favorite", false, "<img src='images/{{favorite}}-star.png' alt='{{favorite}}'/>"),
    TableSchema("Word Count", "wordCount"),
    TableSchema("Minutes to read", "readingTime"),
    TableSchema(
        "Actions",
        "actions",
        false,
        "<a href='https://app.getpocket.com/read/{{item_id}}' class='btn btn-primary' target='_blank'>Open in Pocket</a>"
    )
)

class FullItem(
    val item_id: String,
    val title: String,
    val url: String,
    val source: String,
    val dateAdded: String,
    val dateRead: String,
    val favorite: String,
    val wordCount: Int,
    val readingTime: Int,
    val actions: String?
)

fun getFullItemsTableData(items: List<ListItem>): TableData {
    return TableData(items.map { item -> mapItem(item) }.toTypedArray(), allItemsSchema)
}

private fun mapItem(item: ListItem): FullItem = FullItem(
    item.item_id,
    if (item.given_title != "") item.given_title else "[[no title]]",
    item.given_url,
    format.findSource(item.resolved_url, item.given_url),
    format.formatDate(item.time_added),
    format.formatDate(item.time_read),
    if (item.favorite == "0") "no" else "yes",
    item.word_count?.toInt() ?: 0,
    item.time_to_read ?: 0,
    null
)
