package nl.chimpgamer.simplegiveaway.paper.models

import org.bukkit.entity.Player
import java.time.LocalDateTime
import java.util.UUID

class Giveaway(
    val creator: UUID,
    private val players: MutableSet<UUID> = HashSet(),
    val createdDate: LocalDateTime = LocalDateTime.now()
) {

    fun addPlayer(player: Player) = addPlayer(player.uniqueId)
    fun addPlayer(playerUUID: UUID) {
        players.add(playerUUID)
    }

    fun removePlayer(player: Player) = removePlayer(player.uniqueId)
    fun removePlayer(playerUUID: UUID) {
        players.remove(playerUUID)
    }

    fun players() = players.toSet()
}