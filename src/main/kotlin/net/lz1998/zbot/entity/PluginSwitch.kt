package net.lz1998.zbot.entity

import org.hibernate.annotations.UpdateTimestamp
import java.io.Serializable
import java.util.*
import javax.persistence.*

data class PluginSwitchKey(
        val groupId: Long = 0,
        val pluginName: String = ""
) : Serializable

@Entity
@IdClass(PluginSwitchKey::class)
@Table(name = "zbot_plugin_switch")
data class PluginSwitch(
        @Id
        @Column(columnDefinition = "BIGINT NOT NULL COMMENT '群号'")
        val groupId: Long = 0,
        @Id
        @Column(columnDefinition = "VARCHAR(32) NOT NULL COMMENT '插件名称'")
        val pluginName: String = "",
        @Column(columnDefinition = "BOOLEAN NOT NULL DEFAULT false COMMENT '是否停用'")
        val stop: Boolean = false,
        @UpdateTimestamp
        @Column(columnDefinition = "TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间'")
        val gmtModified: Date? = null
)