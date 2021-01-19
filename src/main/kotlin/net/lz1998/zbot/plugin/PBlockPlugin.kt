package net.lz1998.zbot.plugin

import net.lz1998.pbbot.alias.GroupMessageEvent
import net.lz1998.pbbot.bot.Bot
import net.lz1998.pbbot.bot.BotPlugin
import net.lz1998.zbot.aop.annotations.PrefixFilter
import net.lz1998.zbot.service.PBlockService
import net.lz1998.zbot.service.isSuperAdmin
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Component

@Component
class PBlockPlugin : BotPlugin() {
    @Autowired
    lateinit var pBlockService: PBlockService

    @PrefixFilter(".")
    override fun onGroupMessage(bot: Bot, event: GroupMessageEvent): Int {
        val userId = event.userId
        val groupId = event.groupId
        val puserId = event.sender.userId
        var rawMsg = event.rawMessage.trim()
        val randoms = (0..100).random()
        val faileds = (400..500).random()

        rawMsg = rawMsg.replace("<at qq=\"", "")
        rawMsg = rawMsg.replace("\"/>", "")

        if (rawMsg.startsWith("屏蔽+") && isSuperAdmin(userId)) {
            rawMsg = rawMsg.substring("屏蔽+".length).trim()
            rawMsg.toLongOrNull()?.also {
                pBlockService.setPBlock(userId = it, isPBlock = true, adminId = userId)
                bot.sendGroupMsg(group_id = groupId, message = "屏蔽成功")
            } ?: bot.sendGroupMsg(group_id = groupId, message = "格式错误")
            return MESSAGE_BLOCK
        }
        if (rawMsg.startsWith("屏蔽-") && isSuperAdmin(userId)) {
            rawMsg = rawMsg.substring("屏蔽-".length).trim()
            rawMsg.toLongOrNull()?.also {
                pBlockService.setPBlock(userId = it, isPBlock = false, adminId = userId)
                bot.sendGroupMsg(group_id = groupId, message = "屏蔽成功")
            } ?: bot.sendGroupMsg(group_id = groupId, message = "格式错误")
            return MESSAGE_BLOCK
        }
        return if (pBlockService.isPBlock(puserId)) {
            MESSAGE_BLOCK
        } else {
            // TODO 提示
            MESSAGE_IGNORE
        }
    }
}
