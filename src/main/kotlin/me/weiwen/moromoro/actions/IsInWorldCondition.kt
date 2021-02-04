package me.weiwen.moromoro.actions

class IsInWorldCondition(private val world: String) : Action {
    override fun perform(ctx: Context): Boolean {
        return ctx.player.world.name == world
    }
}
