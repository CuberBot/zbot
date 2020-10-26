package net.lz1998.zbot.service

import net.lz1998.zbot.config.ZbotConfig
import onebot.OnebotEvent
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Component

fun isSuperAdmin(userId: Long): Boolean = ZbotConfig.superAdminList.contains(userId)
fun isGroupOwner(sender: OnebotEvent.GroupMessageEvent.Sender): Boolean = ZbotConfig.superAdminList.contains(sender.userId) || sender.role.toLowerCase() == "owner"
fun isAdmin(sender: OnebotEvent.GroupMessageEvent.Sender): Boolean = ZbotConfig.superAdminList.contains(sender.userId) || sender.role.toLowerCase() == "admin" || sender.role.toLowerCase() == "owner"

@Component
class PermissionService {
    @Autowired
    lateinit var zbotService: ZbotService

    fun isGroupOwner(groupId: Long, userId: Long): Boolean {
        if (ZbotConfig.superAdminList.contains(userId)) {
            return true
        }
        val bot = zbotService.getBotInstance(groupId) ?: return false
        val member = bot.getGroupMemberInfo(groupId, userId, true) ?: return false
        val role = member.role.toLowerCase()
        return role == "owner"
    }

    fun isGroupAdmin(groupId: Long, userId: Long): Boolean {
        if (ZbotConfig.superAdminList.contains(userId)) {
            return true
        }
        val bot = zbotService.getBotInstance(groupId) ?: return false
        val member = bot.getGroupMemberInfo(groupId, userId, true) ?: return false
        val role = member.role.toLowerCase()
        return role == "owner" || role == "admin"
    }
}