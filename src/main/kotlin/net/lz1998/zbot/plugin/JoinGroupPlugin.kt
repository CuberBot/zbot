package net.lz1998.zbot.plugin

import net.lz1998.pbbot.alias.GroupIncreaseNoticeEvent
import net.lz1998.pbbot.bot.Bot
import net.lz1998.pbbot.bot.BotPlugin
import net.lz1998.zbot.config.ZbotConfig
import org.springframework.stereotype.Component

@Component
class JoinGroupPlugin : BotPlugin() {
    override fun onGroupIncreaseNotice(bot: Bot, event: GroupIncreaseNoticeEvent): Int {
        val groupId = event.groupId
        if (event.userId == bot.selfId) {
            bot.sendGroupMsg(groupId, "欢迎使用Zbot\nhttps://github.com/lz1998/zbot\n如有问题可以提issue")
            bot.sendPrivateMsg(ZbotConfig.mainAdmin, "已进群$groupId")
            bot.sendGroupMsg(ZbotConfig.adminGroupId, "已进群$groupId")
            return MESSAGE_BLOCK
        }
        return super.onGroupIncreaseNotice(bot, event)
    }
}