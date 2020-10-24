package net.lz1998.zbot.plugin

import net.lz1998.pbbot.alias.GroupMessageEvent
import net.lz1998.pbbot.alias.GroupRequestEvent
import net.lz1998.pbbot.bot.Bot
import net.lz1998.pbbot.bot.BotPlugin
import net.lz1998.zbot.aop.annotations.PrefixFilter
import net.lz1998.zbot.service.AuthService
import net.lz1998.zbot.utils.isSuperAdmin
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Component

@Component
class AuthPlugin : BotPlugin() {
    @Autowired
    lateinit var authService: AuthService

    @PrefixFilter([".授权+", ".授权-", ".授权", "."])
    override fun onGroupMessage(bot: Bot, event: GroupMessageEvent): Int {
        val userId = event.userId
        val groupId = event.groupId
        val rawMsg = event.rawMessage.trim()
        return when (event.extraMap.getOrDefault("command", "")) {
            ".授权+" -> {
                if (!isSuperAdmin(userId)) return MESSAGE_IGNORE
                rawMsg.toLongOrNull()?.also {
                    authService.setAuth(groupId = it, isAuth = true, adminId = userId)
                    bot.sendGroupMsg(group_id = groupId, message = "授权成功 $it")
                } ?: bot.sendGroupMsg(group_id = groupId, message = "群号错误")
                MESSAGE_BLOCK
            }
            ".授权-" -> {
                if (!isSuperAdmin(userId)) return MESSAGE_IGNORE
                rawMsg.toLongOrNull()?.also {
                    authService.setAuth(groupId = it, isAuth = false, adminId = userId)
                    bot.sendGroupMsg(group_id = groupId, message = "取消授权 $it")
                } ?: bot.sendGroupMsg(group_id = groupId, message = "群号错误")
                MESSAGE_BLOCK
            }
            ".授权" -> {
                authService.setAuth(groupId = groupId, isAuth = true, adminId = userId)
                bot.sendGroupMsg(group_id = groupId, message = "授权成功 $groupId")
                MESSAGE_BLOCK
            }
            else -> {
                if (authService.isAuth(groupId)) {
                    MESSAGE_IGNORE
                } else {
                    // TODO 提示
                    MESSAGE_BLOCK
                }
            }
        }
    }

    override fun onGroupRequest(bot: Bot, event: GroupRequestEvent): Int {
        val userId = event.userId
        if (isSuperAdmin(userId)) {
            bot.setGroupAddRequest(event.flag, event.subType, true, "")
            return MESSAGE_BLOCK
        }
        return MESSAGE_IGNORE
    }
}