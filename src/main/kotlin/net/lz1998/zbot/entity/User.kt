package net.lz1998.zbot.entity

import javax.persistence.*

@Entity
@Table(name = "zbot_user")
data class User(
        @Id
        @Column(columnDefinition = "BIGINT NOT NULL COMMENT 'QQ'")
        val userId: Long = 0,

        @Column(columnDefinition = "VARCHAR(255) NOT NULL COMMENT '密码'")
        val password: String = ""
)
