package nl.chimpgamer.simplegiveaway.paper.commands

import nl.chimpgamer.simplegiveaway.paper.SimpleGiveawayPlugin
import org.bukkit.command.CommandSender
import org.bukkit.entity.Player
import org.incendo.cloud.CommandManager
import org.incendo.cloud.parser.standard.StringParser.greedyStringParser
import org.incendo.cloud.kotlin.coroutines.extension.suspendingHandler
import kotlin.jvm.optionals.getOrNull

class GiveawayCommand(private val plugin: SimpleGiveawayPlugin) {

    fun registerCommands(commandManager: CommandManager<CommandSender>, name: String, vararg aliases: String) {
        val basePermission = "simplegiveaway.command.giveaway"

        val builder = commandManager.commandBuilder(name, *aliases)
            .permission(basePermission)

        commandManager.command(builder
            .senderType(Player::class.java)
            .literal("create")
            .permission("$basePermission.create")
            .required("prize", greedyStringParser())
            .handler { context ->
                val sender = context.sender()
                val prize = context.optional<String>("prize").getOrNull()
                plugin.giveawayManager.createGiveaway(sender, prize)
            }
        )

        commandManager.command(builder
            .senderType(Player::class.java)
            .literal("join")
            .permission("$basePermission.join")
            .handler { context ->
                val sender = context.sender()
                plugin.giveawayManager.joinGiveaway(sender)
            }
        )

        commandManager.command(builder
            .senderType(Player::class.java)
            .literal("leave", "quit")
            .permission("$basePermission.leave")
            .handler { context ->
                val sender = context.sender()
                plugin.giveawayManager.leaveGiveaway(sender)
            }
        )

        commandManager.command(builder
            .senderType(Player::class.java)
            .literal("start")
            .permission("$basePermission.start")
            .suspendingHandler { context ->
                val sender = context.sender()
                plugin.giveawayManager.startGiveaway(sender)
            }
        )

        commandManager.command(builder
            .senderType(Player::class.java)
            .literal("stop")
            .permission("$basePermission.stop")
            .handler { context ->
                val sender = context.sender()
                plugin.giveawayManager.stopGiveaway(sender)
            }
        )

        commandManager.command(builder
            .senderType(Player::class.java)
            .literal("stats")
            .permission("$basePermission.stats")
            .handler { context ->
                val sender = context.sender()
                plugin.giveawayManager.showStats(sender)
            }
        )

        commandManager.command(builder
            .literal("reload")
            .permission("$basePermission.reload")
            .handler { context ->
                val sender = context.sender()
                plugin.settingsConfig.reload()
                plugin.messagesConfig.reload()
                sender.sendRichMessage("<green>Reloaded config files!")
            }
        )
    }
}