import org.w3c.fetch.CORS
import org.w3c.fetch.Request
import org.w3c.fetch.RequestInit
import org.w3c.fetch.RequestMode
import kotlin.browser.document
import kotlin.browser.localStorage
import kotlin.browser.window
import kotlin.js.Json

const val proxyUrl = "https://us-central1-hazel-proxy-235401.cloudfunctions.net/PocketPicker"
const val authUrl = "https://getpocket.com/auth/authorize"

const val reqTokenStore = "requestToken"
const val sessTokenStore = "sessionToken"
const val userNameStore = "userName"
external val redirectUrl: String

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
    document.getElementById("login")?.innerHTML = "Logged in as ${localStorage.getItem("userName")}..."
}

fun login() {
    postJson("""{"action":"key", "redirectUrl":"$redirectUrl"}""") { json ->
        val requestToken = json["code"] as String
        localStorage.setItem(reqTokenStore, requestToken)
        window.location.replace("$authUrl?request_token=$requestToken&redirect_uri=$redirectUrl")
    }
}

private fun auth() {
    val requestToken = localStorage.getItem(reqTokenStore) ?: ""
    postJson("""{"code":"$requestToken", "action":"auth"}""") { json ->
        val sessionToken = json["access_token"] as String
        val username = json["username"] as String
        localStorage.setItem(sessTokenStore, sessionToken)
        localStorage.setItem(userNameStore, username)
        updateLoggedInUser()
        updateUi()
    }
}

fun updateUi() {
    document.getElementById("unread")?.innerHTML = "<img width=50 height=50 src='img/spinner.gif'/>"
    postString("""{"action":"get", "token":"${localStorage.getItem(sessTokenStore)}"}""") { body ->
        val values = Object.values(JSON.parse<ReadingList>(body).list)
        val archived = ArrayList<ListItem>()
        val unread = ArrayList<ListItem>()
        val starred = ArrayList<ListItem>()
        values.forEach { item ->
            if (item.status == "0") unread.add(item)
            if (item.status == "1") archived.add(item)
            if (item.favorite == "1") starred.add(item)
        }
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
                <td>${formatMinutes(unread.map { item -> item.time_to_read ?: 0 }.sum())}</td>
            </tr>
        </table>
            """
    }
}

private fun formatMinutes(totalMinutes: Int): String {
    val days = totalMinutes / 24 / 60
    val remainingMinutes = totalMinutes - days * 24 * 60
    val hours = remainingMinutes / 60
    val minutes = (remainingMinutes - hours * 60)
    return "${if (days > 0) "$days days, " else ""} ${if (hours > 0) "$hours hours and " else ""}$minutes minutes"
}

private fun postJson(payload: String, func: (Json) -> Unit) {
    val req = Request(
        proxyUrl, RequestInit(
            "POST",
            body = payload,
            mode = RequestMode.CORS
        )
    )

    window.fetch(req)
        .then { response ->
            if (response.ok) {
                response.json().then { blob ->
                    func(blob as Json)
                }
            }
        }
}

private fun postString(payload: String, func: (String) -> Unit) {
    val req = Request(
        proxyUrl, RequestInit(
            "POST",
            body = payload,
            mode = RequestMode.CORS
        )
    )

    window.fetch(req)
        .then { response ->
            if (response.ok) {
                response.text().then { blob ->
                    func(blob)
                }
            }
        }
}

external interface ReadingList {
    val status: String?
    val list: Map<String, ListItem>
}

external interface ListItem {
    val status: String
    val favorite: String
    val time_added: String
    val time_read: String?
    val time_favorited: String?
    val time_to_read: Int?
    val word_count: String?
}

external object Object {
    fun keys(map: Map<String, *>): Array<String>
    fun <T> values(map: Map<String, T>): Array<T>
    fun <T> entries(map: Map<String, T>): Map<String, T>
}