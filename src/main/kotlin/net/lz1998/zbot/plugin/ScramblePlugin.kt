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

    var ins = ""
    var rate = 0

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


    @PrefixFilter(".")
    override fun onGroupMessage(bot: Bot, event: GroupMessageEvent): Int {
        val groupId = event.groupId
        val rawMsg = event.rawMessage

        getInsAndRate(rawMsg)

        for (puzzle in TNoodleEnum.values()) {
            if (ins == puzzle.instruction) {
                return try {
                    if(rate == 1) {
                        val scramble = getScramble(puzzle)
                        val imgUrl =
                            "http://${ServiceConfig.tnoodle}/view/${puzzle.shortName}.png?scramble=" + URLEncoder.encode(
                                scramble,
                                "utf-8"
                            )
                        Msg.builder()
                            .text("${puzzle.showName}\n${scramble}\n")
                            .image(imgUrl)
                            .sendToGroup(bot, groupId)
                    }else if(ins == "2" || ins == "3" || ins == "py" || ins == "sk" || ins == "cl" || ins == "fm"){
                        var sendResult = StringBuilder()
                        for(i in 1..rate) {
                            sendResult = sendResult.append("\n").append(i).append(".  ").append(getScramble(puzzle))
                        }
                        Msg.builder()
                            .text("${puzzle.showName}*$rate${sendResult}")
                            .sendToGroup(bot, groupId)
                    }
                    MESSAGE_BLOCK
                } catch (e: IOException) {
                    e.printStackTrace()
                    bot.sendGroupMsg(groupId, "获取打乱失败")
                    MESSAGE_BLOCK
                }
            }
        }

        for (puzzle in SlidysimEnum.values()) {
            if (ins == puzzle.instruction) {
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
    fun getInsAndRate(str: String) {
        val matchIns = Regex("[A-Za-z0-9]+")
        val matchRate = Regex("([*])([0-9]+)")
        val resultIns = matchIns.find(str)?.value ?: str
        val resultRate = matchRate.find(str)?.value
            ins = resultIns
        if (resultRate != null){
            if(resultRate.substring(1).toInt() > 1 && resultRate.substring(1).toInt() < 6){
                rate = resultRate.substring(1).toInt()
            } else if (resultRate.substring(1).toInt() > 5 ) { rate = 5 }
        }else rate = 1
    }
}
