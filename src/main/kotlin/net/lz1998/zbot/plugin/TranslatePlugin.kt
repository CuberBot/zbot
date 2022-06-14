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
        "af",
        "lzh"
    )

    val reg=Regex("^翻译(([a-z]{2,3}):)?([a-z]{2,3})(.+)")

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
            val result=reg.matchEntire(rawMsg);
            if(result==null){
                bot.sendGroupMsg(groupId, "格式错误: .翻译<语种><内容>\n语种: https://www.volcengine.com/docs/4640/35107")
                return MESSAGE_BLOCK
            }
            val (_,_,sourceLanguage,targetLanguage,text)=result.groupValues
            if (!supportedLanguages.contains(targetLanguage)){
                bot.sendGroupMsg(groupId, "格式错误: .翻译<语种><内容>\n语种: https://www.volcengine.com/docs/4640/35107")
                return MESSAGE_BLOCK
            }
            if (sourceLanguage.isNotEmpty() &&!supportedLanguages.contains(sourceLanguage)){
                bot.sendGroupMsg(groupId, "格式错误: .翻译<语种><内容>\n语种: https://www.volcengine.com/docs/4640/35107")
                return MESSAGE_BLOCK
            }
            // 翻译
            val req = TranslateTextRequest()
            req.sourceLanguage = sourceLanguage
            req.targetLanguage = targetLanguage
            req.textList = listOf(text)
            val resp = translateService.translateText(req)
            if (resp.translationList == null || resp.translationList.size < 1) {
                bot.sendGroupMsg(groupId, "翻译错误")
                return MESSAGE_BLOCK
            }

            // 贴回内容，图片使用controller传参翻译
            var i = 0
            val msg = Msg.builder().text("翻译结果:\n")
            event.messageList.forEach {
                when (it.type) {
                    "text" -> {
                        if (i < resp.translationList.size) {
                            msg.text(resp.translationList[i++].translation)
                        } else {
                            msg.text("")
                        }
                    }
//                    "image" -> {
//                        msg.image(
//                            "http://${ServiceConfig.self}/translate/image?sourceLanguage=$sourceLanguage&targetLanguage=$targetLanguage&url=${
//                                URLEncoder.encode(
//                                    it.dataMap["url"]
//                                )
//                            }"
//                        )
//                    }
                    else -> msg.messageChain.add(it)
                }
            }

            bot.sendGroupMsg(groupId, msg)
            return MESSAGE_BLOCK
        }
        return MESSAGE_IGNORE
    }
}