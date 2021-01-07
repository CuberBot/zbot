package net.lz1998.zbot.service

import net.lz1998.zbot.entity.Learn
import net.lz1998.zbot.repository.LearnRepository
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Service

@Service
//@CacheConfig(cacheNames = ["learn"])
class LearnService {
    @Autowired
    lateinit var learnRepository: LearnRepository

//    @Cacheable(key = "#groupId+'_'+#ask")
    fun getAnswer(groupId: Long, ask: String): String {
        val learn = learnRepository.findFirstByGroupIdAndAsk(groupId, ask)
        return if (learn == null || learn.answer == "<default/>") {
            if (groupId != 0L) {
                getAnswer(0, ask)
            } else {
                "<null/>"
            }
        } else {
            learn.answer
        }
    }

//    @CachePut(key = "#groupId+'_'+#ask")
    fun setAnswer(groupId: Long, ask: String, answer: String, adminId: Long): String {
        var learn = Learn(groupId = groupId, ask = ask, answer = answer, adminId = adminId)
        val result = learnRepository.save(learn).answer
        return if (result == "<default/>") { // 必须处理默认，否则 @CachePut 有问题
            learn = learnRepository.findFirstByGroupIdAndAsk(0L, ask) ?: return "<null/>"
            learn.answer
        } else {
            result
        }
    }
}