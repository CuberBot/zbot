package net.lz1998.zbot.plugin

import net.lz1998.pbbot.alias.GroupMessageEvent
import net.lz1998.pbbot.bot.Bot
import net.lz1998.pbbot.bot.BotPlugin
import net.lz1998.zbot.aop.annotations.PrefixFilter
import net.lz1998.zbot.aop.annotations.SwitchFilter
import org.springframework.stereotype.Component
import java.util.*

@Component
@SwitchFilter("sign")
class SignPlugin : BotPlugin() {
    var day: Int = -1
    var signedMap = mutableMapOf<Long, Boolean>()

    @PrefixFilter(".")
    override fun onGroupMessage(bot: Bot, event: GroupMessageEvent): Int {
        val rawMsg = event.rawMessage.toLowerCase()
        if (rawMsg.startsWith("打卡")) {
            val today = Calendar.getInstance().get(Calendar.DAY_OF_WEEK)
            if (today != day) {
                day = today
                signedMap.clear()
            }
            if (signedMap.containsKey(event.groupId)) {
                return MESSAGE_IGNORE
            }
            signedMap[event.groupId] = true
            if (bot.setGroupSignIn(event.groupId) != null) {
                bot.sendGroupMsg(event.groupId, "打卡成功")
            }
            return MESSAGE_BLOCK
        }
        return MESSAGE_IGNORE
    }
}