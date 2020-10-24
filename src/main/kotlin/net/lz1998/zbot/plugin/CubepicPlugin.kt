package net.lz1998.zbot.plugin

import net.lz1998.pbbot.alias.GroupMessageEvent
import net.lz1998.pbbot.bot.Bot
import net.lz1998.pbbot.bot.BotPlugin
import net.lz1998.pbbot.utils.Msg
import net.lz1998.zbot.aop.annotations.PrefixFilter
import net.lz1998.zbot.aop.annotations.SwitchFilter
import net.lz1998.zbot.config.ServiceConfig
import org.springframework.stereotype.Component
import java.net.URLEncoder

@Component
@SwitchFilter("cubepic")
class CubepicPlugin : BotPlugin() {

    @PrefixFilter([".cubepic"])
    override fun onGroupMessage(bot: Bot, event: GroupMessageEvent): Int {
        val groupId = event.groupId
        val rawMsg = event.rawMessage.trim()
        val rows = rawMsg.split("\n")
        var alg = ""
        val paramMap = mutableMapOf<String, String>()
        rows.forEach {
            if (it.contains("=")) {
                val split = it.split("=")
                if (split.size > 1) {
                    paramMap[split[0]] = split[1]
                }
            } else {
                alg = URLEncoder.encode(it, "utf-8")
            }
        }
        val type = paramMap.getOrDefault("type", "alg")
        paramMap.remove("type")
        var imageUrl = "http://${ServiceConfig.vscube}/visualcube.php?fmt=png&${type}=${alg}"
        paramMap.forEach {
            val k = URLEncoder.encode(it.key, "utf-8")
            val v = URLEncoder.encode(it.value, "utf-8")
            imageUrl += "&$k=$v"
        }
        Msg.builder().image(imageUrl).sendToGroup(bot, groupId)
        return MESSAGE_BLOCK
    }

}