package net.lz1998.zbot.entity

import javax.persistence.*

@Entity
@Table(name = "zbot_wca_user")
data class WcaUser(
        @Id
        @Column(columnDefinition = "BIGINT NOT NULL COMMENT 'QQ'")
        val userId: Long = 0,

        @Column(columnDefinition = "VARCHAR(32) NOT NULL COMMENT 'WCA ID'")
        val wcaId: String = "",

        @Column(columnDefinition = "VARCHAR(255) NOT NULL COMMENT '姓名'")
        val name: String = "",

        @Column(columnDefinition = "VARCHAR(32) NOT NULL COMMENT '性别'")
        val gender: String = "",

        @Column(columnDefinition = "BOOLEAN NOT NULL DEFAULT true COMMENT '公开'")
        var open: Boolean = true,

        @Column(columnDefinition = "BOOLEAN NOT NULL DEFAULT true COMMENT '新群参与排名'")
        var defaultAttend: Boolean = true,

        @Column(columnDefinition = "BOOLEAN NOT NULL DEFAULT true COMMENT '有效'")
        val enabled: Boolean = true
)
