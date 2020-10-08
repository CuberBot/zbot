package net.lz1998.zbot.plugin

import net.lz1998.pbbot.alias.GroupMessageEvent
import net.lz1998.pbbot.bot.Bot
import net.lz1998.pbbot.bot.BotPlugin
import net.lz1998.zbot.aop.annotations.PrefixFilter
import net.lz1998.zbot.service.BlackService
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Component

@Component
class BlackPlugin : BotPlugin() {
    @Autowired
    lateinit var blackService: BlackService

    @PrefixFilter(".")
    override fun onGroupMessage(bot: Bot, event: GroupMessageEvent): Int {
        val groupId = event.groupId
        val userId = event.userId
        val rawMsg = event.rawMessage
        if (blackService.isBlack(userId)) {
            return MESSAGE_BLOCK
        }
        val duration = blackService.onCommand(userId, rawMsg)
        return if (duration > 0) {
            bot.sendGroupMsg(groupId, "你已被屏蔽${duration / 60000}分钟")
            MESSAGE_BLOCK
        } else {
            MESSAGE_IGNORE
        }
    }
}

