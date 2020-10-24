package net.lz1998.zbot.plugin

import net.lz1998.pbbot.alias.GroupMessageEvent
import net.lz1998.pbbot.bot.Bot
import net.lz1998.pbbot.bot.BotPlugin
import net.lz1998.zbot.aop.annotations.PrefixFilter
import net.lz1998.zbot.aop.annotations.SwitchFilter
import net.lz1998.zbot.service.WcaService
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Component

@Component
@SwitchFilter("link")
class LinkPlugin : BotPlugin() {
    @Autowired
    lateinit var wcaService: WcaService

    @PrefixFilter([".link"])
    override fun onGroupMessage(bot: Bot, event: GroupMessageEvent): Int {
        val groupId = event.groupId
        val userId = event.userId
        val rawMsg = event.rawMessage.toLowerCase().trim()
        val retMsg = wcaService.handleWca(userId, rawMsg) { "https://cubingchina.com/results/person/${it.id}" }
        bot.sendGroupMsg(groupId, retMsg)
        return MESSAGE_BLOCK
    }
}