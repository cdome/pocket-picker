package support

@JsName("Object")
external object Object {
    fun keys(map: Map<String, *>): Array<String>
    fun <T> values(map: Map<String, T>): Array<T>
    fun <T> entries(map: Map<String, T>): Map<String, T>
}