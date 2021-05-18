package me.weiwen.moromoro.actions

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
@SerialName("not")
data class Not(val action: Action) : Action {
    override fun perform(ctx: Context): Boolean {
        return !action.perform(ctx)
    }
}

