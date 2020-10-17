package net.lz1998.zbot.plugin

import net.lz1998.pbbot.alias.GroupMessageEvent
import net.lz1998.pbbot.bot.Bot
import net.lz1998.pbbot.bot.BotPlugin
import net.lz1998.zbot.aop.annotations.PrefixFilter
import net.lz1998.zbot.aop.annotations.SwitchFilter
import net.lz1998.zbot.service.CubingService
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Component
import java.text.SimpleDateFormat
import java.util.*

@Component
@SwitchFilter("comp")
class CompPlugin : BotPlugin() {

    @Autowired
    lateinit var cubingService: CubingService

    @PrefixFilter(".")
    override fun onGroupMessage(bot: Bot, event: GroupMessageEvent): Int {
        val groupId = event.groupId
        var rawMsg = event.rawMessage

        if ("comp" == rawMsg) { // 赛事列表
            val competitionList = cubingService.getCompetitionList().filter { System.currentTimeMillis() < it.date.to * 1000L }
            if (competitionList.isEmpty()) {
                bot.sendGroupMsg(groupId, "近期没有赛事")
                return MESSAGE_BLOCK
            }
            var retMsg = "近期赛事"
            competitionList.forEachIndexed { index, competition ->
                retMsg += "\n${index + 1}.${competition.name}"
            }
            retMsg += "\n回复.comp 编号 查看详情"
            bot.sendGroupMsg(groupId, retMsg)
            return MESSAGE_BLOCK
        }

        if (rawMsg.startsWith("comp")) {
            rawMsg = rawMsg.substring("comp".length).trim()
            val index = rawMsg.toIntOrNull()
            if (index == null) {
                bot.sendGroupMsg(groupId, "编号错误")
                return MESSAGE_BLOCK
            }
            val competitionList = cubingService.getCompetitionList()
            val competition = competitionList.getOrNull(index - 1)
            if (competition == null) {
                bot.sendGroupMsg(groupId, "赛事不存在")
                return MESSAGE_BLOCK
            } else {
                var retMsg = competition.name
                retMsg += "\n报名人数：${competition.registeredCompetitors}/${competition.competitorLimit}"
                retMsg += "\n时间：${compTimeFormat(competition.date.from)}"
                if (competition.date.from != competition.date.to) {
                    retMsg += "~${compTimeFormat(competition.date.to)}"
                }
                competition.locations.forEach {
                    retMsg += "\n地点：${it.province} ${it.city} ${it.venue}"
                }
                retMsg += "\n粗饼：https://cubingchina.com${competition.url}"
                bot.sendGroupMsg(groupId, retMsg)
                return MESSAGE_BLOCK
            }

        }

        return MESSAGE_IGNORE
    }

    private fun compTimeFormat(time: Int): String {
        return SimpleDateFormat("yyyy-MM-dd").format(Date(time * 1000L))
    }
}