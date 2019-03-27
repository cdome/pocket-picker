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
        val list = JSON.parse<ReadingList>(body)
        val keys = Object.keys(list.list)
        document.getElementById("unread")?.innerHTML = "You have ${keys.size} unread items."
    }
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

external interface ListItem

external object Object {
    fun keys(map: Map<String, *>): Array<String>
}