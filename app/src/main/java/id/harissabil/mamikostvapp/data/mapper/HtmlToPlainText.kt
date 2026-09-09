package id.harissabil.mamikostvapp.data.mapper

import org.jsoup.Jsoup
import org.jsoup.nodes.Document
import org.jsoup.parser.Parser
import org.jsoup.safety.Safelist

private const val PARAGRAPH_BREAK = "\n\n"
private const val LINE_BREAK = "\n"

internal fun htmlToPlainText(html: String): String {
    val document = Jsoup.parse(html)
    document.outputSettings(Document.OutputSettings().prettyPrint(false))
    document.select("br").before(LINE_BREAK)
    document.select("p").before(PARAGRAPH_BREAK)
    val strippedOfTags = Jsoup.clean(
        document.html(),
        "",
        Safelist.none(),
        Document.OutputSettings().prettyPrint(false),
    )
    return Parser.unescapeEntities(strippedOfTags, false).trim()
}
