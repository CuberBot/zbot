package net.lz1998.zbot.plugin

import net.lz1998.pbbot.alias.FriendRequestEvent
import net.lz1998.pbbot.bot.Bot
import net.lz1998.pbbot.bot.BotPlugin
import org.springframework.stereotype.Component

@Component
class AddFriendPlugin : BotPlugin() {
    override fun onFriendRequest(bot: Bot, event: FriendRequestEvent): Int {
        bot.setFriendAddRequest(event.flag, true, "")
        return MESSAGE_BLOCK
    }
}