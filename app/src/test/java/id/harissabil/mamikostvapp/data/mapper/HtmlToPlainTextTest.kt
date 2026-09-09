package id.harissabil.mamikostvapp.data.mapper

import org.junit.Assert.assertEquals
import org.junit.Test

class HtmlToPlainTextTest {

    @Test
    fun `separates paragraphs with a blank line`() {
        val html = "<p>First paragraph.</p><p>Second paragraph.</p>"
        assertEquals("First paragraph.\n\nSecond paragraph.", htmlToPlainText(html))
    }

    @Test
    fun `converts br to a single newline`() {
        assertEquals("Line one\nLine two", htmlToPlainText("Line one<br>Line two"))
    }

    @Test
    fun `strips inline tags and keeps their text`() {
        assertEquals(
            "Under the Dome is a show",
            htmlToPlainText("<b>Under the Dome</b> is a <i>show</i>"),
        )
    }

    @Test
    fun `decodes html entities`() {
        assertEquals("Tom & Jerry", htmlToPlainText("<p>Tom &amp; Jerry</p>"))
    }

    @Test
    fun `trims surrounding whitespace including nbsp`() {
        assertEquals("Body", htmlToPlainText("<p>Body&nbsp;</p>"))
    }

    @Test
    fun `leaves plain text unchanged`() {
        assertEquals("Just text", htmlToPlainText("Just text"))
    }
}
