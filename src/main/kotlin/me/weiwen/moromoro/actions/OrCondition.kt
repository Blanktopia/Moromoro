package me.weiwen.moromoro.actions

class OrCondition(private val actions: List<Action>) : Action {
    override fun perform(ctx: Context): Boolean {
        return actions.any { it.perform(ctx) }
    }
}
