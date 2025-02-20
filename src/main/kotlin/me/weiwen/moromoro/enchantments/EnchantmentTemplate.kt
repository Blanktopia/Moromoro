@file:UseSerializers(
    ItemTypeSerializer::class,
    EnchantmentSerializer::class,
    ColorSerializer::class,
    EquipmentSlotGroupSerializer::class,
)

package me.weiwen.moromoro.items

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import kotlinx.serialization.UseSerializers
import me.weiwen.moromoro.serializers.*
import org.bukkit.inventory.EquipmentSlotGroup

@Serializable
data class EnchantmentTemplate(
    val name: FormattedString? = null,
    val order: Int = Int.MAX_VALUE,
    @SerialName("anvil-cost")
    val anvilCost: Int = 3,
    @SerialName("max-level")
    val maxLevel: Int = 1,
    val weight: Int = 1,
    @SerialName("exclusive-with")
    val exclusiveWith: List<String> = listOf(),
    @SerialName("minimum-cost-base")
    val minimumCostBase: Int = 1,
    @SerialName("minimum-cost-per-level")
    val minimumCostPerLevel: Int = 0,
    @SerialName("maximum-cost-base")
    val maximumCostBase: Int? = null,
    @SerialName("maximum-cost-per-level")
    val maximumCostPerLevel: Int? = null,
    @SerialName("primary-items")
    val primaryItems: List<String> = listOf(),
    @SerialName("supported-items")
    val supportedItems: List<String>? = null,
    val slots: EquipmentSlotGroup = EquipmentSlotGroup.ANY,

    val curse: Boolean = false,
    val treasure: Boolean = false,
    @SerialName("smelts-loot")
    val smeltsLoot: Boolean = false,
)

fun EnchantmentTemplate.enchantment() {

}