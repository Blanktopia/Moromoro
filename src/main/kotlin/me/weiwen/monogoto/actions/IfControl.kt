package me.weiwen.monogoto.actions

class IfControl(private val condition: Action, private val ifTrue: List<Action>, private val ifFalse: List<Action>) : Action {
    override fun perform(ctx: Context): Boolean {
        if (condition.perform(ctx)) {
            ifTrue.forEach { it.perform(ctx) }
            return true
        } else {
            ifFalse.forEach { it.perform(ctx) }
            return false
        }
    }
}
