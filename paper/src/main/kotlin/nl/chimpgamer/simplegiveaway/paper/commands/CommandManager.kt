package nl.chimpgamer.simplegiveaway.paper.commands

import nl.chimpgamer.simplegiveaway.paper.SimpleGiveawayPlugin
import org.bukkit.command.CommandSender
import org.incendo.cloud.bukkit.CloudBukkitCapabilities
import org.incendo.cloud.caption.CaptionProvider
import org.incendo.cloud.caption.StandardCaptionKeys
import org.incendo.cloud.execution.ExecutionCoordinator
import org.incendo.cloud.minecraft.extras.MinecraftExceptionHandler
import org.incendo.cloud.minecraft.extras.caption.ComponentCaptionFormatter
import org.incendo.cloud.paper.LegacyPaperCommandManager
import java.util.logging.Level

class CommandManager(private val plugin: SimpleGiveawayPlugin) {

    private lateinit var paperCommandManager: LegacyPaperCommandManager<CommandSender>

    fun initialize() {
        try {
            paperCommandManager = LegacyPaperCommandManager.createNative(plugin, ExecutionCoordinator.asyncCoordinator())

            if (paperCommandManager.hasCapability(CloudBukkitCapabilities.NATIVE_BRIGADIER)) {
                paperCommandManager.registerBrigadier()
                val brigadierManager = paperCommandManager.brigadierManager()
                brigadierManager.setNativeNumberSuggestions(false)
            } else if (paperCommandManager.hasCapability(CloudBukkitCapabilities.ASYNCHRONOUS_COMPLETION)) {
                paperCommandManager.registerAsynchronousCompletions()
            }

            paperCommandManager.captionRegistry().registerProvider(CaptionProvider.constantProvider(StandardCaptionKeys.EXCEPTION_NO_PERMISSION, plugin.messagesConfig.commandsNoPermission))

            MinecraftExceptionHandler.createNative<CommandSender>()
                .defaultHandlers()
                .captionFormatter(ComponentCaptionFormatter.miniMessage())
                .registerTo(paperCommandManager)
        } catch (ex: Exception) {
            plugin.logger.log(Level.SEVERE, "Failed to initialize the command manager", ex)
        }
    }

    fun loadCommands() {
        GiveawayCommand(plugin).registerCommands(paperCommandManager, "giveaway", "weggeefactie")
    }
}