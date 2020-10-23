package net.lz1998.zbot.plugin

import net.lz1998.pbbot.alias.GroupMessageEvent
import net.lz1998.pbbot.bot.Bot
import net.lz1998.pbbot.bot.BotPlugin
import net.lz1998.zbot.aop.annotations.PrefixFilter
import net.lz1998.zbot.aop.annotations.SwitchFilter
import net.lz1998.zbot.service.ExpressService
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Component

@Component
@SwitchFilter("快递")
class ExpressPlugin : BotPlugin() {

    @Autowired
    lateinit var expressService: ExpressService

    @PrefixFilter(".")
    override fun onGroupMessage(bot: Bot, event: GroupMessageEvent): Int {
        val groupId = event.groupId
        var rawMsg = event.rawMessage
        if (rawMsg.startsWith("查快递")) {
            rawMsg = rawMsg.substring("查快递".length).trim()
            rawMsg = rawMsg.replace("：", ":")
            var type = "auto";
            var number = rawMsg
            val split = rawMsg.split(":")
            if (split.size > 1) {
                number = split[0]
                type = split[1]
            }
            val result = expressService.queryExpress(number, type)
            bot.sendGroupMsg(groupId, result)
            return MESSAGE_BLOCK
        }
        return MESSAGE_IGNORE
    }
}