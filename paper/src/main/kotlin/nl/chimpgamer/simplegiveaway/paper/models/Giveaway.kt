package nl.chimpgamer.simplegiveaway.paper.models

import nl.chimpgamer.simplegiveaway.paper.SimpleGiveawayPlugin
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

    fun addPlayer(player: Player) = addPlayer(player.uniqueId)
    fun addPlayer(playerUUID: UUID) = players.add(playerUUID)

    fun removePlayer(playerUUID: UUID) = players.remove(playerUUID)
    fun removePlayer(player: Player) = removePlayer(player.uniqueId)

    fun players() = players.toSet()

    fun creator() = plugin.server.getPlayer(creator)
}