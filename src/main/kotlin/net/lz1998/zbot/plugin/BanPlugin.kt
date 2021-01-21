package net.lz1998.zbot.plugin

import net.lz1998.pbbot.alias.GroupMessageEvent
import net.lz1998.pbbot.bot.Bot
import net.lz1998.pbbot.bot.BotPlugin
import net.lz1998.zbot.aop.annotations.PrefixFilter
import net.lz1998.zbot.aop.annotations.SwitchFilter
import net.lz1998.zbot.service.ZbotService
import net.lz1998.zbot.service.isAdmin
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Component
import java.lang.StringBuilder

@Component
@SwitchFilter("群管")
class BanPlugin: BotPlugin() {

    @Autowired
    lateinit var zbotService: ZbotService

    @PrefixFilter(".")
    override fun onGroupMessage(bot: Bot, event: GroupMessageEvent): Int {
            val groupId = event.groupId
            var rawMsg = event.rawMessage.trim()

        fun isGroupAdmin(groupId: Long, userId: Long): Boolean {
            val botBan = zbotService.getBotInstance(groupId) ?: return false
            val member = botBan.getGroupMemberInfo(groupId, userId, true) ?: return false
            val role = member.role.toLowerCase()
            return role == "owner" || role == "admin"
        }

        fun isGroupMember(groupId: Long, userId: Long): Boolean {
            val botBan = zbotService.getBotInstance(groupId) ?: return false
            val member = botBan.getGroupMemberInfo(groupId, userId, true) ?: return false
            val role = member.role.toLowerCase()
            return role == "member"
        }

            rawMsg = rawMsg.replace("ban ", "ban")
            rawMsg = rawMsg.replace("<at qq=\"", "")
            rawMsg = rawMsg.replace("\"/>", "")
            rawMsg = rawMsg.replace("  ", " ")
            rawMsg = rawMsg.replace("   ", " ")

            if (rawMsg.startsWith("ban") && isAdmin(event.sender)) {
                if(isGroupAdmin(groupId,bot.selfId)) {
                    rawMsg = rawMsg.substring("ban".length).trim()
                    val split = rawMsg.split(" ")
                    val banId = split[0].trim().toLong()
                    var day = 0
                    var hour = 0
                    var minute = 0
                    var second = 0
                    val duration: Int
                    if (split.size > 1) {
                        val str = split[1].trim().replace("天","d").replace("小时","h").replace("分钟","m").replace("秒","s")
                        val sendStr = StringBuilder()
                        val match = Regex("(\\d)([dhms])")
                        match.findAll(str).forEach {
                            if(it.value.endsWith("d")){
                                sendStr.append(it.value.replace("d","")).append("天")
                                day = it.value.replace("d","").toInt() * 24 * 60 * 60
                            }else if(it.value.endsWith("h")){
                                sendStr.append(it.value.replace("h","")).append("小时")
                                hour = it.value.replace("h","").toInt() * 60 * 60
                            }else if(it.value.endsWith("m")){
                                sendStr.append(it.value.replace("m","")).append("分钟")
                                minute = it.value.replace("m","").toInt() * 60
                            }else if(it.value.endsWith("s")) {
                                sendStr.append(it.value.replace("s","")).append("秒")
                                second = it.value.replace("s","").toInt()
                            }else{
                                sendStr.append(it.value).append("秒")
                                second = it.value.toInt()
                            }
                        }
                        duration = day + hour + minute + second
                        if (isGroupMember(groupId, banId)) {
                            if (duration > 0) {
                                if (duration < 2591941) {
                                    bot.setGroupBan(groupId, banId, duration)
                                    if (duration < 60) {
                                        bot.sendGroupMsg(
                                            groupId,
                                            "已禁言<at qq=\"" + banId.toString() + "\"/> " + sendStr
                                        )
                                        return MESSAGE_BLOCK
                                    } else if (duration > 59 && duration < 3600) {
                                        bot.sendGroupMsg(
                                            groupId,
                                            "已禁言<at qq=\"" + banId.toString() + "\"/> " + sendStr
                                        )
                                        return MESSAGE_BLOCK
                                    } else if (duration > 3599 && duration < 86400) {
                                        bot.sendGroupMsg(
                                            groupId,
                                            "已禁言<at qq=\"" + banId.toString() + "\"/> " + sendStr
                                        )
                                        return MESSAGE_BLOCK
                                    } else if (duration > 86399) {
                                        bot.sendGroupMsg(
                                            groupId,
                                            "已禁言<at qq=\"" + banId.toString() + "\"/> " + sendStr
                                        )
                                        return MESSAGE_BLOCK
                                    }
                                } else {
                                    bot.sendGroupMsg(groupId, "禁言时长超过最大允许范围")
                                    return MESSAGE_BLOCK
                                }
                                return MESSAGE_BLOCK
                            } else {
                                bot.setGroupBan(groupId, banId, 0)
                                bot.sendGroupMsg(groupId, "已解除<at qq=\"" + banId.toString() + "\"/> 的禁言")
                                return MESSAGE_BLOCK
                            }
                        }else if(isGroupAdmin(groupId,banId)){
                            bot.sendGroupMsg(groupId,"无法禁言管理员")
                            return MESSAGE_BLOCK
                        }else{
                            bot.sendGroupMsg(groupId, "该成员不在群里")
                            return MESSAGE_BLOCK
                        }
                    }
                    bot.sendGroupMsg(groupId, "禁言格式错误")
                    return MESSAGE_BLOCK
                }else{
                    bot.sendGroupMsg(groupId,"需要管理权限")
                    return MESSAGE_BLOCK
                }
            }else if (rawMsg.startsWith("t") && isAdmin(event.sender)) {
                if(isGroupAdmin(groupId,bot.selfId)){
                    rawMsg = rawMsg.substring("t".length).trim()
                    val split = rawMsg.split(" ")
                    val banId = split[0].trim().toLong()
                    if (isGroupMember(groupId,banId)) {
                        bot.sendGroupMsg(groupId, "踢出<at qq=\"" + banId.toString() + "\"/> 成功")
                        bot.setGroupKick(groupId, banId, false)
                        return MESSAGE_BLOCK
                    }else if(isGroupAdmin(groupId,banId)){
                        bot.sendGroupMsg(groupId,"无法踢出管理员")
                        return MESSAGE_BLOCK
                    }else{
                        bot.sendGroupMsg(groupId, "该成员不在群里")
                        return MESSAGE_BLOCK
                    }
                }else{
                    bot.sendGroupMsg(groupId,"需要管理权限")
                    return MESSAGE_BLOCK
                }
            }else if (rawMsg.startsWith("T") && isAdmin(event.sender)) {
                if(isGroupAdmin(groupId,bot.selfId)) {
                    rawMsg = rawMsg.substring("T".length).trim()
                    val split = rawMsg.split(" ")
                    val banId = split[0].trim().toLong()
                    if (isGroupMember(groupId, banId)) {
                        bot.sendGroupMsg(
                            groupId,
                            "踢出<at qq=\"" + banId.toString() + "\"/> 成功,已拒收<at qq=\"" + banId.toString() + "\"/> 的入群申请"
                        )
                        bot.setGroupKick(groupId, banId, true)
                        return MESSAGE_BLOCK
                    }else if(isGroupAdmin(groupId,banId)){
                        bot.sendGroupMsg(groupId,"无法踢出管理员")
                        return MESSAGE_BLOCK
                    }else{
                        bot.sendGroupMsg(groupId, "该成员不在群里")
                        return MESSAGE_BLOCK
                    }
                }else{
                    bot.sendGroupMsg(groupId,"需要管理权限")
                    return MESSAGE_BLOCK
                }
            }
            return MESSAGE_IGNORE
    }
}