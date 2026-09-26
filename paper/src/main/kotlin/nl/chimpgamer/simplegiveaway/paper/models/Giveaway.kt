package nl.chimpgamer.simplegiveaway.paper.models

import net.kyori.adventure.text.minimessage.tag.resolver.TagResolver
import nl.chimpgamer.simplegiveaway.paper.SimpleGiveawayPlugin
import nl.chimpgamer.simplegiveaway.paper.extensions.parse
import org.bukkit.entity.Player
import java.time.LocalDateTime
import java.util.UUID

class Giveaway(
    val plugin: SimpleGiveawayPlugin,
    val creator: UUID,
    val prize: String? = null,
    private val players: MutableSet<UUID> = HashSet(),
    val createdDate: LocalDateTime = LocalDateTime.now()
) {

    fun addPlayer(player: Player) {
        players.add(player.uniqueId)
        val placeholders = mapOf(
            "player_name" to player.name,
            "participants_count" to this.players().count()
        )
        creator()?.sendMessage(plugin.messagesConfig.giveawayPlayerJoinActionbar.parse(placeholders))
    }

    fun removePlayer(player: Player) {
        players.remove(player.uniqueId)
        val placeholders = mapOf(
            "player_name" to player.name,
            "participants_count" to this.players().count()
        )
        creator()?.sendMessage(plugin.messagesConfig.giveawayPlayerLeaveActionbar.parse(placeholders))
    }

    fun forceRemovePlayer(playerUUID: UUID) = players.remove(playerUUID)

    fun players() = players.toSet()

    fun creator() = plugin.server.getPlayer(creator)
}