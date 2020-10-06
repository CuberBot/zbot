package net.lz1998.zbot.entity

import java.util.*
import javax.persistence.*

@Entity
@Table(name = "zbot_plugin_switch",
        indexes = [
            Index(name = "uniq_group_plugin", columnList = "groupId, pluginName", unique = true)
        ]
)
data class PluginSwitch(
        @Id
        @GeneratedValue(strategy = GenerationType.IDENTITY)
        val id: Long = -1,
        @Column(columnDefinition = "BIGINT NOT NULL COMMENT '群号'")
        val groupId: Long,
        @Column(columnDefinition = "VARCHAR(32) NOT NULL COMMENT '插件名称'")
        val pluginName: String,
        @Column(columnDefinition = "BOOLEAN NOT NULL DEFAULT false COMMENT '是否停用'")
        val stop: Boolean = false,
        @Column(columnDefinition = "TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间'")
        val updatedAt: Date? = null
)