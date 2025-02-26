package me.weiwen.moromoro.enchantments.listeners

import me.weiwen.moromoro.Moromoro.Companion.plugin
import net.kyori.adventure.text.Component
import net.kyori.adventure.text.format.NamedTextColor
import org.bukkit.Material
import org.bukkit.NamespacedKey
import org.bukkit.Registry
import org.bukkit.entity.Entity
import org.bukkit.entity.EntityType
import org.bukkit.entity.Player
import org.bukkit.event.EventHandler
import org.bukkit.event.Listener
import org.bukkit.event.entity.EntityDamageEvent
import org.bukkit.event.entity.EntityDeathEvent
import org.bukkit.inventory.ItemStack
import org.bukkit.inventory.meta.SkullMeta

object Beheading : Listener {
    val key = NamespacedKey(plugin.config.namespace, "beheading")

    @EventHandler
    fun onEntityDeath(event: EntityDeathEvent) {
        val entity: Entity = event.entity
        val lastDamageCause = entity.lastDamageCause
        if (lastDamageCause != null && lastDamageCause.cause != EntityDamageEvent.DamageCause.ENTITY_ATTACK && lastDamageCause.cause != EntityDamageEvent.DamageCause.ENTITY_SWEEP_ATTACK) return

        val killer = event.entity.killer ?: return

        val weapon = killer.equipment.itemInMainHand
        val enchantment = Registry.ENCHANTMENT.get(key) ?: return
        if (!weapon.containsEnchantment(enchantment)) return

        val (chance: Double, skull: ItemStack?) = when (entity.type) {
            EntityType.PLAYER -> {
                val skull = ItemStack(Material.PLAYER_HEAD)
                val skullMeta = skull.itemMeta as SkullMeta
                skullMeta.setOwningPlayer(entity as Player)
                skullMeta.displayName(Component.text(entity.name + "'s Head").color(NamedTextColor.YELLOW))
                skullMeta.lore(listOf(Component.text("Killed by ").append(killer.name())))
                skull.itemMeta = skullMeta
                event.drops.add(skull)
                return
            }
            EntityType.SKELETON -> 0.02 to ItemStack(Material.SKELETON_SKULL)
            EntityType.WITHER_SKELETON -> 0.02 to ItemStack(Material.WITHER_SKELETON_SKULL)
            EntityType.ZOMBIE -> 0.02 to ItemStack(Material.ZOMBIE_HEAD)
            EntityType.CREEPER -> 0.02 to ItemStack(Material.CREEPER_HEAD)
            EntityType.ENDER_DRAGON -> 0.2 to ItemStack(Material.DRAGON_HEAD)
            EntityType.PIGLIN -> 0.2 to ItemStack(Material.PIGLIN_HEAD)
            else -> 0.0 to null
        }

        // val (chance: Double, skull: ItemStack?) = when (entity.type) {
        //     EntityType.ELDER_GUARDIAN -> Pair(0.1, playerHeadFromTexture(
        //         "Elder Guardian",
        //         "4340a268f25fd5cc276ca147a8446b2630a55867a2349f7ca107c26eb58991"
        //     ))
        //     EntityType.WITHER_SKELETON -> Pair(0.01, playerHeadFromTexture(
        //         "Wither Skeleton",
        //         ""
        //     ))
        //     EntityType.STRAY -> Pair(0.05, playerHeadFromTexture(
        //         "Stray",
        //         "2c5097916bc0565d30601c0eebfeb287277a34e867b4ea43c63819d53e89ede7"
        //     ))
        //     EntityType.HUSK -> Pair(0.05, playerHeadFromTexture(
        //         "Husk",
        //         "c096164f81950a5cc0e33e87999f98cde792517f4d7f99a647a9aedab23ae58"
        //     ))
        //     EntityType.ZOMBIE_VILLAGER -> Pair(0.05, playerHeadFromTexture(
        //         "Zombie Villager",
        //         "d58698448e316d4bdd833d9621c2db547b60cf5a5566a79f296fd7cc91e918d9"
        //     ))
        //     EntityType.SKELETON_HORSE -> Pair(0.05, playerHeadFromTexture(
        //         "Skeleton Horse", 
        //         "eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvNDdlZmZjZTM1MTMyYzg2ZmY3MmJjYWU3N2RmYmIxZDIyNTg3ZTk0ZGYzY2JjMjU3MGVkMTdjZjg5NzNhIn19fQ=="
        //     ))
        //     EntityType.ZOMBIE_HORSE -> Pair(0.05, playerHeadFromTexture(
        //         "Zombie Horse",
        //         "eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvNWI1NGYyYmMyYjZmNTlhYmI4YzNmOWQ2YzNhMzE1NGUzZmVmZTIzOGYyYmFkNTkzZGIyZGU3ZmEzYTQifX19"
        //     ))
        //     EntityType.DONKEY,
        //     EntityType.MULE -> Pair(0.05, playerHeadFromTexture(
        //         "Donkey",
        //         "eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvZGZiNmMzYzA1MmNmNzg3ZDIzNmEyOTE1ZjgwNzJiNzdjNTQ3NDk3NzE1ZDFkMmY4Y2JjOWQyNDFkODhhIn19fQ=="
        //     ))
        //     EntityType.EVOKER -> Pair(0.05, playerHeadFromTexture(
        //         "Evoker",
        //         "eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvZTc5ZjEzM2E4NWZlYzQ1MDljZTRkZTVmOGJjZjRlNGQyZTlkNTczMDRjNzZmOWYyYzA3ZjM5MWFjNmYzZmEifX19"
        //     ))
        //     EntityType.VEX -> Pair(0.05, playerHeadFromTexture(
        //         "Vex",
        //         "eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvYzJlYzVhNTE2NjE3ZmYxNTczY2QyZjlkNWYzOTY5ZjU2ZDU1NzVjNGZmNGVmZWZhYmQyYTE4ZGM3YWI5OGNkIn19fQ=="
        //     ))
        //     EntityType.VINDICATOR -> Pair(0.05, playerHeadFromTexture(
        //         "Vindicator",
        //         "eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvNmRlYWVjMzQ0YWIwOTViNDhjZWFkNzUyN2Y3ZGVlNjM5YjE2YzczZDc3ZGZjMDlhM2NkMmZlOGFlYjJjMCJ9fX0="
        //     ))
        //     EntityType.ILLUSIONER -> Pair(0.05, playerHeadFromTexture(
        //         "Illusioner",
        //         "eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvNTEyNTEyZTdkMDE2YTIzNDNhN2JmZjFhNGNkMTUzNTdhYjg1MTU3OWYxMzg5YmQ0ZTNhMjRjYmViODhiIn19fQ=="
        //     ))
        //     EntityType.CREEPER -> Pair(0.05, playerHeadFromTexture(
        //         "Creeper",
        //         "eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvZjQyNTQ4MzhjMzNlYTIyN2ZmY2EyMjNkZGRhYWJmZTBiMDIxNWY3MGRhNjQ5ZTk0NDQ3N2Y0NDM3MGNhNjk1MiJ9fX0="
        //     ))
        //     EntityType.SKELETON -> Pair(0.05, playerHeadFromTexture(
        //         "Skeleton",
        //         "eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvMzAxMjY4ZTljNDkyZGExZjBkODgyNzFjYjQ5MmE0YjMwMjM5NWY1MTVhN2JiZjc3ZjRhMjBiOTVmYzAyZWIyIn19fQ=="
        //     ))
        //     EntityType.SPIDER -> Pair(0.05, playerHeadFromTexture(
        //         "Spider",
        //         "eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvY2Q1NDE1NDFkYWFmZjUwODk2Y2QyNThiZGJkZDRjZjgwYzNiYTgxNjczNTcyNjA3OGJmZTM5MzkyN2U1N2YxIn19fQ=="
        //     ))
        //     EntityType.GIANT -> Pair(0.05, playerHeadFromTexture(
        //         "Giant",
        //         "eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvNjM2ZTI2YzQ0NjU5ZTgxNDhlZDU4YWE3OWU0ZDYwZGI1OTVmNDI2NDQyMTE2ZjQxYTM4YzU4NTJlNDRmYzEifX19"
        //     ))
        //     EntityType.ZOMBIE -> Pair(0.05, playerHeadFromTexture(
        //         "Zombie",
        //         "eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvNTZmYzg1NGJiODRjZjRiNzY5NzI5Nzk3M2UwMmI3OWJjMTA2OTg0NjBiNTFhNjM5YzYwZTVlNDE3NzM0ZTExIn19fQ=="
        //     ))
        //     EntityType.SLIME -> Pair(0.02, playerHeadFromTexture(
        //         "Slime",
        //         "eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvYTIwZTg0ZDMyZDFlOWM5MTlkM2ZkYmI1M2YyYjM3YmEyNzRjMTIxYzU3YjI4MTBlNWE0NzJmNDBkYWNmMDA0ZiJ9fX0="
        //     ))
        //     EntityType.GHAST -> Pair(0.05, playerHeadFromTexture(
        //         "Ghast",
        //         "eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvOGI2YTcyMTM4ZDY5ZmJiZDJmZWEzZmEyNTFjYWJkODcxNTJlNGYxYzk3ZTVmOTg2YmY2ODU1NzFkYjNjYzAifX19"
        //     ))
        //     EntityType.ZOMBIFIED_PIGLIN -> Pair(0.05, playerHeadFromTexture(
        //         "Zombified Piglin",
        //         "eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvN2VhYmFlY2M1ZmFlNWE4YTQ5Yzg4NjNmZjQ4MzFhYWEyODQxOThmMWEyMzk4ODkwYzc2NWUwYThkZTE4ZGE4YyJ9fX0="
        //     ))
        //     EntityType.ENDERMAN -> Pair(0.05, playerHeadFromTexture(
        //         "Enderman",
        //         "eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvN2E1OWJiMGE3YTMyOTY1YjNkOTBkOGVhZmE4OTlkMTgzNWY0MjQ1MDllYWRkNGU2YjcwOWFkYTUwYjljZiJ9fX0="
        //     ))
        //     EntityType.CAVE_SPIDER -> Pair(0.05, playerHeadFromTexture(
        //         "Cave Spider",
        //         "eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvNDE2NDVkZmQ3N2QwOTkyMzEwN2IzNDk2ZTk0ZWViNWMzMDMyOWY5N2VmYzk2ZWQ3NmUyMjZlOTgyMjQifX19"
        //     ))
        //     EntityType.SILVERFISH -> Pair(0.02, playerHeadFromTexture(
        //         "Silverfish",
        //         "eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvZGE5MWRhYjgzOTFhZjVmZGE1NGFjZDJjMGIxOGZiZDgxOWI4NjVlMWE4ZjFkNjIzODEzZmE3NjFlOTI0NTQwIn19fQ=="
        //     ))
        //     EntityType.BLAZE -> Pair(0.02, playerHeadFromTexture(
        //         "Blaze",
        //         "eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvYjc4ZWYyZTRjZjJjNDFhMmQxNGJmZGU5Y2FmZjEwMjE5ZjViMWJmNWIzNWE0OWViNTFjNjQ2Nzg4MmNiNWYwIn19fQ=="
        //     ))
        //     EntityType.MAGMA_CUBE -> Pair(0.02, playerHeadFromTexture(
        //         "Magma Cube",
        //         "eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvMzg5NTdkNTAyM2M5MzdjNGM0MWFhMjQxMmQ0MzQxMGJkYTIzY2Y3OWE5ZjZhYjM2Yjc2ZmVmMmQ3YzQyOSJ9fX0="
        //     ))
        //     EntityType.ENDER_DRAGON -> Pair(0.1, playerHeadFromTexture(
        //         "Ender Dragon",
        //         "eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvNzRlY2MwNDA3ODVlNTQ2NjNlODU1ZWYwNDg2ZGE3MjE1NGQ2OWJiNGI3NDI0YjczODFjY2Y5NWIwOTVhIn19fQ=="
        //     ))
        //     EntityType.WITHER -> Pair(0.1, playerHeadFromTexture(
        //         "Wither",
        //         "eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvY2RmNzRlMzIzZWQ0MTQzNjk2NWY1YzU3ZGRmMjgxNWQ1MzMyZmU5OTllNjhmYmI5ZDZjZjVjOGJkNDEzOWYifX19"
        //     ))
        //     EntityType.BAT -> Pair(0.05, playerHeadFromTexture(
        //         "Bat",
        //         "eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvMzgyMGExMGRiMjIyZjY5YWMyMjE1ZDdkMTBkY2E0N2VlYWZhMjE1NTUzNzY0YTJiODFiYWZkNDc5ZTc5MzNkMSJ9fX0="
        //     ))
        //     EntityType.WITCH -> Pair(0.05, playerHeadFromTexture(
        //         "Witch",
        //         "eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvMjBlMTNkMTg0NzRmYzk0ZWQ1NWFlYjcwNjk1NjZlNDY4N2Q3NzNkYWMxNmY0YzNmODcyMmZjOTViZjlmMmRmYSJ9fX0="
        //     ))
        //     EntityType.ENDERMITE -> Pair(0.05, playerHeadFromTexture(
        //         "Endermite",
        //         "eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvNWJjN2I5ZDM2ZmI5MmI2YmYyOTJiZTczZDMyYzZjNWIwZWNjMjViNDQzMjNhNTQxZmFlMWYxZTY3ZTM5M2EzZSJ9fX0="
        //     ))
        //     EntityType.GUARDIAN -> Pair(0.02, playerHeadFromTexture(
        //         "Guardian",
        //         "eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvYTBiZjM0YTcxZTc3MTViNmJhNTJkNWRkMWJhZTVjYjg1Zjc3M2RjOWIwZDQ1N2I0YmZjNWY5ZGQzY2M3Yzk0In19fQ=="
        //     ))
        //     EntityType.SHULKER -> Pair(0.05, playerHeadFromTexture(
        //         "Shulker",
        //         "eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvYjFkMzUzNGQyMWZlODQ5OTI2MmRlODdhZmZiZWFjNGQyNWZmZGUzNWM4YmRjYTA2OWU2MWUxNzg3ZmYyZiJ9fX0="
        //     ))
        //     EntityType.PIG -> Pair(0.05, playerHeadFromTexture(
        //         "Pig",
        //         "eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvNjIxNjY4ZWY3Y2I3OWRkOWMyMmNlM2QxZjNmNGNiNmUyNTU5ODkzYjZkZjRhNDY5NTE0ZTY2N2MxNmFhNCJ9fX0="
        //     ))
        //     EntityType.SHEEP -> Pair(0.05, playerHeadFromTexture(
        //         "Sheep",
        //         "eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvZjMxZjljY2M2YjNlMzJlY2YxM2I4YTExYWMyOWNkMzNkMThjOTVmYzczZGI4YTY2YzVkNjU3Y2NiOGJlNzAifX19"
        //     ))
        //     EntityType.COW -> Pair(0.05, playerHeadFromTexture(
        //         "Cow",
        //         "eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvNWQ2YzZlZGE5NDJmN2Y1ZjcxYzMxNjFjNzMwNmY0YWVkMzA3ZDgyODk1ZjlkMmIwN2FiNDUyNTcxOGVkYzUifX19"
        //     ))
        //     EntityType.CHICKEN -> Pair(0.05, playerHeadFromTexture(
        //         "Chicken",
        //         "eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvMTYzODQ2OWE1OTljZWVmNzIwNzUzNzYwMzI0OGE5YWIxMWZmNTkxZmQzNzhiZWE0NzM1YjM0NmE3ZmFlODkzIn19fQ=="
        //     ))
        //     EntityType.SQUID -> Pair(0.05, playerHeadFromTexture(
        //         "Squid",
        //         "eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvMDE0MzNiZTI0MjM2NmFmMTI2ZGE0MzRiODczNWRmMWViNWIzY2IyY2VkZTM5MTQ1OTc0ZTljNDgzNjA3YmFjIn19fQ=="
        //     ))
        //     EntityType.WOLF -> Pair(0.05, playerHeadFromTexture(
        //         "Wolf",
        //         "eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvZGMzZGQ5ODRiYjY1OTg0OWJkNTI5OTQwNDY5NjRjMjNjYjU3MjUzNGQ0YjBkMWE1YmYyZTlmNTU0YTFkYzM5MiJ9fX0="
        //     ))
        //     EntityType.MOOSHROOM -> Pair(0.05, playerHeadFromTexture(
        //         "Mooshroom",
        //         "eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvZDBiYzYxYjk3NTdhN2I4M2UwM2NkMjUwN2EyMTU3OTEzYzJjZjAxNmU3YzA5NmE0ZDZjZjFmZTFiOGRiIn19fQ=="
        //     ))
        //     EntityType.SNOW_GOLEM -> Pair(0.05, playerHeadFromTexture(
        //         "Snow Golem",
        //         "eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvMWZkZmQxZjc1MzhjMDQwMjU4YmU3YTkxNDQ2ZGE4OWVkODQ1Y2M1ZWY3MjhlYjVlNjkwNTQzMzc4ZmNmNCJ9fX0="
        //     ))
        //     EntityType.OCELOT -> Pair(0.05, playerHeadFromTexture(
        //         "Ocelot",
        //         "eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvNTY1N2NkNWMyOTg5ZmY5NzU3MGZlYzRkZGNkYzY5MjZhNjhhMzM5MzI1MGMxYmUxZjBiMTE0YTFkYjEifX19"
        //     ))
        //     EntityType.IRON_GOLEM -> Pair(0.05, playerHeadFromTexture(
        //         "Iron Golem",
        //         "eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvODkwOTFkNzllYTBmNTllZjdlZjk0ZDdiYmE2ZTVmMTdmMmY3ZDQ1NzJjNDRmOTBmNzZjNDgxOWE3MTQifX19"
        //     ))
        //     EntityType.HORSE -> Pair(0.05, playerHeadFromTexture(
        //         "Horse",
        //         "eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvNDJlYjk2N2FiOTRmZGQ0MWE2MzI1ZjEyNzdkNmRjMDE5MjI2ZTVjZjM0OTc3ZWVlNjk1MzY1NGFjMGE3ZmJhMCJ9fX0="
        //     ))
        //     EntityType.RABBIT -> Pair(0.05, playerHeadFromTexture(
        //         "Rabbit",
        //         "eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvZmZlY2M2YjVlNmVhNWNlZDc0YzQ2ZTc2MjdiZTNmMDgyNjMyN2ZiYTI2Mzg2YzZjYzc4NjMzNzJlOWJjIn19fQ=="
        //     ))
        //     EntityType.POLAR_BEAR -> Pair(0.05, playerHeadFromTexture(
        //         "Polar Bear",
        //         ""
        //     ))
        //     EntityType.LLAMA -> Pair(0.05, playerHeadFromTexture(
        //         "Llama",
        //         ""
        //     ))
        //     EntityType.LLAMA_SPIT -> Pair(0.0, null)
        //     EntityType.PARROT -> Pair(0.05, playerHeadFromTexture(
        //         "Parrot",
        //         ""
        //     ))
        //     EntityType.VILLAGER -> Pair(0.05, playerHeadFromTexture(
        //         "Villager", 
        //         ""
        //     ))
        //     EntityType.TURTLE -> Pair(0.05, playerHeadFromTexture(
        //         "Turtle",
        //         ""
        //     ))
        //     EntityType.PHANTOM -> Pair(0.05, playerHeadFromTexture(
        //         "Phantom",
        //         ""
        //     ))
        //     EntityType.COD -> Pair(0.05, playerHeadFromTexture(
        //         "Cod",
        //         ""
        //     ))
        //     EntityType.SALMON -> Pair(0.05, playerHeadFromTexture(
        //         "Salmon",
        //         ""
        //     ))
        //     EntityType.PUFFERFISH -> Pair(0.05, playerHeadFromTexture(
        //         "Pufferfish",
        //         ""
        //     ))
        //     EntityType.TROPICAL_FISH -> Pair(0.05, playerHeadFromTexture(
        //         "Tropical Fish",
        //         ""
        //     ))
        //     EntityType.DROWNED -> Pair(0.04, playerHeadFromTexture(
        //         "Drowned",
        //         ""
        //     ))
        //     EntityType.DOLPHIN -> Pair(0.05, playerHeadFromTexture(
        //         "Dolphin",
        //         ""
        //     ))
        //     EntityType.CAT -> Pair(0.14, playerHeadFromTexture(
        //         "Cat",
        //         ""
        //     ))
        //     EntityType.PANDA -> Pair(0.05, playerHeadFromTexture(
        //         "Panda",
        //         ""
        //     ))
        //     EntityType.PILLAGER -> Pair(0.05, playerHeadFromTexture(
        //         "Pillager",
        //         ""
        //     ))
        //     EntityType.RAVAGER -> Pair(0.05, playerHeadFromTexture(
        //         "Ravager",
        //         ""
        //     ))
        //     EntityType.TRADER_LLAMA -> Pair(0.05, playerHeadFromTexture(
        //         "Trader Llama",
        //         ""
        //     ))
        //     EntityType.WANDERING_TRADER -> Pair(0.05, playerHeadFromTexture(
        //         "Wandering Trader",
        //         ""
        //     ))
        //     EntityType.FOX -> Pair(0.05, playerHeadFromTexture(
        //         "Fox",
        //         ""
        //     ))
        //     EntityType.BEE -> Pair(0.05, playerHeadFromTexture(
        //         "Bee",
        //         ""
        //     ))
        //     EntityType.HOGLIN -> Pair(0.05, playerHeadFromTexture(
        //         "Hoglin",
        //         ""
        //     ))
        //     EntityType.PIGLIN -> Pair(0.05, playerHeadFromTexture(
        //         "Piglin",
        //         "5081a1239fffe135cbfa4a98a6aa6cc5b0787ad0790f56a16bf07f86374606c5"
        //     ))
        //     EntityType.STRIDER -> Pair(0.05, playerHeadFromTexture(
        //         "Strider",
        //         "18a9adf780ec7dd4625c9c0779052e6a15a451866623511e4c82e9655714b3c1"
        //     ))
        //     EntityType.ZOGLIN -> Pair(0.05, playerHeadFromTexture(
        //         "Zoglin",
        //         "e67e18602e03035ad68967ce090235d8996663fb9ea47578d3a7ebbc42a5ccf9"
        //     ))
        //     EntityType.PIGLIN_BRUTE -> Pair(0.05, playerHeadFromTexture(
        //         "Piglin Brute",
        //         ""
        //     ))
        //     EntityType.AXOLOTL -> Pair(0.05, playerHeadFromTexture(
        //         "Axolotl",
        //         ""
        //     ))
        //     EntityType.GLOW_SQUID -> Pair(0.05, playerHeadFromTexture(
        //         "Glow Squid",
        //         ""
        //     ))
        //     EntityType.GOAT -> Pair(0.05, playerHeadFromTexture(
        //         "Goat",
        //         ""
        //     ))
        //     EntityType.ALLAY -> Pair(0.05, playerHeadFromTexture(
        //         "Allay",
        //         ""
        //     ))
        //     EntityType.FROG -> Pair(0.05, playerHeadFromTexture(
        //         "Frog",
        //         ""
        //     ))
        //     EntityType.TADPOLE -> Pair(0.05, playerHeadFromTexture(
        //         "Tadpole",
        //         ""
        //     ))
        //     EntityType.WARDEN -> Pair(0.05, playerHeadFromTexture(
        //         "Warden",
        //         ""
        //     ))
        //     EntityType.CAMEL -> Pair(0.05, playerHeadFromTexture(
        //         "Camel",
        //         ""
        //     ))
        //     EntityType.SNIFFER -> Pair(0.05, playerHeadFromTexture(
        //         "Sniffer",
        //         ""
        //     ))
        //     EntityType.BREEZE -> Pair(0.05, playerHeadFromTexture(
        //         "Breeze",
        //         ""
        //     ))
        //     EntityType.ARMADILLO -> Pair(0.05, playerHeadFromTexture(
        //         "Armadillo",
        //         ""
        //     ))
        //     EntityType.BOGGED -> Pair(0.05, playerHeadFromTexture(
        //         "Bogged",
        //         ""
        //     ))
        //     EntityType.CREAKING -> Pair(0.05, playerHeadFromTexture(
        //         "Creaking",
        //         ""
        //     ))
        //     EntityType.PLAYER -> Pair(0.0, null)
        // }

        if (skull != null && (weapon.getEnchantLevel(enchantment)) * chance > Math.random()) {
            event.drops.add(skull)
        }
    }
}
