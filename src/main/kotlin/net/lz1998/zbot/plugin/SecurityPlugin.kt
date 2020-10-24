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

    @PrefixFilter([".验证码"])
    override fun onPrivateMessage(bot: Bot, event: PrivateMessageEvent): Int {
        val userId = event.userId
        var rawMsg = event.rawMessage.trim()
        val retMsg = userService.register(userId = userId, verificationCode = rawMsg)
        bot.sendPrivateMsg(userId, retMsg)
        return MESSAGE_BLOCK
    }

    @PrefixFilter([".验证码"])
    override fun onGroupMessage(bot: Bot, event: GroupMessageEvent): Int {
        val groupId = event.groupId
        val userId = event.userId
        val rawMsg = event.rawMessage.trim()
        val retMsg = userService.register(userId = userId, verificationCode = rawMsg)
        bot.sendGroupMsg(groupId, retMsg)
        return MESSAGE_BLOCK
    }


}