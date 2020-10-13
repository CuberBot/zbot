package net.lz1998.zbot.plugin

import net.lz1998.pbbot.alias.GroupMessageEvent
import net.lz1998.pbbot.alias.PrivateMessageEvent
import net.lz1998.pbbot.bot.Bot
import net.lz1998.pbbot.bot.BotPlugin
import net.lz1998.zbot.aop.annotations.PrefixFilter
import net.lz1998.zbot.service.UserService
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Component

@Component
class SecurityPlugin : BotPlugin() {
    @Autowired
    lateinit var userService: UserService

    @PrefixFilter(".")
    override fun onPrivateMessage(bot: Bot, event: PrivateMessageEvent): Int {
        val userId = event.userId
        var rawMsg = event.rawMessage
        if (rawMsg.startsWith("验证码")) {
            rawMsg = rawMsg.substring("验证码".length).trim()
            val retMsg = userService.register(userId = userId, verificationCode = rawMsg)
            bot.sendPrivateMsg(userId, retMsg)
            return MESSAGE_BLOCK
        }

        return MESSAGE_IGNORE
    }

    @PrefixFilter(".")
    override fun onGroupMessage(bot: Bot, event: GroupMessageEvent): Int {
        val groupId = event.groupId
        val userId = event.userId
        var rawMsg = event.rawMessage
        if (rawMsg.startsWith("验证码")) {
            rawMsg = rawMsg.substring("验证码".length).trim()
            val retMsg = userService.register(userId = userId, verificationCode = rawMsg)
            bot.sendGroupMsg(groupId, retMsg)
            return MESSAGE_BLOCK
        }
        return MESSAGE_IGNORE
    }


}