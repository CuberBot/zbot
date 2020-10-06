package net.lz1998.zbot.repository

import net.lz1998.zbot.entity.PluginSwitch
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.jpa.repository.JpaSpecificationExecutor
import org.springframework.data.jpa.repository.Modifying
import org.springframework.data.jpa.repository.Query
import org.springframework.data.repository.query.Param

interface PluginSwitchRepository : JpaRepository<PluginSwitch, Long>,JpaSpecificationExecutor<PluginSwitch> {
    fun findPluginSwitchByGroupIdAndPluginName(groupId: Long, pluginName: String): PluginSwitch?
    fun findPluginSwitchesByGroupId(groupId: Long): List<PluginSwitch>

    @Modifying
    @Query(value = "INSERT INTO zbot_plugin_switch (group_id, plugin_name, stop) VALUES (:#{#pluginSwitch.groupId},:#{#pluginSwitch.pluginName},:#{#pluginSwitch.stop}) ON DUPLICATE KEY UPDATE STOP = :#{#pluginSwitch.stop}",nativeQuery = true)
    fun save(@Param("pluginSwitch")pluginSwitch: PluginSwitch)
}