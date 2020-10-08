package net.lz1998.zbot.service

import com.fasterxml.jackson.databind.util.LRUMap
import org.springframework.stereotype.Service

// 临时黑名单
data class TmpBlack(
        var userId: Long,
        var startTime: Long,
        var duration: Long
)

// 消息记录
data class CommandRecord(
        var lastCommandTime: Long,
        var msgList: MutableList<String>
)

@Service
class BlackService {

    val tmpBlackLru = LRUMap<Long, TmpBlack>(16, 128)
    val commandLru = LRUMap<Long, CommandRecord>(64, 256)
    val COMMAND_MIN_TIME = 5000L;
    val DURATION = 25000L

    fun setTmpBlack(userId: Long): Long {
        val duration = (tmpBlackLru[userId]?.duration ?: DURATION) * 24
        val tmpBlack = TmpBlack(userId = userId, startTime = System.currentTimeMillis(), duration = duration)
        tmpBlackLru.put(userId, tmpBlack)
        return duration
    }

    fun onCommand(userId: Long, rawMsg: String): Long {
        val now = System.currentTimeMillis()
        var commandRecord = commandLru[userId]
        if (commandRecord == null || commandRecord.lastCommandTime + COMMAND_MIN_TIME < now) {
            commandRecord = CommandRecord(now, mutableListOf(rawMsg))
        } else {
            commandRecord.msgList.add(rawMsg)
        }
        commandLru.put(userId, commandRecord)
        return if (commandRecord.msgList.size > 3) {
            return setTmpBlack(userId)
        } else 0
    }

    fun isBlack(userId: Long): Boolean {
        val tmpBlack = tmpBlackLru[userId] ?: return false
        return tmpBlack.startTime + tmpBlack.duration > System.currentTimeMillis()
    }
}

