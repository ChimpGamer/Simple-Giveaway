package nl.chimpgamer.simplegiveaway.paper.models

import org.bukkit.Color
import org.bukkit.FireworkEffect
import org.bukkit.entity.Firework
import org.bukkit.entity.Player

class ConfigurableFirework(
    private val enabled: Boolean,
    private val fireworkEffect: FireworkEffect
) {

    fun play(player: Player) {
        if (!enabled) return
        val location = player.location.clone().add(0.0, 1.0, 0.0)
        val firework = player.location.world.spawn(location, Firework::class.java)
        val fireworkMeta = firework.fireworkMeta.apply {
            addEffect(fireworkEffect)
            power = 1
        }
        firework.fireworkMeta = fireworkMeta
    }

    companion object {
        fun deserialize(map: Map<String, Any>): ConfigurableFirework {
            var enabled = false
            var type = FireworkEffect.Type.STAR
            var flicker = false
            var trail = false
            var colors: List<Color> = ArrayList()
            var fadeColors: List<Color> = ArrayList()

            if (map.containsKey("enabled")) {
                enabled = map["enabled"].toString().toBoolean()
            }
            if (map.containsKey("type")) {
                type = FireworkEffect.Type.valueOf(map["type"].toString())
            }
            if (map.containsKey("flicker")) {
                flicker = map["flicker"].toString().toBoolean()
            }
            if (map.containsKey("trail")) {
                trail = map["trail"].toString().toBoolean()
            }
            if (map.containsKey("colors")) {
                colors = (map["colors"] as List<*>).filterIsInstance<Map<String, Any>>().map { Color.deserialize(it) }.toList()
            }
            if (map.containsKey("fade-colors")) {
                fadeColors = (map["fade-colors"] as List<*>).filterIsInstance<Map<String, Any>>().map { Color.deserialize(it) }.toList()
            }
            val fireworkEffect = FireworkEffect.builder()
                .flicker(flicker)
                .trail(trail)
                .withColor(colors)
                .withFade(fadeColors)
                .with(type)
                .build()

            return ConfigurableFirework(enabled, fireworkEffect)
        }
    }
}