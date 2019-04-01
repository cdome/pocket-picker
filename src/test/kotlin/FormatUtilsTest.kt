import kotlin.test.BeforeTest
import kotlin.test.Test
import kotlin.test.assertEquals

class FormatUtilsTest {
    private var format = FormatUtils()

    @BeforeTest
    fun init() {
        format = FormatUtils()
    }

    @Test
    fun testFormatMinutes() {
        assertEquals("6 days, 22 hours and 40 minutes", format.formatMinutes(10000))
        assertEquals("1 day, 9 hours and 20 minutes", format.formatMinutes(2000))
        assertEquals("16 hours and 40 minutes", format.formatMinutes(1000))
        assertEquals("1 hour and 5 minutes", format.formatMinutes(65))
        assertEquals("15 minutes", format.formatMinutes(15))
        assertEquals("1 minute", format.formatMinutes(1))
    }

    @Test
    fun testFormatTimeStamp() {
        // PhantomJS format
        // val timestamp = format.formatTimeStamp("1553980233")
        //assertTrue(timestamp.startsWith("March 30, 2019 at "), "Starts with date")
        //assertTrue(timestamp.contains("10:33"), "Contains time part")
        assertEquals("N/A", format.formatTimeStamp("0"))
        assertEquals("N/A", format.formatTimeStamp(null))
    }

    @Test
    fun testShortenText() {
        val longText =
            "https://www.washingtonpost.com/business/economy/despite-pause-in-trade-war-us-and-chinas-economic-relationship-is-forever-changed/2018/12/02/ea79fb58-f666-11e8-8c9a-860ce2a8148f_story.html?wpisrc=nl_rainbow&wpmm=1"
        val shortenedText = "https://www.washingtonpost.com/bus...ory.html?wpisrc=nl_rainbow&wpmm=1"
        assertEquals("aaa", format.shortenText("aaa"))
        assertEquals(shortenedText, format.shortenText(longText))
    }
}