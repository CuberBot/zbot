package net.lz1998.zbot.plugin

import net.lz1998.pbbot.alias.GroupMessageEvent
import net.lz1998.pbbot.bot.Bot
import net.lz1998.pbbot.bot.BotPlugin
import net.lz1998.zbot.aop.annotations.PrefixFilter
import net.lz1998.zbot.aop.annotations.SwitchFilter
import net.lz1998.zbot.service.LearnService
import net.lz1998.zbot.utils.isAdmin
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Component

@Component
@SwitchFilter("学习")
class LearnPlugin : BotPlugin() {

    @Autowired
    lateinit var learnService: LearnService

    @PrefixFilter(["."])
    override fun onGroupMessage(bot: Bot, event: GroupMessageEvent): Int {
        val groupId = event.groupId
        val userId = event.userId

        var rawMsg = event.rawMessage.trim()
        rawMsg = rawMsg.replace("问：", "问:")
        rawMsg = rawMsg.replace("答：", "答:")

        if (rawMsg.startsWith("问:") && isAdmin(event.sender)) {
            rawMsg = rawMsg.substring("问:".length).trim()
            val split = rawMsg.split("答:")
            if (split.size < 2) {
                bot.sendGroupMsg(groupId, "格式错误")
                return MESSAGE_BLOCK
            }
            val ask = split[0].trim() // 匹配内容必须 trim 否则 MySQL 会有问题
            val answer = split[1]
            if (ask.length > 100) {
                bot.sendGroupMsg(groupId, "问 长度超过最大限制")
                return MESSAGE_BLOCK
            }
            if (answer.length > 2000) {
                bot.sendGroupMsg(groupId, "答 长度超过最大限制")
                return MESSAGE_BLOCK
            }

            // TODO 审核

            learnService.setAnswer(groupId = groupId, ask = ask, answer = answer, adminId = userId)
            bot.sendGroupMsg(groupId, "已学会\n测试阶段数据可能(一定)会丢失") // TODO 测试阶段
            return MESSAGE_BLOCK
        }

        var answer = learnService.getAnswer(groupId = groupId, ask = rawMsg)
        if (answer.toLowerCase() != "<null/>") {
            answer = answer.replace("{{userId}}", userId.toString())
            bot.sendGroupMsg(groupId, answer)
            return MESSAGE_BLOCK
        }

        return MESSAGE_IGNORE
    }
}