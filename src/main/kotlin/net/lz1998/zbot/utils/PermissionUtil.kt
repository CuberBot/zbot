package net.lz1998.zbot.utils

import net.lz1998.zbot.config.ZbotConfig
import onebot.OnebotEvent

fun isSuperAdmin(userId: Long): Boolean = ZbotConfig.superAdminList.contains(userId)
fun isGroupOwner(sender: OnebotEvent.GroupMessageEvent.Sender): Boolean = ZbotConfig.superAdminList.contains(sender.userId) || sender.role == "OWNER"
fun isAdmin(sender: OnebotEvent.GroupMessageEvent.Sender): Boolean = ZbotConfig.superAdminList.contains(sender.userId) || sender.role == "ADMIN" || sender.role == "OWNER"
