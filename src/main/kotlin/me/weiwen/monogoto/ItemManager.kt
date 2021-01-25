package me.weiwen.monogoto

class ItemManager(val plugin: Monogoto) {
    val items: Map<String, ItemTemplate> = HashMap()
    var names: Set<String> = setOf()
        private set

    fun load() {
        // TODO: Load items from configuration

        names = items.keys
    }
}
