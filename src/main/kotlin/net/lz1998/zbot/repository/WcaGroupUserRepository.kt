package net.lz1998.zbot.repository

import net.lz1998.zbot.entity.WcaGroupUser
import net.lz1998.zbot.entity.WcaGroupUserKey
import org.springframework.data.domain.Page
import org.springframework.data.domain.Pageable
import org.springframework.data.jpa.repository.JpaRepository

interface WcaGroupUserRepository : JpaRepository<WcaGroupUser, WcaGroupUserKey> {
    fun findWcaGroupUsersByGroupId(groupId: Long): List<WcaGroupUser>
    fun deleteWcaGroupUsersByGroupIdAndUserIdIn(groupId: Long, userIdList: List<Long>)
    fun findWcaGroupUsersByGroupIdAndAttendIsTrue(groupId: Long, pageable: Pageable): Page<WcaGroupUser>
    fun findWcaGroupUserByGroupIdAndUserId(groupId: Long, userId: Long): WcaGroupUser?
    fun findWcaGroupUsersByGroupIdAndAttendIsTrueAndBanIsFalse(groupId: Long): List<WcaGroupUser>

    fun findWcaGroupUsersByUserId(userId: Long): List<WcaGroupUser>
}