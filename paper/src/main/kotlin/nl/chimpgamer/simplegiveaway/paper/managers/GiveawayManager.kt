package nl.chimpgamer.simplegiveaway.paper.managers

import com.github.shynixn.mccoroutine.folia.globalRegionDispatcher
import kotlinx.coroutines.delay
import kotlinx.coroutines.withContext
import net.kyori.adventure.text.minimessage.tag.resolver.Formatter
import net.kyori.adventure.text.minimessage.tag.resolver.Placeholder
import net.kyori.adventure.text.minimessage.tag.resolver.TagResolver
import nl.chimpgamer.simplegiveaway.paper.SimpleGiveawayPlugin
import nl.chimpgamer.simplegiveaway.paper.extensions.parse
import nl.chimpgamer.simplegiveaway.paper.models.Giveaway
import org.bukkit.entity.Player
import kotlin.time.Duration.Companion.seconds

class GiveawayManager(private val plugin: SimpleGiveawayPlugin) {
    var giveaway: Giveaway? = null

    fun createGiveaway(creator: Player) {
        if (plugin.giveawayManager.giveaway != null) {
            creator.sendRichMessage(plugin.messagesConfig.giveawayAlreadyRunning)
            return
        }

        this.giveaway = Giveaway(creator.uniqueId)

        creator.sendRichMessage(plugin.messagesConfig.giveawayCreated)
        plugin.broadcast(plugin.messagesConfig.giveawayCreatedBroadcast.parse())
    }

    fun joinGiveaway(player: Player) {
        val giveaway = this.giveaway
        if (giveaway == null) {
            player.sendRichMessage(plugin.messagesConfig.giveawayNotFound)
            return
        }

        giveaway.addPlayer(player)
        player.sendRichMessage(plugin.messagesConfig.giveawayJoined)
        plugin.settingsConfig.giveawayJoinSound.play(player)
    }

    fun leaveGiveaway(player: Player) {
        val giveaway = this.giveaway
        if (giveaway == null) {
            player.sendRichMessage(plugin.messagesConfig.giveawayNotFound)
            return
        }

        giveaway.removePlayer(player)
        player.sendRichMessage(plugin.messagesConfig.giveawayLeft)
        plugin.settingsConfig.giveawayLeaveSound.play(player)
    }

    fun stopGiveaway(player: Player) {
        val giveaway = this.giveaway
        if (giveaway == null) {
            player.sendRichMessage(plugin.messagesConfig.giveawayNotFound)
            return
        }

        this.giveaway = null
        player.sendRichMessage(plugin.messagesConfig.giveawayStopped)
        plugin.broadcast(plugin.messagesConfig.giveawayStoppedBroadcast.parse())
    }

    suspend fun startGiveaway(player: Player) {
        val giveaway = this.giveaway
        if (giveaway == null) {
            player.sendRichMessage(plugin.messagesConfig.giveawayNotFound)
            return
        }

        if (giveaway.players().isEmpty()) {
            player.sendRichMessage(plugin.messagesConfig.giveawayStartNotEnoughParticipants)
            return
        }

        var winnerUUID = giveaway.players().random()
        var winner = plugin.server.getPlayer(winnerUUID)
        while (winner == null) {
            giveaway.removePlayer(winnerUUID)
            winnerUUID = giveaway.players().random()
            winner = plugin.server.getPlayer(winnerUUID)
        }

        plugin.broadcast(plugin.messagesConfig.giveawayStartCountdown.parse(
            TagResolver.resolver(
                Placeholder.parsed("countdown_number", "10"),
                Formatter.choice("countdown", 10)
            )
        ))
        val giveawayStartCountdownSound = plugin.settingsConfig.giveawayStartCountdownSound

        var i = 10
        while (i != 0) {
            delay(1.seconds)
            if (i <= 5) {
                plugin.broadcast(plugin.messagesConfig.giveawayStartCountdown.parse(
                    TagResolver.resolver(
                        Placeholder.parsed("countdown_number", i.toString()),
                        Formatter.choice("countdown", i)
                    )
                ))
                giveawayStartCountdownSound.play(player)
            }
            i--
        }
        delay(1.seconds)
        plugin.broadcast(plugin.messagesConfig.giveawayStartWinnerBroadcast.parse(mapOf("winner_name" to winner.name)))
        plugin.settingsConfig.giveawayStartWinnerAnnouncementSound.play(winner)
        val firework = plugin.settingsConfig.giveawayStartWinnerAnnouncementFirework
        withContext(plugin.globalRegionDispatcher) {
            firework.play(winner)
        }
        this.giveaway = null
    }

    fun showStats(player: Player) {
        val giveaway = this.giveaway
        if (giveaway == null) {
            player.sendRichMessage(plugin.messagesConfig.giveawayNotFound)
            return
        }

        player.sendMessage(plugin.messagesConfig.giveawayStats.parse(mapOf(
            "participants_count" to giveaway.players().count(),
            "online_players_count" to plugin.server.onlinePlayers.count(),
            "participants" to giveaway.players().mapNotNull { plugin.server.getPlayer(it)?.name }.joinToString(),
            "online_players" to plugin.server.onlinePlayers.joinToString { it.name }
        )))
    }
}