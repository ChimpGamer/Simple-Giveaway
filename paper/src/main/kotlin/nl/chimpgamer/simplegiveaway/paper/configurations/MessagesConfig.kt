package nl.chimpgamer.simplegiveaway.paper.configurations

import dev.dejvokep.boostedyaml.YamlDocument
import dev.dejvokep.boostedyaml.dvs.versioning.BasicVersioning
import dev.dejvokep.boostedyaml.settings.dumper.DumperSettings
import dev.dejvokep.boostedyaml.settings.general.GeneralSettings
import dev.dejvokep.boostedyaml.settings.loader.LoaderSettings
import dev.dejvokep.boostedyaml.settings.updater.UpdaterSettings
import nl.chimpgamer.simplegiveaway.paper.SimpleGiveawayPlugin

class MessagesConfig(plugin: SimpleGiveawayPlugin) {
    val config: YamlDocument

    val giveawayAlreadyRunning: String get() = config.getString("giveaway.already-running")
    val giveawayCreated: String get() = config.getString("giveaway.created")
    val giveawayCreatedBroadcast: String get() = config.getString("giveaway.created-broadcast")
    val giveawayNotFound: String get() = config.getString("giveaway.not-found")
    val giveawayJoined: String get() = config.getString("giveaway.joined")
    val giveawayLeft: String get() = config.getString("giveaway.left")
    val giveawayStopped: String get() = config.getString("giveaway.stopped")
    val giveawayStoppedBroadcast: String get() = config.getString("giveaway.stopped-broadcast")
    val giveawayStartNotEnoughParticipants: String get() = config.getString("giveaway.start.not-enough-participants")
    val giveawayStartCountdown: String get() = config.getString("giveaway.start.countdown")
    val giveawayStartWinnerBroadcast: String get() = config.getString("giveaway.start.winner.broadcast")
    val giveawayStats: String get() = config.getString("giveaway.stats")

    val commandsNoPermission: String get() = config.getString("commands.no-permission")

    fun reload() {
        config.reload()
    }

    init {
        val file = plugin.dataFolder.resolve("messages.yml")
        val inputStream = plugin.getResource("messages.yml")
        val loaderSettings = LoaderSettings.builder().setAutoUpdate(true).build()
        val updaterSettings = UpdaterSettings.builder().setVersioning(BasicVersioning("config-version")).build()
        config = if (inputStream != null) {
            YamlDocument.create(file, inputStream, GeneralSettings.DEFAULT, loaderSettings, DumperSettings.DEFAULT, updaterSettings)
        } else {
            YamlDocument.create(file, GeneralSettings.DEFAULT, loaderSettings, DumperSettings.DEFAULT, updaterSettings)
        }
    }
}