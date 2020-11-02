package net.lz1998.zbot.plugin

import net.lz1998.pbbot.alias.GroupMessageEvent
import net.lz1998.pbbot.alias.PrivateMessageEvent
import net.lz1998.pbbot.bot.Bot
import net.lz1998.pbbot.bot.BotContainer
import net.lz1998.pbbot.bot.BotPlugin
import net.lz1998.zbot.config.ZbotConfig
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Component

@Component
class TestPlugin : BotPlugin() {

    @Autowired
    lateinit var botContainer: BotContainer
    override fun onPrivateMessage(bot: Bot, event: PrivateMessageEvent): Int {
        val userId = event.userId
        val msg = event.rawMessage
        if (userId == ZbotConfig.mainAdmin && msg == "hi") {
            val result = "在线机器人\n" + botContainer.bots.keys.map { it.toString() }.joinToString("\n")
            bot.sendPrivateMsg(userId, result, false)
        }
        return super.onPrivateMessage(bot, event)
    }

    override fun onGroupMessage(bot: Bot, event: GroupMessageEvent): Int {
        val userId = event.userId
        val groupId = event.groupId
        val msg = event.rawMessage
        if (userId == ZbotConfig.mainAdmin && msg == "hi") {
            bot.sendGroupMsg(groupId, "hello", false)
        }
        return super.onGroupMessage(bot, event)
    }
}