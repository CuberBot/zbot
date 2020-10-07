package net.lz1998.zbot.service

import net.lz1998.zbot.utils.isSuperAdmin
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.security.core.context.SecurityContextHolder
import org.springframework.stereotype.Service

@Service
class AdminService {
    @Autowired
    lateinit var zbotService: ZbotService

    fun isWebSuperAdmin(): Boolean {
        val userId = SecurityContextHolder.getContext().authentication.principal as Long
        return isSuperAdmin(userId)
    }

    fun isWebGroupOwner(groupId: Long): Boolean {
        val userId = SecurityContextHolder.getContext().authentication.principal as Long
        val bot = zbotService.getBotInstance(groupId) ?: return false
        val memberInfo = bot.getGroupMemberInfo(groupId, userId, true)
        return isSuperAdmin(userId) || memberInfo?.role == "OWNER"
    }

    fun isWebGroupAdmin(groupId: Long): Boolean {
        val userId = SecurityContextHolder.getContext().authentication.principal as Long
        val bot = zbotService.getBotInstance(groupId) ?: return false
        val memberInfo = bot.getGroupMemberInfo(groupId, userId, true)
        return isSuperAdmin(userId) || memberInfo?.role == "OWNER" || memberInfo?.role == "ADMIN"
    }
}