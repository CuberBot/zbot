package net.lz1998.zbot.plugin

import kotlinx.serialization.decodeFromString
import kotlinx.serialization.json.Json
import net.lz1998.pbbot.alias.GroupMessageEvent
import net.lz1998.pbbot.bot.Bot
import net.lz1998.pbbot.bot.BotPlugin
import net.lz1998.zbot.aop.annotations.SwitchFilter
import net.lz1998.zbot.config.ServiceConfig
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.http.HttpEntity
import org.springframework.http.HttpHeaders
import org.springframework.http.MediaType
import org.springframework.stereotype.Component
import org.springframework.util.LinkedMultiValueMap
import org.springframework.util.MultiValueMap
import org.springframework.web.client.RestTemplate


@Component
@SwitchFilter("nsfw")
class NsfwPlugin : BotPlugin() {
    @Autowired
    lateinit var restTemplate: RestTemplate
    var json: Json = Json {
        isLenient = true
        ignoreUnknownKeys = true
    }

    /**
     * 收到群消息时调用此方法
     *
     * @param bot    机器人对象
     * @param event 事件内容
     * @return 是否继续处理下一个插件, MESSAGE_BLOCK表示不继续，MESSAGE_IGNORE表示继续
     */
    override fun onGroupMessage(bot: Bot, event: GroupMessageEvent): Int {
        for (m in event.messageList) {
            if (m.type != "image") {
                continue
            }
            val headers = HttpHeaders()
            headers.contentType = MediaType.MULTIPART_FORM_DATA

            val map: MultiValueMap<String, String> = LinkedMultiValueMap()
            map.add("url", m.dataMap["url"])

            val request: HttpEntity<MultiValueMap<String, String>> =
                HttpEntity<MultiValueMap<String, String>>(map, headers)
            val respStr = restTemplate.postForObject(
                "http://${ServiceConfig.nsfw}",
                request,
                String::class.java
            )
            val resp = json.decodeFromString<HashMap<String, Double>>(respStr.orEmpty());
            val hentai = (resp["hentai"] ?: 0.0) * 100
            if (hentai > 20.0) {
                bot.sendGroupMsg(event.groupId, "hso\uD83D\uDE2E hentai detect:${String.format("%.2f", hentai)}%")
                return MESSAGE_BLOCK
            }
        }
        return MESSAGE_IGNORE
    }
}