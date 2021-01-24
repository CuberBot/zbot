package net.lz1998.zbot.plugin

import net.lz1998.pbbot.alias.GroupMessageEvent
import net.lz1998.pbbot.bot.Bot
import net.lz1998.pbbot.bot.BotPlugin
import net.lz1998.zbot.aop.annotations.PrefixFilter
import net.lz1998.zbot.aop.annotations.SwitchFilter
import net.lz1998.zbot.service.PermissionService
import net.lz1998.zbot.service.isAdmin
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Component
import java.lang.StringBuilder

// 禁言 + 踢人功能
@Component
@SwitchFilter("群管")
class AdminPlugin : BotPlugin() {

    @Autowired
    lateinit var permissionService: PermissionService


    @PrefixFilter(".")
    override fun onGroupMessage(bot: Bot, event: GroupMessageEvent): Int {
        val groupId = event.groupId
        var rawMsg = event.rawMessage.trim()

        rawMsg = rawMsg.replace("<at qq=\"", "")
        rawMsg = rawMsg.replace("\"/>", "")
        rawMsg = rawMsg.replace("  ", " ")
        rawMsg = rawMsg.replace("   ", " ")

        if (rawMsg.startsWith("ban") && isAdmin(event.sender)) {
            if (permissionService.isGroupAdmin(groupId, bot.selfId)) {
                rawMsg = rawMsg.substring("ban".length).trim()
                val split = rawMsg.split(" ")
                val banId = split[0].trim().toLong()
                var day = 0
                var hour = 0
                var minute = 0
                var second = 0
                val duration: Int
                if (split.size > 1) {
                    val str = split[1].trim().replace("天", "d").replace("小时", "h").replace("分钟", "m").replace("秒", "s")
                    val sendStr = StringBuilder()
                    val match = Regex("(\\d)([dhms])")
                    match.findAll(str).forEach {
                        if (it.value.endsWith("d")) {
                            sendStr.append(it.value.replace("d", "")).append("天")
                            day = it.value.replace("d", "").toInt() * 24 * 60 * 60
                        } else if (it.value.endsWith("h")) {
                            sendStr.append(it.value.replace("h", "")).append("小时")
                            hour = it.value.replace("h", "").toInt() * 60 * 60
                        } else if (it.value.endsWith("m")) {
                            sendStr.append(it.value.replace("m", "")).append("分钟")
                            minute = it.value.replace("m", "").toInt() * 60
                        } else if (it.value.endsWith("s")) {
                            sendStr.append(it.value.replace("s", "")).append("秒")
                            second = it.value.replace("s", "").toInt()
                        } else {
                            sendStr.append(it.value).append("秒")
                            second = it.value.toInt()
                        }
                    }
                    duration = day + hour + minute + second
                    if (!permissionService.isGroupAdmin(groupId, banId)) {
                        if (duration > 0 && duration < 2591941) {
                            bot.setGroupBan(groupId, banId, duration)
                            bot.sendGroupMsg(
                                    groupId,
                                    "已禁言<at qq=\"" + banId.toString() + "\"/> " + sendStr
                            )
                        } else if (duration == 0) {
                            bot.setGroupBan(groupId, banId, 0)
                            bot.sendGroupMsg(groupId, "已解除<at qq=\"" + banId.toString() + "\"/> 的禁言")
                        } else {
                            bot.sendGroupMsg(groupId, "禁言时间超过允许范围")
                        }
                    } else if (permissionService.isGroupAdmin(groupId, banId)) {
                        bot.sendGroupMsg(groupId, "无法禁言管理员")
                    } else {
                        bot.sendGroupMsg(groupId, "该成员不在群里")
                    }
                }
                bot.sendGroupMsg(groupId, "禁言格式错误")
            } else {
                bot.sendGroupMsg(groupId, "需要管理权限")
            }
            return MESSAGE_BLOCK
        } else if ((rawMsg.startsWith("t") || rawMsg.startsWith("T")) && isAdmin(event.sender)) {
            if (permissionService.isGroupAdmin(groupId, bot.selfId)) {
                val rejectAddAgain = rawMsg.startsWith("T")
                rawMsg = rawMsg.replace("T", "t").substring("t".length).trim()
                val split = rawMsg.split(" ")
                val banId = split[0].trim().toLong()
                if (!permissionService.isGroupAdmin(groupId, banId)) {
                    bot.sendGroupMsg(groupId, "踢出 " + banId.toString() + " 成功")
                    bot.setGroupKick(groupId, banId, rejectAddAgain)
                } else if (permissionService.isGroupAdmin(groupId, banId)) {
                    bot.sendGroupMsg(groupId, "无法踢出管理员")
                } else {
                    bot.sendGroupMsg(groupId, "该成员不在群里")
                }
            } else {
                bot.sendGroupMsg(groupId, "需要管理权限")
            }
            return MESSAGE_BLOCK
        }
        return MESSAGE_IGNORE
    }
}
