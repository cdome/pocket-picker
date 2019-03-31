import kotlin.test.Test
import kotlin.test.assertEquals

class PocketPickerTest {

    @Test
    fun testFormatMinutes() {
        assertEquals("10 days, 22 hours and 40 minutes", formatMinutes(10000))
        assertEquals("1 day, 9 hours and 20 minutes", formatMinutes(2000))
        assertEquals("16 hours and 40 minutes", formatMinutes(1000))
        assertEquals("1 hour and 5 minutes", formatMinutes(65))
        assertEquals("15 minutes", formatMinutes(15))
        assertEquals("1 minutes", formatMinutes(1))
    }
}