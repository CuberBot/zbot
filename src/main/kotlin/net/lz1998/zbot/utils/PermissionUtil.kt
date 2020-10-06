package net.lz1998.zbot.utils

import net.lz1998.zbot.config.Config
import onebot.OnebotEvent

fun hasAdminRole(sender: OnebotEvent.GroupMessageEvent.Sender): Boolean = Config.ADMIN_LIST.contains(sender.userId) || sender.role == "ADMIN" || sender.role == "OWNER"
