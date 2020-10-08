package net.lz1998.zbot.service

import net.lz1998.zbot.entity.Welcome
import net.lz1998.zbot.repository.WelcomeRepository
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.cache.annotation.CacheConfig
import org.springframework.cache.annotation.CachePut
import org.springframework.cache.annotation.Cacheable
import org.springframework.stereotype.Service

@Service
@CacheConfig(cacheNames = ["welcome"])
class WelcomeService {
    @Autowired
    lateinit var welcomeRepository: WelcomeRepository

    val DEFAULT_MSG = "<default/>"
    val EMPTY_MSG = ""

    @CachePut(key = "#groupId")
    fun setWelcomeMsg(groupId: Long, welcomeMsg: String, adminId: Long): String {
        val welcome = Welcome(groupId = groupId, welcomeMsg = welcomeMsg, adminId = adminId)
        welcomeRepository.save(welcome)
        return if (welcomeMsg == DEFAULT_MSG) {
            getWelcomeMsg(0)
        } else {
            welcomeMsg
        }
    }

    @Cacheable(key = "#groupId")
    fun getWelcomeMsg(groupId: Long): String {
        val welcomeMsg = welcomeRepository.findWelcomeByGroupId(groupId = groupId)?.welcomeMsg ?: "<default/>"
        return when {
            welcomeMsg != DEFAULT_MSG -> welcomeMsg
            groupId != 0L -> getWelcomeMsg(0)
            else -> EMPTY_MSG
        }
    }
}