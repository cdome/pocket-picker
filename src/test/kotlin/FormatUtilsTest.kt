import kotlin.test.Test
import kotlin.test.assertEquals

class FormatUtilsTest {
    private var format = FormatUtils()

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
        assertEquals("March 30, 2019 at 10:40:33 PM MDT", format.formatTimeStamp("1554007233"))
        assertEquals("N/A", format.formatTimeStamp("0"))
        assertEquals("N/A", format.formatTimeStamp(null))
    }
}