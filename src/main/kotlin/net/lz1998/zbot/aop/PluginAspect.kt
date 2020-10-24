package net.lz1998.zbot.aop

import net.lz1998.pbbot.alias.GroupIncreaseNoticeEvent
import net.lz1998.pbbot.alias.GroupMessageEvent
import net.lz1998.pbbot.alias.Message
import net.lz1998.pbbot.alias.PrivateMessageEvent
import net.lz1998.pbbot.bot.BotPlugin
import net.lz1998.zbot.aop.annotations.PrefixFilter
import net.lz1998.zbot.aop.annotations.SwitchFilter
import net.lz1998.zbot.service.PluginSwitchService
import org.aspectj.lang.ProceedingJoinPoint
import org.aspectj.lang.annotation.Around
import org.aspectj.lang.annotation.Aspect
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Component

@Aspect
@Component
class PluginAspect {

    @Autowired
    lateinit var pluginSwitchService: PluginSwitchService

    // 这里代码有点乱
    @Around(value = "execution(int net.lz1998.zbot.plugin.*.on*Message(..)) && @annotation(prefixFilter)", argNames = "pjp, prefixFilter")
    fun checkPrefix(pjp: ProceedingJoinPoint, prefixFilter: PrefixFilter): Int {
        val args = pjp.args
        val ignoreCase = prefixFilter.ignoreCase
        var prefixes = prefixFilter.value.toList()
        args.forEachIndexed { index, arg ->
            prefixes.forEach {
                var prefix = it
                if (arg is GroupMessageEvent) {
                    var rawMessage = arg.rawMessage
                    if (ignoreCase) {
                        rawMessage = rawMessage.toLowerCase()
                        prefix = prefix.toLowerCase()
                    }
                    if (!rawMessage.startsWith(prefix)) { // 根据消息前缀过滤
                        return@forEach
                    }
                    val eventBuilder = arg.toBuilder()
                    eventBuilder.putExtra("command", prefix)
                    var firstText = arg.messageList.first().dataMap["text"] ?: return BotPlugin.MESSAGE_IGNORE
                    rawMessage = arg.rawMessage.substring(prefix.length)
                    firstText = firstText.substring(prefix.length)
                    eventBuilder.addMessage(0, Message.newBuilder().setType("text").putData("text", firstText).build())
                    eventBuilder.rawMessage = rawMessage
                    args[index] = eventBuilder.build()
                    return pjp.proceed(args) as Int
                }
                if (arg is PrivateMessageEvent) {
                    var rawMessage = arg.rawMessage
                    if (ignoreCase) {
                        rawMessage = rawMessage.toLowerCase()
                        prefix = prefix.toLowerCase()
                    }
                    if (!rawMessage.startsWith(prefix)) { // 根据消息前缀过滤
                        return@forEach
                    }
                    val eventBuilder = arg.toBuilder()
                    eventBuilder.putExtra("command", prefix)
                    var firstText = arg.messageList.first().dataMap["text"] ?: return BotPlugin.MESSAGE_IGNORE
                    rawMessage = arg.rawMessage.substring(prefix.length)
                    firstText = firstText.substring(prefix.length)
                    eventBuilder.addMessage(0, Message.newBuilder().setType("text").putData("text", firstText).build())
                    eventBuilder.rawMessage = rawMessage
                    args[index] = eventBuilder.build()
                    return pjp.proceed(args) as Int
                }
            }
        }
        return BotPlugin.MESSAGE_IGNORE
    }

    @Around(value = "execution(int net.lz1998.zbot.plugin.*.*(..)) && @within(switchFilter)", argNames = "pjp, switchFilter")
    fun checkSwitch(pjp: ProceedingJoinPoint, switchFilter: SwitchFilter): Int {
        val args = pjp.args
        val pluginName = switchFilter.value
        args.forEachIndexed { _, arg ->
            if (arg is GroupMessageEvent) {
                val groupId = arg.groupId
                val stop = pluginSwitchService.isPluginStop(groupId = groupId, pluginName = pluginName)
                if (stop) {
                    return BotPlugin.MESSAGE_IGNORE
                }
            }
            if (arg is GroupIncreaseNoticeEvent) {
                val groupId = arg.groupId
                val stop = pluginSwitchService.isPluginStop(groupId = groupId, pluginName = pluginName)
                if (stop) {
                    return BotPlugin.MESSAGE_IGNORE
                }
            }
        }
        return pjp.proceed(args) as Int
    }
}