package me.weiwen.moromoro.actions

object NoopAction : Action {
    override fun perform(ctx: Context): Boolean { return false }
}
