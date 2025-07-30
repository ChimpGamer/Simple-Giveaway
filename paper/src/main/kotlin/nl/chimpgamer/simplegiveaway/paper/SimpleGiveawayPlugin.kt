package nl.chimpgamer.simplegiveaway.paper

import net.kyori.adventure.text.Component
import nl.chimpgamer.simplegiveaway.paper.commands.CommandManager
import nl.chimpgamer.simplegiveaway.paper.configurations.MessagesConfig
import nl.chimpgamer.simplegiveaway.paper.configurations.SettingsConfig
import nl.chimpgamer.simplegiveaway.paper.listeners.ConnectionListener
import nl.chimpgamer.simplegiveaway.paper.managers.GiveawayManager
import org.bukkit.plugin.java.JavaPlugin

class SimpleGiveawayPlugin : JavaPlugin() {

    val settingsConfig = SettingsConfig(this)
    val messagesConfig = MessagesConfig(this)

    val commandManager = CommandManager(this)

    val giveawayManager = GiveawayManager(this)

    override fun onEnable() {
        super.onEnable()

        commandManager.initialize()
        commandManager.loadCommands()

        server.pluginManager.registerEvents(ConnectionListener(this), this)
    }

    override fun onDisable() {
        super.onDisable()
    }

    fun broadcast(message: Component) {
        server.consoleSender.sendMessage(message)
        server.onlinePlayers.forEach { player -> player.sendMessage(message) }
    }
}