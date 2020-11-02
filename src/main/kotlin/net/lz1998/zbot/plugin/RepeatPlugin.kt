package net.lz1998.zbot.plugin

import net.lz1998.pbbot.alias.GroupMessageEvent
import net.lz1998.pbbot.bot.Bot
import net.lz1998.pbbot.bot.BotPlugin
import net.lz1998.zbot.aop.annotations.SwitchFilter
import org.springframework.stereotype.Component
import java.util.*
import java.util.concurrent.ConcurrentHashMap


@Component
@SwitchFilter("复读")
class RepeatPlugin : BotPlugin() {
    var lastMsgMap: MutableMap<Long, String> = ConcurrentHashMap()
    var lastRepeatTimeMap: MutableMap<Long, Long> = ConcurrentHashMap()
    override fun onGroupMessage(bot: Bot, event: GroupMessageEvent): Int {
        val msg = event.messageList
        val rawMsg = event.rawMessage
        val groupId: Long = event.groupId
        if (rawMsg.startsWith(".")) {
            // 正常指令忽略
            return MESSAGE_IGNORE
        }
        val lastRepeatTime = lastRepeatTimeMap.getOrDefault(groupId, 0L)
        if (System.currentTimeMillis() - lastRepeatTime < 300 * 1000L) {
            // 最短300秒复读一次
            return MESSAGE_IGNORE
        }
        val lastMsg = lastMsgMap.getOrDefault(groupId, "<null/>")
        if (lastMsg != rawMsg) {
            lastMsgMap[groupId] = rawMsg
            return MESSAGE_IGNORE
        } else if (rawMsg.length < 50 && random.nextInt(100) % 10 == 0) {
            lastRepeatTimeMap[groupId] = System.currentTimeMillis()
            bot.sendGroupMsg(groupId, msg)
            return MESSAGE_IGNORE
        }
        return MESSAGE_IGNORE
    }

    companion object {
        private val random = Random()
    }
}