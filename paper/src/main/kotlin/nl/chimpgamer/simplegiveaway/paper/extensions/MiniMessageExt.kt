package nl.chimpgamer.simplegiveaway.paper.extensions

import net.kyori.adventure.text.ComponentLike
import net.kyori.adventure.text.format.TextDecoration
import net.kyori.adventure.text.minimessage.MiniMessage.miniMessage
import net.kyori.adventure.text.minimessage.tag.resolver.Placeholder
import net.kyori.adventure.text.minimessage.tag.resolver.TagResolver

val newlineSplitRegex = "(<br>|<newline>)".toRegex()
private val miniMessageTagRegex = Regex("<[!?#]?[a-z0-9_-]*>")

fun String.parse() = miniMessage().deserialize(this).decorationIfAbsent(TextDecoration.ITALIC, TextDecoration.State.FALSE)

fun String.parse(tagResolver: TagResolver) = miniMessage().deserialize(this, tagResolver).decorationIfAbsent(TextDecoration.ITALIC, TextDecoration.State.FALSE)
fun String.parse(vararg tagResolvers: TagResolver) = parse(TagResolver.resolver(*tagResolvers))

fun String.parse(replacements: Map<String, *>) = parse(replacements.toTagResolver())

fun String.isValid(): Boolean = miniMessage().deserializeOrNull(this) != null

fun String.containsMiniMessage(): Boolean = miniMessageTagRegex.containsMatchIn(this)

fun Map<String, *>.toTagResolver(parsed: Boolean = false) = TagResolver.resolver(
    map { (key, value) ->
        if (value is ComponentLike) Placeholder.component(key, value)
        else if (parsed) Placeholder.parsed(key, value.toString())
        else Placeholder.unparsed(key, value.toString())
    }
)