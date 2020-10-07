package net.lz1998.zbot.repository

import net.lz1998.zbot.entity.Auth
import org.springframework.data.jpa.repository.JpaRepository

interface AuthRepository : JpaRepository<Auth, Long> {
    fun findAuthByGroupId(groupId: Long): Auth?
    fun findAuthsByIsAuthTrue(): List<Auth>
}