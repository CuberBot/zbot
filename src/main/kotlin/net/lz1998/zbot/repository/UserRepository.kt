package net.lz1998.zbot.repository

import net.lz1998.zbot.entity.User
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.jpa.repository.JpaSpecificationExecutor
import org.springframework.data.jpa.repository.Modifying
import org.springframework.data.jpa.repository.Query
import org.springframework.data.repository.query.Param

interface UserRepository : JpaRepository<User, Long>, JpaSpecificationExecutor<User> {
    fun findUserByUserId(userId: Long): User?
}