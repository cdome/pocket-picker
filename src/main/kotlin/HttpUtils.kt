import org.w3c.fetch.CORS
import org.w3c.fetch.Request
import org.w3c.fetch.RequestInit
import org.w3c.fetch.RequestMode
import kotlin.browser.window
import kotlin.js.Json

class HttpUtils {
    fun postString(payload: String, func: (String) -> Unit) {
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

    fun postJson(payload: String, func: (Json) -> Unit) {
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

}