package net.lz1998.zbot.plugin

import net.lz1998.pbbot.alias.*
import net.lz1998.pbbot.bot.Bot
import net.lz1998.pbbot.bot.BotPlugin
import org.springframework.stereotype.Component

// TODO
@Component
class LogPlugin : BotPlugin() {
    override fun onFriendAddNotice(bot: Bot, event: FriendAddNoticeEvent): Int {
        return super.onFriendAddNotice(bot, event)
    }

    override fun onFriendRequest(bot: Bot, event: FriendRequestEvent): Int {
        return super.onFriendRequest(bot, event)
    }

    override fun onGroupAdminNotice(bot: Bot, event: GroupAdminNoticeEvent): Int {
        return super.onGroupAdminNotice(bot, event)
    }

    override fun onGroupBanNotice(bot: Bot, event: GroupBanNoticeEvent): Int {
        return super.onGroupBanNotice(bot, event)
    }

    override fun onGroupDecreaseNotice(bot: Bot, event: GroupDecreaseNoticeEvent): Int {
        return super.onGroupDecreaseNotice(bot, event)
    }

    override fun onGroupIncreaseNotice(bot: Bot, event: GroupIncreaseNoticeEvent): Int {
        return super.onGroupIncreaseNotice(bot, event)
    }

    override fun onGroupMessage(bot: Bot, event: GroupMessageEvent): Int {
        return super.onGroupMessage(bot, event)
    }

    override fun onGroupRequest(bot: Bot, event: GroupRequestEvent): Int {
        return super.onGroupRequest(bot, event)
    }

    override fun onGroupUploadNotice(bot: Bot, event: GroupUploadNoticeEvent): Int {
        return super.onGroupUploadNotice(bot, event)
    }

    override fun onPrivateMessage(bot: Bot, event: PrivateMessageEvent): Int {
        return super.onPrivateMessage(bot, event)
    }
}