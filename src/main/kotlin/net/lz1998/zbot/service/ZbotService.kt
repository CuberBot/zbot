package net.lz1998.zbot.service

import net.lz1998.pbbot.bot.Bot
import net.lz1998.pbbot.bot.BotContainer
import net.lz1998.zbot.repository.AuthRepository
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Service
import java.util.concurrent.ConcurrentHashMap

@Service
class ZbotService {
    @Autowired
    lateinit var authRepository: AuthRepository

    @Autowired
    lateinit var botContainer: BotContainer

    var groupInfoMap = ConcurrentHashMap<Long, MyGroupInfo>()
    var lastRefreshTime: Long = 0
    val REFRESH_INTERVAL: Long = 600000L

    fun getMyGroupInfo(groupId: Long): MyGroupInfo? {
        var myGroupInfo = groupInfoMap[groupId]
        if (myGroupInfo == null) {
            // 刷新数据
            if (System.currentTimeMillis() - lastRefreshTime > REFRESH_INTERVAL) {
                synchronized(lastRefreshTime) {
                    if (System.currentTimeMillis() - lastRefreshTime > REFRESH_INTERVAL) {
                        lastRefreshTime = System.currentTimeMillis()
                        refreshGroupData()
                        myGroupInfo = groupInfoMap[groupId]
                    }
                }
            }
        }
        return myGroupInfo
    }

    fun getBotInstance(groupId: Long): Bot? {
        return botContainer.bots[getBotId(groupId)]
    }

    fun getBotId(groupId: Long): Long? {
        return groupInfoMap[groupId]?.botId
    }

    @Synchronized
    fun refreshGroupData() {
        val authGroups: List<Long> = authRepository.findAuthsByIsAuthTrue().map { it.groupId }
        // 对机器人循环
        botContainer.bots.forEach { botId, bot ->
            val groupList = bot.getGroupList()?.groupList ?: return@forEach
            groupList.filter { authGroups.contains(it.groupId) }.forEach {
                val myGroupInfo = MyGroupInfo(
                        groupId = it.groupId,
                        groupName = it.groupName,
                        botId = botId
                )
                groupInfoMap[it.groupId] = myGroupInfo
            }
        }
    }

}

data class MyGroupInfo(
        val groupId: Long,
        val groupName: String,
        val botId: Long
)