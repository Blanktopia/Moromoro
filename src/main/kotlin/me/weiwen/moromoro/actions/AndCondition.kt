package me.weiwen.moromoro.actions

class AndCondition(private val actions: List<Action>) : Action {
    override fun perform(ctx: Context): Boolean {
        return actions.all { it.perform(ctx) }
    }
}
