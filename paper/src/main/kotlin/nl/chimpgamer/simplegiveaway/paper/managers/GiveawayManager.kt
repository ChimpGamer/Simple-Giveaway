package nl.chimpgamer.simplegiveaway.paper.managers

import com.github.shynixn.mccoroutine.folia.globalRegionDispatcher
import kotlinx.coroutines.delay
import kotlinx.coroutines.withContext
import net.kyori.adventure.text.minimessage.tag.resolver.Formatter
import net.kyori.adventure.text.minimessage.tag.resolver.Placeholder.parsed
import net.kyori.adventure.text.minimessage.tag.resolver.TagResolver
import nl.chimpgamer.simplegiveaway.paper.SimpleGiveawayPlugin
import nl.chimpgamer.simplegiveaway.paper.extensions.parse
import nl.chimpgamer.simplegiveaway.paper.extensions.toTagResolver
import nl.chimpgamer.simplegiveaway.paper.models.Giveaway
import org.bukkit.entity.Player
import kotlin.time.Duration.Companion.seconds

class GiveawayManager(private val plugin: SimpleGiveawayPlugin) {
    companion object {
        private const val INITIAL_COUNTDOWN = 10
        private const val COUNTDOWN_SOUND_THRESHOLD = 5
    }

    val settingsConfig get() = plugin.settingsConfig
    val messagesConfig get() = plugin.messagesConfig
    var giveaway: Giveaway? = null

    fun createGiveaway(creator: Player, prize: String? = null) {
        if (giveaway != null) {
            creator.sendRichMessage(messagesConfig.giveawayAlreadyRunning)
            return
        }

        val cleanedPrize = prize?.trim()?.takeIf { it.isNotEmpty() }
        giveaway = Giveaway(creator.uniqueId, cleanedPrize)

        if (cleanedPrize == null) {
            plugin.broadcast(messagesConfig.giveawayCreatedBroadcast.parse())
        } else {
            plugin.broadcast(
                messagesConfig.giveawayCreatedBroadcastWithPrize
                    .parse(parsed("prize", cleanedPrize))
            )
        }
    }

    fun joinGiveaway(player: Player) {
        executeWithGiveaway(player) { giveaway ->
            giveaway.addPlayer(player)
            player.sendRichMessage(messagesConfig.giveawayJoined)
            settingsConfig.giveawayJoinSound.play(player)
        }
    }

    fun leaveGiveaway(player: Player) {
        executeWithGiveaway(player) { giveaway ->
            giveaway.removePlayer(player)
            player.sendRichMessage(messagesConfig.giveawayLeft)
            settingsConfig.giveawayLeaveSound.play(player)
        }
    }

    fun stopGiveaway(player: Player) {
        executeWithGiveaway(player) { giveaway ->
            this.giveaway = null
            player.sendRichMessage(messagesConfig.giveawayStopped)
            plugin.broadcast(messagesConfig.giveawayStoppedBroadcast.parse())
        }
    }

    suspend fun startGiveaway(player: Player) {
        executeWithGiveaway(player) { giveaway ->
            if (giveaway.players().isEmpty()) {
                player.sendRichMessage(messagesConfig.giveawayStartNotEnoughParticipants)
                return
            }

            var winnerUUID = giveaway.players().random()
            var winner = plugin.server.getPlayer(winnerUUID)
            while (winner == null) {
                giveaway.removePlayer(winnerUUID)
                winnerUUID = giveaway.players().random()
                winner = plugin.server.getPlayer(winnerUUID)
            }

            plugin.broadcast(
                messagesConfig.giveawayStartCountdown.parse(
                    parsed("countdown_number", "$INITIAL_COUNTDOWN"),
                    Formatter.choice("countdown", INITIAL_COUNTDOWN)
                )
            )
            val giveawayStartCountdownSound = settingsConfig.giveawayStartCountdownSound

            for (i in INITIAL_COUNTDOWN downTo 1) {
                delay(1.seconds)
                if (i <= COUNTDOWN_SOUND_THRESHOLD) {
                    plugin.broadcast(
                        messagesConfig.giveawayStartCountdown.parse(
                            parsed("countdown_number", i.toString()),
                            Formatter.choice("countdown", i)
                        )
                    )
                    giveawayStartCountdownSound.play(plugin.server.onlinePlayers.first())
                }
            }

            plugin.broadcast(messagesConfig.giveawayStartWinnerBroadcast.parse(parsed("winner_name", winner.name)))
            settingsConfig.giveawayStartWinnerAnnouncementSound.play(winner)
            val firework = settingsConfig.giveawayStartWinnerAnnouncementFirework
            withContext(plugin.globalRegionDispatcher) {
                firework.play(winner)
            }
            this.giveaway = null
        }
    }

    fun showStats(player: Player) {
        executeWithGiveaway(player) { giveaway ->
            val tagResolver = TagResolver.resolver(
                mapOf(
                    "creator_name" to plugin.server.getOfflinePlayer(giveaway.creator).name,
                    "creator_uuid" to giveaway.creator.toString(),
                    "participants_count" to giveaway.players().count(),
                    "online_players_count" to plugin.server.onlinePlayers.count(),
                    "participants" to giveaway.players().mapNotNull { plugin.server.getPlayer(it)?.name }
                        .joinToString(),
                    "online_players" to plugin.server.onlinePlayers.joinToString { it.name },
                ).toTagResolver(true),
                Formatter.date("created_at", giveaway.createdDate)
            )
            player.sendMessage(messagesConfig.giveawayStats.parse(tagResolver))
        }
    }

    private inline fun executeWithGiveaway(player: Player, action: (Giveaway) -> Unit) {
        val currentGiveaway = giveaway
        if (currentGiveaway == null) {
            player.sendRichMessage(messagesConfig.giveawayNotFound)
            return
        }
        action(currentGiveaway)
    }
}