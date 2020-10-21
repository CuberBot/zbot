package net.lz1998.zbot.plugin

import net.lz1998.pbbot.alias.GroupMessageEvent
import net.lz1998.pbbot.bot.Bot
import net.lz1998.pbbot.bot.BotPlugin
import net.lz1998.pbbot.utils.Msg
import net.lz1998.zbot.aop.annotations.PrefixFilter
import net.lz1998.zbot.aop.annotations.SwitchFilter
import net.lz1998.zbot.config.ServiceConfig
import net.lz1998.zbot.service.WcaService
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Component

@Component
@SwitchFilter("trend")
class TrendPlugin : BotPlugin() {
    private val eventIdList = listOf("222", "333", "444", "555", "666", "777", "pyram", "skewb", "sq1", "minx", "clock", "333oh", "333bf", "333fm")
    private val typeList = listOf("avg", "sin")
    private val themeList = listOf("light", "dark", "chalk", "essos", "halloween", "infographic", "macarons", "purple_passion", "roma", "romantic", "shine", "vintage", "walden", "westeros", "wonderland")

    @Autowired
    lateinit var wcaService: WcaService

    @PrefixFilter(".")
    override fun onGroupMessage(bot: Bot, event: GroupMessageEvent): Int {
        val groupId = event.groupId
        val userId = event.userId
        var rawMsg = event.rawMessage

        if (rawMsg.startsWith("trend")) {
            rawMsg = rawMsg.substring("trend".length).trim()
            val args = rawMsg.split("-")
            val q = args[0]
            var type = "sin"
            var eventId = "333"
            var theme = "macarons"
            args.forEach {
                if (eventIdList.contains(it.trim())) {
                    eventId = it.trim()
                }
                if (typeList.contains(it.trim())) {
                    type = it.trim()
                }
                if (themeList.contains(it.trim())) {
                    theme = it.trim().replace("_", "-")
                }
            }
            val result = wcaService.handleWca(userId, q) {
                "http://${ServiceConfig.self}/trend/getImage?wcaId=${it.id}&eventId=${eventId}&type=${type}&theme=${theme}"
            }
            if (result.startsWith("http://")) {
                Msg.builder().image(result).sendToGroup(bot, groupId) // 找到精确一个人
            } else {
                bot.sendGroupMsg(groupId, result) // 范围太大
            }
            return MESSAGE_BLOCK

        }
        return super.onGroupMessage(bot, event)
    }
}