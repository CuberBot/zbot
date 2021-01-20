package net.lz1998.zbot.repository

import net.lz1998.zbot.entity.Auth
import org.springframework.data.domain.Page
import org.springframework.data.domain.Pageable
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.jpa.repository.Modifying
import org.springframework.transaction.annotation.Transactional

interface AuthRepository : JpaRepository<Auth, Long> {
    fun findAuthByGroupId(groupId: Long): Auth?
    fun findAuthsByIsAuthTrue(): List<Auth>

    @Transactional
    @Modifying
    fun deleteAuthByGroupId(groupId: Long)

    fun findAuthsByGroupId(groupId: Long,pageable: Pageable): Page<Auth>
}