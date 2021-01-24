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
            val match = Regex("(\\d)([dhms])")
            var duration = 0
            match.findAll(str).forEach {
                // FIXME 这里转换toInt 有一定风险 abcd，"abc".toInt() 需要改为toIntOrNull
                if (it.value.endsWith("d")) {
                    duration += it.value.replace("d", "").toInt() * 24 * 60 * 60
                } else if (it.value.endsWith("h")) {
                    duration += it.value.replace("h", "").toInt() * 60 * 60
                } else if (it.value.endsWith("m")) {
                    duration += it.value.replace("m", "").toInt() * 60
                } else if (it.value.endsWith("s")) {
                    duration += it.value.replace("s", "").toInt()
                } else {
                    duration += it.value.toInt()
                }
            }
            if (duration <= 0) {
                bot.setGroupBan(groupId, banId, 0)
                Msg.builder().text("已解除").at(banId).text("的禁言").sendToGroup(bot, groupId)
                return MESSAGE_BLOCK
            }

            // TODO 时间计算 duration -> 时间长度字符串
            return if (duration in 1..(30 * 24 * 60 * 60)) {
                bot.setGroupBan(groupId, banId, duration)
                Msg.builder().text("已禁言").at(banId).sendToGroup(bot, groupId)
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
}
