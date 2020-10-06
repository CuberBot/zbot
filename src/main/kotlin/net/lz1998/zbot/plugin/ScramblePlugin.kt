@file:Suppress("unused")

package net.lz1998.zbot.plugin

import net.lz1998.pbbot.alias.GroupMessageEvent
import net.lz1998.pbbot.bot.Bot
import net.lz1998.pbbot.bot.BotPlugin
import net.lz1998.pbbot.utils.Msg
import net.lz1998.zbot.aop.annotations.PrefixFilter
import net.lz1998.zbot.aop.annotations.SwitchFilter
import net.lz1998.zbot.enums.SlidysimEnum
import net.lz1998.zbot.enums.TNoodleEnum
import net.lz1998.zbot.service.ScrambleService
import net.lz1998.zbot.utils.HttpUtil
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.beans.factory.annotation.Value
import org.springframework.stereotype.Component
import org.springframework.web.client.RestTemplate
import java.io.IOException
import java.net.URLEncoder

@Component
@SwitchFilter("打乱")
class ScramblePlugin : BotPlugin() {

    @Autowired
    lateinit var scrambleService: ScrambleService

    @Value("\${zbot.tnoodle}")
    var DOMAIN: String = ""

    @Autowired
    lateinit var restTemplate: RestTemplate

    fun getScramble(type: String): String? {
        val url = "http://$DOMAIN/scramble/.txt?=$type"
        var scramble: String = HttpUtil.getString(url)
        scramble = scramble.replace("\r", "")
        if (scramble.endsWith("\n")) {
            scramble = scramble.substring(0, scramble.length - 1)
        }
        if ("minx" == type) {
            scramble = scramble.replace("U' ", "U'\n")
            scramble = scramble.replace("U ", "U\n")
        }
        return scramble
    }


    @PrefixFilter(".")
    override fun onGroupMessage(bot: Bot, event: GroupMessageEvent): Int {
        val groupId = event.groupId
        val rawMsg = event.rawMessage

        for (puzzle in TNoodleEnum.values()) {
            if (rawMsg == puzzle.instruction) {
                return try {
                    val scramble = getScramble(puzzle.shortName)
                    val imgUrl = "http://" + DOMAIN + "/view/" + puzzle.shortName + ".png?scramble=" + URLEncoder.encode(scramble, "utf-8")
                    val retmsg = Msg.builder().text("${puzzle.showName}\n${scramble}\n").image(imgUrl)
                    bot.sendGroupMsg(groupId, retmsg, false)
                    MESSAGE_BLOCK
                } catch (e: IOException) {
                    e.printStackTrace()
                    val retmsg = "获取打乱失败"
                    bot.sendGroupMsg(groupId, retmsg, false)
                    MESSAGE_BLOCK
                }
            }
        }

        for (puzzle in SlidysimEnum.values()) {
            if (rawMsg == puzzle.instruction) {
                return try {
                    val scramble = scrambleService.getScrambleSlidysim(puzzle.n)
                    val retMsg = "${puzzle.showName}\n${scramble}"
                    bot.sendGroupMsg(groupId, retMsg, false)
                    MESSAGE_BLOCK
                } catch (e: Exception) {
                    e.printStackTrace()
                    val retMsg = "获取打乱失败"
                    bot.sendGroupMsg(groupId, retMsg, false)
                    MESSAGE_BLOCK
                }
            }
        }

        return MESSAGE_IGNORE
    }
}