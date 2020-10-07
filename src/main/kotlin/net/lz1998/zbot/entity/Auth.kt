package net.lz1998.zbot.entity

import org.hibernate.annotations.UpdateTimestamp
import java.util.*
import javax.persistence.Column
import javax.persistence.Entity
import javax.persistence.Id
import javax.persistence.Table

@Entity
@Table(name = "zbot_auth")
data class Auth(
        @Id
        @Column(columnDefinition = "BIGINT NOT NULL COMMENT '群号'")
        val groupId: Long = 0,
        @Column(columnDefinition = "BOOLEAN NOT NULL DEFAULT false COMMENT '是否已授权'")
        val isAuth: Boolean = false,
        @Column(columnDefinition = "BIGINT NOT NULL COMMENT '授权人QQ'")
        val adminId: Long = 0,
        @UpdateTimestamp
        @Column(columnDefinition = "TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间'")
        val gmtModified: Date? = null
)