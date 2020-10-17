package net.lz1998.zbot.plugin

import net.lz1998.pbbot.alias.GroupMessageEvent
import net.lz1998.pbbot.bot.Bot
import net.lz1998.pbbot.bot.BotPlugin
import net.lz1998.zbot.aop.annotations.PrefixFilter
import net.lz1998.zbot.aop.annotations.SwitchFilter
import net.lz1998.zbot.service.CubingService
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Component

@Component
@SwitchFilter("cuber")
class CuberPlugin : BotPlugin() {
    @Autowired
    lateinit var cubingService: CubingService

    @PrefixFilter(".")
    override fun onGroupMessage(bot: Bot, event: GroupMessageEvent): Int {
        val groupId = event.groupId
        var rawMsg = event.rawMessage
        if (rawMsg.startsWith("cuber")) {
            rawMsg = rawMsg.substring("cuber".length).trim()
            val competitionNameList = cubingService
                    .getCompetitionList()
                    .filter { System.currentTimeMillis() < it.date.to * 1000L }
                    .filter { cubingService.getCompetitorList(it.alias).map { it.competitor.name }.contains(rawMsg) }
                    .map { it.name }
            if (competitionNameList.isEmpty()) {
                bot.sendGroupMsg(groupId, "${rawMsg}近期没有报名比赛")
                return MESSAGE_BLOCK
            }
            bot.sendGroupMsg(groupId, "${rawMsg}报名了以下比赛\n" + competitionNameList.joinToString("\n"))
            return MESSAGE_BLOCK
        }
        return super.onGroupMessage(bot, event)
    }
}