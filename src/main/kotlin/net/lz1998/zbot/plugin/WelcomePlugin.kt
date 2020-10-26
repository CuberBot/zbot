package net.lz1998.zbot.plugin

import net.lz1998.pbbot.alias.GroupIncreaseNoticeEvent
import net.lz1998.pbbot.alias.GroupMessageEvent
import net.lz1998.pbbot.bot.Bot
import net.lz1998.pbbot.bot.BotPlugin
import net.lz1998.zbot.aop.annotations.PrefixFilter
import net.lz1998.zbot.aop.annotations.SwitchFilter
import net.lz1998.zbot.service.WelcomeService
import net.lz1998.zbot.service.isAdmin
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Component


// TODO 消息入库序列化，反序列化，使用messageChain

@Component
@SwitchFilter("欢迎")
class WelcomePlugin : BotPlugin() {

    @Autowired
    lateinit var welcomeService: WelcomeService

    @PrefixFilter(".")
    override fun onGroupMessage(bot: Bot, event: GroupMessageEvent): Int {
        if (!isAdmin(event.sender)) {
            return MESSAGE_IGNORE
        }
        val groupId = event.groupId
        val userId = event.userId
        var rawMsg = event.rawMessage
        if (rawMsg.startsWith("设置欢迎")) {
            rawMsg = rawMsg.substring("设置欢迎".length).trim()
            if (rawMsg.length > 1000) {
                bot.sendGroupMsg(groupId, "错误：欢迎 最大长度1000")
                return MESSAGE_BLOCK
            }
            welcomeService.setWelcomeMsg(groupId = groupId, welcomeMsg = rawMsg, adminId = userId)
            bot.sendGroupMsg(groupId, "设置成功")
            return MESSAGE_BLOCK
        }
        if (rawMsg == "查看欢迎") {
            var welcomeMsg = welcomeService.getWelcomeMsg(groupId)
            if (welcomeMsg.isEmpty()) {
                welcomeMsg = "无"
            }
            welcomeMsg = welcomeMsg.replace("{{userId}}", userId.toString())
            bot.sendGroupMsg(groupId, welcomeMsg)
            return MESSAGE_BLOCK
        }
        return MESSAGE_IGNORE
    }

    override fun onGroupIncreaseNotice(bot: Bot, event: GroupIncreaseNoticeEvent): Int {
        val groupId = event.groupId
        val userId = event.userId
        var welcomeMsg = welcomeService.getWelcomeMsg(groupId)
        if (welcomeMsg.isNotEmpty()) {
            welcomeMsg = welcomeMsg.replace("{{userId}}", userId.toString())
            bot.sendGroupMsg(groupId, welcomeMsg)
            return MESSAGE_BLOCK
        }
        return MESSAGE_IGNORE
    }
}