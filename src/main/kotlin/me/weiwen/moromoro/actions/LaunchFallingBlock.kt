@file:UseSerializers(MaterialSerializer::class)

package me.weiwen.moromoro.actions

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import kotlinx.serialization.UseSerializers
import me.weiwen.moromoro.extensions.pitch
import me.weiwen.moromoro.serializers.MaterialSerializer
import org.bukkit.Bukkit.createBlockData
import org.bukkit.Material

@Serializable
@SerialName("launch-falling-block")
data class LaunchFallingBlock(
    val material: Material,
    val magnitude: Double = 1.5,
    val pitch: Double = 0.0,
    val isPitchRelative: Boolean = true,
    val canDropItem: Boolean = true,
    val canHurtEntities: Boolean = false,
    val disguise: DisguiseData? = null,
) : Action {
    override fun perform(ctx: Context): Boolean {
        val player = ctx.player ?: return false
        val entity = player.world.spawnFallingBlock(player.eyeLocation, createBlockData(material))
        entity.dropItem = canDropItem
        entity.setHurtEntities(canHurtEntities)

        val v = player.location.direction

        if (!isPitchRelative) {
            v.pitch = 0.0
        }
        v.pitch += pitch
        v.normalize().multiply(magnitude)
        entity.velocity = v

        if (disguise != null) {
            val disguise = disguise.disguise
            disguise.entity = entity
            disguise.startDisguise()
        }

        return true
    }
}
