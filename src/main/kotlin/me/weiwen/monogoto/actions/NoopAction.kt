package me.weiwen.monogoto.actions

object NoopAction : Action {
    override fun perform(ctx: Context): Boolean { return false }
}