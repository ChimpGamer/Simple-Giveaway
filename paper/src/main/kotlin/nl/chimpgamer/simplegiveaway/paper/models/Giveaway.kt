package nl.chimpgamer.simplegiveaway.paper.models

import org.bukkit.entity.Player
import net.kyori.adventure.text.minimessage.tag.resolver.Formatter
import net.kyori.adventure.text.minimessage.tag.resolver.Placeholder.parsed
import net.kyori.adventure.text.minimessage.tag.resolver.TagResolver
import java.time.LocalDateTime
import java.util.UUID

class Giveaway(
    val creator: UUID,
    val prize: String? = null,
    private val players: MutableSet<UUID> = HashSet(),
    val createdDate: LocalDateTime = LocalDateTime.now()
) {

    fun addPlayer(player: Player) = addPlayer(player.uniqueId)
    fun addPlayer(playerUUID: UUID) {
        players.add(playerUUID)
        var joinedPlayer = plugin.server.getPlayer(playerUUID)
        val tagResolver = Tagresolver.resolver(
            mapOf(
                "player_name" to joinedPlayer.name,
                "participants_count" to this.players().count()
            )
        )
        creator.sendRichMessage(messagesConfig.giveawayPlayerJoinActionbar.parse(tagResolver))
    }

    fun removePlayer(player: Player) = removePlayer(player.uniqueId)
    fun removePlayer(playerUUID: UUID) {
        players.remove(playerUUID)
        var removedPlayer = plugin.server.getPlayer(playerUUID)
        val tagResolver = Tagresolver.resolver(
            mapOf(
                "player_name" to removedPlayer.name,
                "participants_count" to this.players().count()
            )
        )
        creator.sendRichMessage(messagesConfig.giveawayPlayerLeaveActionbar.parse(tagResolver))
    }

    fun players() = players.toSet()
}