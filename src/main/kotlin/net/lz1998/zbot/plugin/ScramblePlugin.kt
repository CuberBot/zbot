package net.lz1998.zbot.plugin

import net.lz1998.pbbot.alias.GroupMessageEvent
import net.lz1998.pbbot.bot.Bot
import net.lz1998.pbbot.bot.BotPlugin
import net.lz1998.pbbot.utils.Msg
import net.lz1998.zbot.aop.annotations.PrefixFilter
import net.lz1998.zbot.aop.annotations.SwitchFilter
import net.lz1998.zbot.config.ServiceConfig
import net.lz1998.zbot.enums.SlidysimEnum
import net.lz1998.zbot.enums.TNoodleEnum
import net.lz1998.zbot.service.ScrambleService
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Component
import java.io.IOException
import java.net.URL
import java.net.URLEncoder

@Component
@SwitchFilter("打乱")
class ScramblePlugin : BotPlugin() {

    @Autowired
    lateinit var scrambleService: ScrambleService

    fun getScramble(type: TNoodleEnum): String? {
        var scramble: String = URL("http://${ServiceConfig.tnoodle}/scramble/.txt?=${type.shortName}").readText()
        scramble = scramble.replace("\r", "")
        if (scramble.endsWith("\n")) {
            scramble = scramble.substring(0, scramble.length - 1)
        }
        if ("minx" == type.shortName) {
            scramble = scramble.replace("U' ", "U'\n")
            scramble = scramble.replace("U ", "U\n")
        }
        return scramble
    }


    @PrefixFilter(["."])
    override fun onGroupMessage(bot: Bot, event: GroupMessageEvent): Int {
        val groupId = event.groupId
        val rawMsg = event.rawMessage

        for (puzzle in TNoodleEnum.values()) {
            if (rawMsg == puzzle.instruction) {
                return try {
                    val scramble = getScramble(puzzle)
                    val imgUrl = "http://${ServiceConfig.tnoodle}/view/${puzzle.shortName}.png?scramble=" + URLEncoder.encode(scramble, "utf-8")
                    Msg.builder()
                            .text("${puzzle.showName}\n${scramble}\n")
                            .image(imgUrl)
                            .sendToGroup(bot, groupId)
                    MESSAGE_BLOCK
                } catch (e: IOException) {
                    e.printStackTrace()
                    bot.sendGroupMsg(groupId, "获取打乱失败")
                    MESSAGE_BLOCK
                }
            }
        }

        for (puzzle in SlidysimEnum.values()) {
            if (rawMsg == puzzle.instruction) {
                return try {
                    val scramble = scrambleService.getScrambleSlidysim(puzzle.n)
                    val retMsg = "${puzzle.showName}\n${scramble}"
                    bot.sendGroupMsg(groupId, retMsg)
                    MESSAGE_BLOCK
                } catch (e: Exception) {
                    e.printStackTrace()
                    val retMsg = "获取打乱失败"
                    bot.sendGroupMsg(groupId, retMsg)
                    MESSAGE_BLOCK
                }
            }
        }

        return MESSAGE_IGNORE
    }
}