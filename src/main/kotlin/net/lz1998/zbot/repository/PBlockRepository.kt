package net.lz1998.zbot.repository

import net.lz1998.zbot.entity.PBlock
import net.lz1998.zbot.entity.PBlockKey
import org.springframework.data.domain.Page
import org.springframework.data.domain.Pageable
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.jpa.repository.JpaSpecificationExecutor
import org.springframework.data.jpa.repository.Modifying
import org.springframework.transaction.annotation.Transactional

interface PBlockRepository : JpaRepository<PBlock, PBlockKey>, JpaSpecificationExecutor<PBlock> {
    fun findPBlockByUserId(userId: Long): PBlock?
    fun findPBlocksByIsPBlockTrue(): List<PBlock>

    @Transactional
    @Modifying
    fun deletePBlockByUserId(userId: Long)

    fun findPBlocksByUserId(userId: Long,pageable: Pageable): Page<PBlock>
}