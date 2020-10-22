package net.lz1998.zbot.repository

import net.lz1998.zbot.entity.Learn
import net.lz1998.zbot.entity.LearnKey
import org.springframework.data.domain.Page
import org.springframework.data.domain.Pageable
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.jpa.repository.JpaSpecificationExecutor
import org.springframework.transaction.annotation.Transactional

interface LearnRepository : JpaRepository<Learn, LearnKey>, JpaSpecificationExecutor<Learn> {
    fun findFirstByGroupIdAndAsk(groupId: Long, ask: String): Learn?

    @Transactional
    fun deleteLearnsByGroupId(groupId: Long)

    fun findLearnsByGroupId(groupId: Long, pageable: Pageable): Page<Learn>

    @Transactional
    fun deleteLearnByGroupIdAndAsk(groupId: Long, ask: String)

    fun findFirstByGroupIdInAndAsk(groupIdList: List<Long>, ask: String): Learn?
}