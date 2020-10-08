package net.lz1998.zbot.plugin

import net.lz1998.pbbot.alias.GroupMessageEvent
import net.lz1998.pbbot.bot.Bot
import net.lz1998.pbbot.bot.BotPlugin
import net.lz1998.zbot.aop.annotations.PrefixFilter
import net.lz1998.zbot.aop.annotations.SwitchFilter
import net.lz1998.zbot.config.ServiceConfig
import net.lz1998.zbot.service.WcaService
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Component
import java.net.URL
import java.net.URLEncoder
import java.nio.charset.Charset

@Component
@SwitchFilter("rank")
class RankPlugin : BotPlugin() {
    @Autowired
    lateinit var wcaService: WcaService

    val findPersonUrl: String get() = "http://${ServiceConfig.rank}/getRank/person?wcaid="

    @PrefixFilter(".")
    override fun onGroupMessage(bot: Bot, event: GroupMessageEvent): Int {
        var rawMsg = event.rawMessage
        val groupId = event.groupId
        val userId = event.userId
        if (rawMsg.startsWith("rank")) {
            rawMsg = rawMsg.substring("rank".length).trim()
            val url = "${findPersonUrl}${URLEncoder.encode(rawMsg, Charsets.UTF_8.name())}"
            val retMsg = URL(url).readText(Charset.forName("GBK")).replace("#success", "")
            bot.sendGroupMsg(groupId, retMsg)
            return MESSAGE_BLOCK
        }
        return MESSAGE_IGNORE
    }
}