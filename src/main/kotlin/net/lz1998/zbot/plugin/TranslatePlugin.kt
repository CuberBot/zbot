package net.lz1998.zbot.plugin

import com.volcengine.model.request.TranslateTextRequest
import com.volcengine.service.translate.impl.TranslateServiceImpl
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
@SwitchFilter("翻译")
class TranslatePlugin : BotPlugin() {

    val translateService = TranslateServiceImpl.getInstance()!!
    val supportedLanguages = mutableListOf(
        "zh",
        "en",
        "ja",
        "ko",
        "es",
        "pt",
        "fr",
        "ar",
        "de",
        "hi",
        "id",
        "vi",
        "th",
        "nl",
        "it",
        "tr",
        "ru",
        "pl",
        "fi",
        "my",
        "ro",
        "cs",
        "el",
        "uk",
        "sv",
        "fa",
        "he",
        "ta",
        "te",
        "ml",
        "mr",
        "bn",
        "km",
        "tl",
        "ms",
        "ka",
        "bg",
        "da",
        "no",
        "sk",
        "lo",
        "pa",
        "kn",
        "az",
        "bs",
        "et",
        "hr",
        "gu",
        "lt",
        "mk",
        "mn",
        "sl",
        "ur",
        "lv",
        "af"
    )

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
                bot.sendGroupMsg(groupId, "格式错误: .翻译<语种><内容>\n语种: https://www.volcengine.com/docs/4640/35107")
                return MESSAGE_BLOCK
            }

            // 获取目标语种
            val targetLanguage = rawMsg.substring(0, 2)
            if (!supportedLanguages.contains(targetLanguage)) {
                bot.sendGroupMsg(groupId, "格式错误: .翻译<语种><内容>\n语种: https://www.volcengine.com/docs/4640/35107")
                return MESSAGE_BLOCK
            }

            // 获取所有文本
            val textList = mutableListOf<String>()
            event.messageList.filter { it.type == "text" }.withIndex().forEach { (i, it) ->
                if (i == 0) {
                    textList.add(it.dataMap["text"]?.substring("翻译".length + 2)?.trim() ?: "")
                } else {
                    textList.add(it.dataMap["text"] ?: "")
                }
            }

            // 翻译
            val req = TranslateTextRequest()
            req.targetLanguage = targetLanguage
            req.textList = textList
            val resp = translateService.translateText(req)
            if (resp.translationList == null || resp.translationList.size < textList.size) {
                bot.sendGroupMsg(groupId, "翻译错误")
                return MESSAGE_BLOCK
            }
            var sourceLanguage = "zh"
            if (resp.translationList.size > 0) {
                sourceLanguage = resp.translationList[0].detectedSourceLanguage
            }

            // 贴回内容，图片使用controller传参翻译
            var i = 0
            val msg = Msg.builder().reply(event.messageId).text("翻译结果:\n")
            event.messageList.forEach {
                when (it.type) {
                    "text" -> {
                        if (i < resp.translationList.size) {
                            msg.text(resp.translationList[i++].translation)
                        } else {
                            msg.text("")
                        }
                    }
                    "image" -> {
                        msg.image(
                            "http://${ServiceConfig.self}/translate/image?sourceLanguage=$sourceLanguage&targetLanguage=$targetLanguage&url=${
                                URLEncoder.encode(
                                    it.dataMap["url"]
                                )
                            }"
                        )
                    }
                    else -> msg.messageChain.add(it)
                }
            }

            bot.sendGroupMsg(groupId, msg)
            return MESSAGE_BLOCK
        }
        return MESSAGE_IGNORE
    }
}