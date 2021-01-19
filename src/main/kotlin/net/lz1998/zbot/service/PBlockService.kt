package net.lz1998.zbot.service

import net.lz1998.zbot.entity.PBlock
import net.lz1998.zbot.repository.PBlockRepository
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.cache.annotation.CacheConfig
import org.springframework.cache.annotation.CachePut
import org.springframework.cache.annotation.Cacheable
import org.springframework.data.jpa.repository.Modifying
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

@Service
@CacheConfig(cacheNames = ["pblock"])
class PBlockService {
    @Autowired
    lateinit var pBlockRepository: PBlockRepository

    @Cacheable(key = "#userId")
    fun isPBlock(userId: Long): Boolean {
        return pBlockRepository.findPBlockByUserId(userId)?.isPBlock ?: false
    }

    @Transactional
    @Modifying
    fun isDelete(userId: Long) {
        return pBlockRepository.deletePBlockByUserId(userId)
    }

    @CachePut(key = "#userId")
    @Modifying
    fun setPBlock(userId: Long, isPBlock: Boolean, adminId: Long): Boolean =
        pBlockRepository.save(PBlock(
                    userId = userId,
                    isPBlock = isPBlock,
                    adminId = adminId
            )).isPBlock
}