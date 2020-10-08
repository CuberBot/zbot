package net.lz1998.zbot.repository

import net.lz1998.zbot.entity.Welcome
import org.springframework.data.jpa.repository.JpaRepository

interface WelcomeRepository : JpaRepository<Welcome, Long> {
    fun findWelcomeByGroupId(groupId: Long): Welcome?
}