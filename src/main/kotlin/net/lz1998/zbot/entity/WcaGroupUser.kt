package net.lz1998.zbot.entity

import java.io.Serializable
import javax.persistence.*

data class WcaGroupUserKey(
        val groupId: Long = 0,
        val userId: Long = 0,
) : Serializable

@Entity
@Table(name = "zbot_wca_group_user")
@IdClass(WcaGroupUserKey::class)
data class WcaGroupUser(
        @Id
        @Column(columnDefinition = "BIGINT NOT NULL COMMENT '群号'")
        var groupId: Long = 0,

        @Id
        @Column(columnDefinition = "BIGINT NOT NULL COMMENT 'QQ'")
        var userId: Long = 0,

        @Column(columnDefinition = "VARCHAR(32) NOT NULL COMMENT 'WCA ID'")
        var wcaId: String = "",

        @Column(columnDefinition = "VARCHAR(255) NOT NULL COMMENT '姓名'")
        var name: String = "",

        @Column(columnDefinition = "VARCHAR(32) NOT NULL COMMENT '性别'")
        var gender: String = "",

        @Column(columnDefinition = "BIGINT NOT NULL DEFAULT 0 COMMENT '统计开始时间'")
        var startTime: Long = 0,

        @Column(columnDefinition = "BIGINT NOT NULL DEFAULT 4102415999000 COMMENT '统计结束时间'")
        var endTime: Long = 0, // 4102415999000L世界末日

        @Column(columnDefinition = "BOOLEAN NOT NULL DEFAULT false COMMENT '管理禁止参与统计'")
        var ban: Boolean = false,

        @Column(columnDefinition = "BOOLEAN NOT NULL DEFAULT false COMMENT '自己不参与统计'")
        var attend: Boolean = false,

        @Column(columnDefinition = "BOOLEAN NOT NULL DEFAULT true COMMENT '在群内'")
        var inGroup: Boolean = true,
)