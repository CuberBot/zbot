package net.lz1998.zbot.plugin

import net.lz1998.pbbot.alias.GroupMessageEvent
import net.lz1998.pbbot.bot.Bot
import net.lz1998.pbbot.bot.BotPlugin
import net.lz1998.pbbot.utils.Msg
import net.lz1998.zbot.aop.annotations.PrefixFilter
import net.lz1998.zbot.aop.annotations.SwitchFilter
import net.lz1998.zbot.service.isAdmin
import org.springframework.stereotype.Component

// 禁言 + 踢人功能
@Component
@SwitchFilter("群管")
class AdminPlugin : BotPlugin() {

    companion object {
        const val DAY_RATE = 24 * 60 * 60
        const val HOUR_RATE = 60 * 60
        const val MIN_RATE = 60
        const val SEC_RATE = 1
    }

    @PrefixFilter(".")
    override fun onGroupMessage(bot: Bot, event: GroupMessageEvent): Int {
        val groupId = event.groupId
        var rawMsg = event.rawMessage.trim()

        // 预处理 at
        rawMsg = rawMsg.replace("<at qq=\"", "").trim()
        rawMsg = rawMsg.replace("\"/>", " ").trim() // at后留空格用于分割
        while (rawMsg.contains("  ")) {
            rawMsg = rawMsg.replace("  ", " ")
        }

        // 禁言功能
        if (rawMsg.startsWith("ban") && isAdmin(event.sender)) {
            rawMsg = rawMsg.substring("ban".length).trim()
            val split = rawMsg.split(" ", limit = 2)
            val banId = split[0].trim().toLongOrNull() // 如果不可转换成Long，是null，格式错误
            if (split.size < 2 || banId == null) {
                bot.sendGroupMsg(groupId, "禁言格式错误")
                return MESSAGE_BLOCK
            }
            val str = split[1]
                    .replace("天", "d")
                    .replace("小时", "h")
                    .replace("时", "h")
                    .replace("分钟", "m")
                    .replace("分", "m")
                    .replace("秒", "s")
                    .trim()
            var duration = 0
            val match = Regex("(\\d)([dhms])")
            match.findAll(str).forEach {
                val num = it.groupValues[1].toInt()
                val rate = when (it.groupValues[2]) {
                    "d" -> DAY_RATE
                    "h" -> HOUR_RATE
                    "m" -> MIN_RATE
                    "s" -> SEC_RATE
                    else -> 0
                }
                duration += num * rate
            }
            if (duration <= 0) {
                bot.setGroupBan(groupId, banId, 0)
                Msg.builder().text("已解除").at(banId).text("的禁言").sendToGroup(bot, groupId)
                return MESSAGE_BLOCK
            }

            return if (duration < 30 * DAY_RATE) {
                bot.setGroupBan(groupId, banId, duration)
                val day = duration / DAY_RATE
                val hour = (duration % DAY_RATE) / HOUR_RATE
                val min = (duration % HOUR_RATE) / MIN_RATE
                val sec = duration % MIN_RATE
                Msg.builder().text("已禁言").at(banId).text(getTimeStr(day, hour, min, sec)).sendToGroup(bot, groupId)
                MESSAGE_BLOCK
            } else {
                bot.sendGroupMsg(groupId, "禁言时间超过允许范围")
                MESSAGE_BLOCK
            }
        }

        // 踢人功能
        if ((rawMsg.startsWith("t") || rawMsg.startsWith("T")) && isAdmin(event.sender)) {
            val rejectAddAgain = rawMsg.startsWith("T")
            rawMsg = rawMsg.substring("t".length).trim()
            val banId = rawMsg.toLongOrNull()
            if (banId == null) {
                bot.sendGroupMsg(groupId, "格式错误")
                return MESSAGE_BLOCK
            }
            bot.sendGroupMsg(groupId, "踢出 $banId 成功")
            bot.setGroupKick(groupId, banId, rejectAddAgain)
            return MESSAGE_BLOCK
        }
        return MESSAGE_IGNORE
    }

    final fun getTimeStr(day: Int, hour: Int, min: Int, sec: Int): String {
        return (if (day != 0) "${day}天" else "") + (if (hour != 0) "${hour}小时" else "") + (if (min != 0) "${min}分钟" else "") + (if (sec != 0) "${sec}秒" else "")
    }
}