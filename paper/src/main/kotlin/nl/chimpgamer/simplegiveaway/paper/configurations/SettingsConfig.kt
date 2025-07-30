package nl.chimpgamer.simplegiveaway.paper.configurations

import dev.dejvokep.boostedyaml.YamlDocument
import dev.dejvokep.boostedyaml.dvs.versioning.BasicVersioning
import dev.dejvokep.boostedyaml.settings.dumper.DumperSettings
import dev.dejvokep.boostedyaml.settings.general.GeneralSettings
import dev.dejvokep.boostedyaml.settings.loader.LoaderSettings
import dev.dejvokep.boostedyaml.settings.updater.UpdaterSettings
import nl.chimpgamer.simplegiveaway.paper.SimpleGiveawayPlugin
import nl.chimpgamer.simplegiveaway.paper.models.ConfigurableFirework
import nl.chimpgamer.simplegiveaway.paper.models.ConfigurableSound

class SettingsConfig(plugin: SimpleGiveawayPlugin) {
    val config: YamlDocument

    val giveawayJoinSound: ConfigurableSound get() = ConfigurableSound.deserialize(config.getSection("giveaway.join.sound").getStringRouteMappedValues(false))
    val giveawayLeaveSound: ConfigurableSound get() = ConfigurableSound.deserialize(config.getSection("giveaway.leave.sound").getStringRouteMappedValues(false))
    val giveawayStartCountdownSound: ConfigurableSound get() = ConfigurableSound.deserialize(config.getSection("giveaway.start.countdown.sound").getStringRouteMappedValues(false))
    val giveawayStartWinnerAnnouncementSound: ConfigurableSound get() = ConfigurableSound.deserialize(config.getSection("giveaway.start.winner-announcement.sound").getStringRouteMappedValues(false))
    val giveawayStartWinnerAnnouncementFirework: ConfigurableFirework get() = ConfigurableFirework.deserialize(config.getSection("giveaway.start.winner-announcement.firework").getStringRouteMappedValues(false))

    fun reload() {
        config.reload()
    }

    init {
        val file = plugin.dataFolder.resolve("settings.yml")
        val inputStream = plugin.getResource("settings.yml")
        val loaderSettings = LoaderSettings.builder().setAutoUpdate(true).build()
        val updaterSettings = UpdaterSettings.builder().setVersioning(BasicVersioning("config-version")).build()
        config = if (inputStream != null) {
            YamlDocument.create(file, inputStream, GeneralSettings.DEFAULT, loaderSettings, DumperSettings.DEFAULT, updaterSettings)
        } else {
            YamlDocument.create(file, GeneralSettings.DEFAULT, loaderSettings, DumperSettings.DEFAULT, updaterSettings)
        }
    }
}