package nl.chimpgamer.simplegiveaway.paper.listeners

import nl.chimpgamer.simplegiveaway.paper.SimpleGiveawayPlugin
import org.bukkit.event.EventHandler
import org.bukkit.event.Listener
import org.bukkit.event.player.PlayerQuitEvent

class ConnectionListener(private val plugin: SimpleGiveawayPlugin) : Listener {

    @EventHandler
    fun onPlayerQuit(event: PlayerQuitEvent) {
        plugin.giveawayManager.leaveGiveaway(event.player)
    }
}