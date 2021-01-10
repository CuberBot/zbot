package net.lz1998.zbot.plugin

import net.lz1998.pbbot.alias.GroupMessageEvent
import net.lz1998.pbbot.bot.Bot
import net.lz1998.pbbot.bot.BotPlugin
import net.lz1998.pbbot.utils.Msg
import net.lz1998.zbot.aop.annotations.PrefixFilter
import net.lz1998.zbot.aop.annotations.SwitchFilter
import net.lz1998.zbot.config.ServiceConfig
import net.lz1998.zbot.enums.TNos
import org.springframework.stereotype.Component
import java.io.IOException
import java.net.URL

@Component
@SwitchFilter("多打乱")
class TnosPlugin: BotPlugin() {
    fun getScramble(type: TNos): String? {
        var scramble: String = URL("http://${ServiceConfig.tnoodle}/scramble/.txt?=${type.shortName}").readText()
        scramble = scramble.replace("\r\n", "##")
        if (scramble.endsWith("\n")) {
            scramble = scramble.substring(0, scramble.length - 1)
        }
        return scramble
    }

    @PrefixFilter(".")
    override fun onGroupMessage(bot: Bot, event: GroupMessageEvent): Int {
        val groupId = event.groupId
        val rawMsg = event.rawMessage
        val doSth = rawMsg.split("*")
        var count = 0
        if(doSth.size == 2){
            if (doSth[1].toInt()> 5){
                val sMsg = doSth[0].trim() + "*5"
                for (puzzle in TNos.values()) {
                    if (sMsg == puzzle.instruction) {
                        return try {
                            val scramble = getScramble(puzzle)
                            val split = scramble?.split("##")?: arrayListOf(scramble)
                            val resultBuilder = StringBuilder()
                            val firstItem = split[0]
                            val iterator = split.iterator()
                            while(iterator.hasNext()) {
                                val nextItems = iterator.next()
                                if (nextItems == "") {
                                    break
                                } else {
                                    resultBuilder.append("\n").append(count+1).append(".").append("  ").append(nextItems)
                                }
                                if (++count > 4 && (nextItems != firstItem)) {
                                    break
                                }
                            }
                            val result = resultBuilder.toString()
                            Msg.builder()
                                .text("${puzzle.showName}${result}")
                                .sendToGroup(bot, groupId)
                            return MESSAGE_BLOCK
                        } catch (e: IOException) {
                            e.printStackTrace()
                            bot.sendGroupMsg(groupId, "获取打乱失败")
                            MESSAGE_BLOCK
                        }
                    }
                }
                return MESSAGE_BLOCK
            }else if (doSth[1].toInt() < 2){
                bot.sendGroupMsg(groupId,"获取打乱失败")
                return MESSAGE_BLOCK
            }else{
                for (puzzle in TNos.values()) {
                    if (rawMsg == puzzle.instruction) {
                        return try {
                            val scramble = getScramble(puzzle)
                            val split = scramble?.split("##")?: arrayListOf(scramble)
                            val resultBuilder = StringBuilder()
                            val firstItem = split[0]
                            val iterator = split.iterator()
                            while(iterator.hasNext()) {
                                val nextItems = iterator.next()
                                if (nextItems == "") {
                                    break
                                } else {
                                    resultBuilder.append("\n").append(count+1).append(".").append("  ").append(nextItems)
                                }
                                if (++count > 4 && (nextItems != firstItem)) {
                                    break
                                }
                            }
                            val result = resultBuilder.toString()
                            Msg.builder()
                                .text("${puzzle.showName}${result}")
                                .sendToGroup(bot, groupId)
                            return MESSAGE_BLOCK
                        } catch (e: IOException) {
                            e.printStackTrace()
                            bot.sendGroupMsg(groupId, "获取打乱失败")
                            MESSAGE_BLOCK
                        }
                    }
                }
            }
        }else{
            return MESSAGE_IGNORE
        }
        return MESSAGE_IGNORE
    }
}