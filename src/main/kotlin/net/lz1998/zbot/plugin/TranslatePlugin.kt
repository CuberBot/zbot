package net.lz1998.zbot.plugin

import com.volcengine.model.request.TranslateTextRequest
import com.volcengine.service.translate.impl.TranslateServiceImpl
import net.lz1998.pbbot.alias.GroupMessageEvent
import net.lz1998.pbbot.bot.Bot
import net.lz1998.pbbot.bot.BotPlugin
import net.lz1998.pbbot.utils.Msg
import net.lz1998.zbot.aop.annotations.PrefixFilter
import net.lz1998.zbot.aop.annotations.SwitchFilter
import org.springframework.stereotype.Component

@Component
@SwitchFilter("翻译")
class TranslatePlugin : BotPlugin() {

    val translateService = TranslateServiceImpl.getInstance()!!

    /**
     * 收到群消息时调用此方法
     *
     * @param bot    机器人对象
     * @param event 事件内容
     * @return 是否继续处理下一个插件, MESSAGE_BLOCK表示不继续，MESSAGE_IGNORE表示继续
     */
    @PrefixFilter(".")
    override fun onGroupMessage(bot: Bot, event: GroupMessageEvent): Int {
        var rawMsg = event.rawMessage
        val groupId = event.groupId
        if (rawMsg.startsWith("翻译")) {
            rawMsg = rawMsg.substring("翻译".length)
            if (rawMsg.length < 2) {
                bot.sendGroupMsg(groupId, "格式错误")
                return MESSAGE_BLOCK
            }
            val req = TranslateTextRequest()
            req.targetLanguage = rawMsg.substring(0, 2)
            req.textList = mutableListOf(rawMsg.substring(2).trim())
            val resp = translateService.translateText(req)
            if (resp.translationList == null || resp.translationList.size < 1) {
                bot.sendGroupMsg(groupId, "翻译错误")
                return MESSAGE_BLOCK
            }
            val msg = Msg.builder().reply(event.messageId).text("翻译结果: ${resp.translationList[0].translation}")
            bot.sendGroupMsg(groupId, msg)
            return MESSAGE_BLOCK
        }
        return MESSAGE_IGNORE
    }
}