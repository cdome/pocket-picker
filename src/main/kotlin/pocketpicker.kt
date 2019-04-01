import model.ListItem
import model.ReadingList
import support.Object
import kotlin.browser.document
import kotlin.browser.localStorage
import kotlin.browser.window
import kotlin.js.Date

const val proxyUrl = "https://us-central1-hazel-proxy-235401.cloudfunctions.net/PocketPicker"
const val authUrl = "https://getpocket.com/auth/authorize"

const val reqTokenStore = "requestToken"
const val sessTokenStore = "sessionToken"
const val userNameStore = "userName"
external val redirectUrl: String

val format = FormatUtils()
val http = HttpUtils()

fun main() {
    if (document.URL.contains("authDone")) {
        auth()
    }
    if (localStorage.getItem(userNameStore) != null) {
        updateLoggedInUser()
        updateUi()
    }
}

private fun updateLoggedInUser() {
    document.getElementById("login")?.innerHTML = "Welcome <strong>${localStorage.getItem("userName")}</strong>..."
}

fun login() {
    http.postJson("""{"action":"key", "redirectUrl":"$redirectUrl"}""") { json ->
        val requestToken = json["code"] as String
        localStorage.setItem(reqTokenStore, requestToken)
        window.location.replace("$authUrl?request_token=$requestToken&redirect_uri=$redirectUrl")
    }
}

private fun auth() {
    val requestToken = localStorage.getItem(reqTokenStore) ?: ""
    http.postJson("""{"code":"$requestToken", "action":"auth"}""") { json ->
        val sessionToken = json["access_token"] as String
        val username = json["username"] as String
        localStorage.setItem(sessTokenStore, sessionToken)
        localStorage.setItem(userNameStore, username)
        updateLoggedInUser()
        updateUi()
    }
}

private fun updateUi() {
    document.getElementById("unread")?.innerHTML = "<img width=50 height=50 src='img/spinner.gif'/>"
    http.postString("""{"action":"get", "token":"${localStorage.getItem(sessTokenStore)}"}""") { body ->
        val values = Object.values(JSON.parse<ReadingList>(body).list)
        val archived = ArrayList<ListItem>()
        val unread = ArrayList<ListItem>()
        val starred = ArrayList<ListItem>()
        values.forEach { item ->
            if (item.status == "0") unread.add(item)
            if (item.status == "1") archived.add(item)
            if (item.favorite == "1") starred.add(item)
        }
        updateActivitySince(values)
        findDuplicates(values)
        document.getElementById("unread")?.innerHTML =
            """
        <table class="table table-bordered">
            <tr class="table-primary">
                <th>Total items</th>
                <th>Unread items</th>
                <th>Read items</th>
                <th>Starred items</th>
                <th>Words to read</th>
                <th>Remaining reading time</th>
            </tr>
            <tr>
                <td>${values.size}</td>
                <td>${unread.size}</td>
                <td>${archived.size}</td>
                <td>${starred.size}</td>
                <td>${unread.map { item -> item.word_count?.toInt() ?: 0 }.sum()}</td>
                <td>${format.formatMinutes(unread.map { item -> item.time_to_read ?: 0 }.sum())}</td>
            </tr>
        </table>
            """
    }
}

private fun findDuplicates(values: Array<ListItem>) {
    val sortedItems = HashMap<String, ArrayList<ListItem>>()
    values.forEach { item -> sortedItems.getOrPut(item.resolved_id) { ArrayList() }.add(item) }
    val duplicateItems = sortedItems.filter { item -> item.key != "0" && item.value.size > 1 }
    if (duplicateItems.isNotEmpty()) {
        val table = StringBuilder("<div>You have ${duplicateItems.size} duplicate items on your list:</div>")
        table.append(
            """
        <table class="table table-bordered">
            <tr class="table-primary">
                <th>Title</th>
                <th>URL</th>
                <th>Added</th>
                <th>Read</th>
                <th>Actions</th>
            </tr>
        """
        )

        duplicateItems.values.forEach { items ->
            items.forEach { item ->
                table.append(
                    """
                <tr>
                    <td>${if (item.given_title != "") item.given_title else item.resolved_title}</td>
                    <td>${format.shortenText(item.given_url)}</td>
                    <td>${format.formatTimeStamp(item.time_added)}</td>
                    <td>${format.formatTimeStamp(item.time_read)}</td>
                    <td><a href="https://app.getpocket.com/read/${item.item_id}" class="btn btn-primary">Open in Pocket</a></td>
                </tr>
            """
                )
            }
        }

        table.append("</table>")

        document.getElementById("duplicates")?.innerHTML = table.toString()
    } else {
        document.getElementById("duplicates")?.innerHTML = "Congrats! You have no duplicate items on your list."
    }
}

private fun updateActivitySince(values: Array<ListItem>) {
    val firstItem =
        values.filter { item -> item.time_added != "0" }.map { item -> item.time_added.toLong() }.min()
    firstItem?.let {
        document.getElementById("since")?.innerHTML =
            "You are active on Pocket since ${Date(firstItem * 1000L).toDateString()}"
    }
}