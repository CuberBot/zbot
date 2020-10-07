package net.lz1998.zbot.repository

import net.lz1998.zbot.entity.WcaUser
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.jpa.repository.JpaSpecificationExecutor
import org.springframework.data.jpa.repository.Modifying
import org.springframework.data.jpa.repository.Query
import org.springframework.data.repository.query.Param

interface WcaUserRepository : JpaRepository<WcaUser, Long>, JpaSpecificationExecutor<WcaUser> {
    fun findWcaUserByUserId(userId: Long): WcaUser?
    fun findWcaUsersByUserIdIn(userIdList: List<Long>): List<WcaUser>
    fun findWcaUsersByWcaIdIn(wcaIdList: List<String>): List<WcaUser>
}