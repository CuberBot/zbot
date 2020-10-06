package net.lz1998.zbot.plugin

import net.lz1998.pbbot.alias.GroupMessageEvent
import net.lz1998.pbbot.alias.PrivateMessageEvent
import net.lz1998.pbbot.bot.Bot
import net.lz1998.pbbot.bot.BotPlugin
import net.lz1998.zbot.config.Config
import org.springframework.stereotype.Component

@Component
class TestPlugin : BotPlugin() {
    override fun onPrivateMessage(bot: Bot, event: PrivateMessageEvent): Int {
        val userId = event.userId
        val msg = event.rawMessage
        if (userId == Config.MAIN_ADMIN && msg == "hi") {
            bot.sendPrivateMsg(userId, "hello", false)
        }
        return super.onPrivateMessage(bot, event)
    }

    override fun onGroupMessage(bot: Bot, event: GroupMessageEvent): Int {
        val userId = event.userId
        val groupId = event.groupId
        val msg = event.rawMessage
        if (userId == Config.MAIN_ADMIN && msg == "hi") {
            bot.sendGroupMsg(groupId, "hello", false)
        }
        return super.onGroupMessage(bot, event)
    }
}