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
                    if (split.size > 1) {
                        val str = split[1].trim()
                        val num: Int
                        if (str.endsWith("m")) {
                            num = str.replace("m", "").toInt() * 60
                        } else if (str.endsWith("h")) {
                            num = str.replace("h", "").toInt() * 60 * 60
                        } else if (str.endsWith("d")) {
                            num = str.replace("d", "").toInt() * 60 * 60 * 24
                        } else {
                            num = str.toInt()
                        }
                        val duration = num
                        if (isGroupMember(groupId, banId)) {
                            if (duration > 0) {
                                if (duration < 2591941) {
                                    bot.setGroupBan(groupId, banId, duration)
                                    if (duration < 60) {
                                        bot.sendGroupMsg(
                                            groupId,
                                            "已禁言<at qq=\"" + banId.toString() + "\"/> " + duration + "秒"
                                        )
                                        return MESSAGE_BLOCK
                                    } else if (duration > 59 && duration < 3600) {
                                        bot.sendGroupMsg(
                                            groupId,
                                            "已禁言<at qq=\"" + banId.toString() + "\"/> " + duration / 60 + "分钟" + duration % 60 + "秒"
                                        )
                                        return MESSAGE_BLOCK
                                    } else if (duration > 3599 && duration < 86400) {
                                        bot.sendGroupMsg(
                                            groupId,
                                            "已禁言<at qq=\"" + banId.toString() + "\"/> " + duration / 3600 + "小时" + duration % 3600 / 60 + "分钟"
                                        )
                                        return MESSAGE_BLOCK
                                    } else if (duration > 86399) {
                                        bot.sendGroupMsg(
                                            groupId,
                                            "已禁言<at qq=\"" + banId.toString() + "\"/> " + duration / 86400 + "天" + duration % 86400 / 3600 + "小时"
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