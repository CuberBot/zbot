package net.lz1998.zbot.plugin

import net.lz1998.pbbot.alias.GroupMessageEvent
import net.lz1998.pbbot.bot.Bot
import net.lz1998.pbbot.bot.BotPlugin
import net.lz1998.pbbot.utils.Msg
import net.lz1998.zbot.aop.annotations.PrefixFilter
import net.lz1998.zbot.aop.annotations.SwitchFilter
import net.lz1998.zbot.config.ServiceConfig
import net.lz1998.zbot.enums.TNoodle
import net.lz1998.zbot.service.ScrambleService
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Component
import java.io.IOException
import java.net.URL
import java.net.URLEncoder

@Component
@SwitchFilter("中文打乱")
class Scramble : BotPlugin() {

    fun getScramble(type: TNoodle): String? {
        var scramble: String = URL("http://${ServiceConfig.tnoodle}/scramble/.txt?=${type.shortName}").readText()
        scramble = scramble.replace("\r", "")
        if (scramble.endsWith("\n")) {
            scramble = scramble.substring(0, scramble.length - 1)
        }
        return scramble
    }

    @PrefixFilter(".")
    override fun onGroupMessage(bot: Bot, event: GroupMessageEvent): Int {
        val groupId = event.groupId
        val rawMsg = event.rawMessage

        for (puzzle in TNoodle.values()) {
            if (rawMsg == puzzle.instruction) {
                return try {
                    val scramble = getScramble(puzzle)
                    var convert = scramble
                    convert = convert?.replace("3Fw'","三提")
                    convert = convert?.replace("Fw'","双提")
                    convert = convert?.replace("F'","提")
                    convert = convert?.replace("3Fw2","三顺顺")
                    convert = convert?.replace("Fw2","双顺顺")
                    convert = convert?.replace("F2","顺顺")
                    convert = convert?.replace("3Fw","三顺")
                    convert = convert?.replace("Fw","双顺")
                    convert = convert?.replace("F","顺")
                    convert = convert?.replace("3Bw'","三右")
                    convert = convert?.replace("Bw'","双右")
                    convert = convert?.replace("B'","右")
                    convert = convert?.replace("3Bw2","三左左")
                    convert = convert?.replace("Bw2","双左左")
                    convert = convert?.replace("B2","左左")
                    convert = convert?.replace("3Bw","三左")
                    convert = convert?.replace("Bw","双左")
                    convert = convert?.replace("B","左")
                    convert = convert?.replace("3Uw'","三回")
                    convert = convert?.replace("Uw'","双回")
                    convert = convert?.replace("U'","回")
                    convert = convert?.replace("3Uw2","三勾勾")
                    convert = convert?.replace("Uw2","双勾勾")
                    convert = convert?.replace("U2","勾勾")
                    convert = convert?.replace("3Uw","三勾")
                    convert = convert?.replace("Uw","双勾")
                    convert = convert?.replace("U","勾")
                    convert = convert?.replace("3Dw'","三旋")
                    convert = convert?.replace("Dw'","双旋")
                    convert = convert?.replace("D'","旋")
                    convert = convert?.replace("3Dw2","三拨拨")
                    convert = convert?.replace("Dw2","双拨拨")
                    convert = convert?.replace("D2","拨拨")
                    convert = convert?.replace("3Dw","三拨")
                    convert = convert?.replace("Dw","双拨")
                    convert = convert?.replace("D","拨")
                    convert = convert?.replace("3Rw'","三下")
                    convert = convert?.replace("Rw'","双下")
                    convert = convert?.replace("R'","下")
                    convert = convert?.replace("3Rw2","三上上")
                    convert = convert?.replace("Rw2","双上上")
                    convert = convert?.replace("R2","上上")
                    convert = convert?.replace("3Rw","三上")
                    convert = convert?.replace("Rw","双上")
                    convert = convert?.replace("R","上")
                    convert = convert?.replace("3Lw'","三推")
                    convert = convert?.replace("Lw'","双推")
                    convert = convert?.replace("L'","推")
                    convert = convert?.replace("3Lw2","三拉拉")
                    convert = convert?.replace("Lw2","双拉拉")
                    convert = convert?.replace("L2","拉拉")
                    convert = convert?.replace("3Lw","三拉")
                    convert = convert?.replace("Lw","双拉")
                    convert = convert?.replace("L","拉")

                    val imgUrl = "http://${ServiceConfig.tnoodle}/view/${puzzle.shortName}.png?scramble=" + URLEncoder.encode(scramble, "utf-8")
                    Msg.builder()
                            .text("${puzzle.showName}\n${convert}\n")
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

        return MESSAGE_IGNORE
    }
}