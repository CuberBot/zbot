package net.lz1998.zbot.plugin


import net.lz1998.pbbot.alias.*
import net.lz1998.pbbot.bot.Bot
import net.lz1998.pbbot.bot.BotContainer
import net.lz1998.pbbot.bot.BotPlugin
import net.lz1998.zbot.config.Config
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Component
import java.util.concurrent.ConcurrentHashMap

@Component
class CollisionPlugin : BotPlugin() {

    @Autowired
    lateinit var botContainer: BotContainer
    var lastNoticeTimeMap: MutableMap<Long, Long> = ConcurrentHashMap()
    override fun onGroupMessage(bot: Bot, event: GroupMessageEvent): Int {
        val senderId: Long = event.sender.userId
        val groupId: Long = event.groupId
        if (groupId == Config.ADMIN_GROUP_ID) {
            // 管理群，继续处理
            return MESSAGE_IGNORE
        }
        //主群
        if (groupId == Config.MAIN_GROUP_ID) {
            return if (Config.MAIN_ROBOT_ID != bot.selfId) {
                MESSAGE_BLOCK
            } else {
                MESSAGE_IGNORE
            }
        }
        // 其他群
        if (botContainer.bots.containsKey(senderId)) {
            lastNoticeTimeMap.putIfAbsent(groupId, 0L)
            val time = System.currentTimeMillis()
            // 5分钟最多提示一次
            if (time - lastNoticeTimeMap[groupId]!! < 300 * 1000L) {
                return MESSAGE_BLOCK
            }
            lastNoticeTimeMap[groupId] = time
            bot.sendGroupMsg(Config.ADMIN_GROUP_ID, "发现重复群$groupId", false)
            return MESSAGE_BLOCK
        }
        return MESSAGE_IGNORE
    }

    override fun onGroupUploadNotice(bot: Bot, event: GroupUploadNoticeEvent): Int {
        return if (bot.selfId != Config.MAIN_ROBOT_ID && event.groupId == Config.MAIN_GROUP_ID) {
            MESSAGE_BLOCK
        } else MESSAGE_IGNORE
    }

    override fun onGroupAdminNotice(bot: Bot, event: GroupAdminNoticeEvent): Int {
        return if (bot.selfId != Config.MAIN_ROBOT_ID && event.groupId == Config.MAIN_GROUP_ID) {
            MESSAGE_BLOCK
        } else MESSAGE_IGNORE
    }

    override fun onGroupDecreaseNotice(bot: Bot, event: GroupDecreaseNoticeEvent): Int {
        return if (bot.selfId != Config.MAIN_ROBOT_ID && event.groupId == Config.MAIN_GROUP_ID) {
            MESSAGE_BLOCK
        } else MESSAGE_IGNORE
    }

    override fun onGroupIncreaseNotice(bot: Bot, event: GroupIncreaseNoticeEvent): Int {
        return if (bot.selfId != Config.MAIN_ROBOT_ID && event.groupId == Config.MAIN_GROUP_ID) {
            MESSAGE_BLOCK
        } else MESSAGE_IGNORE
    }
}