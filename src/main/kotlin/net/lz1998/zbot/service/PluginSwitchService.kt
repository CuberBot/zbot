@file:Suppress("unused")

package net.lz1998.zbot.service

import net.lz1998.zbot.entity.PluginSwitch
import net.lz1998.zbot.repository.PluginSwitchRepository
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Service

@Service
class PluginSwitchService {
    @Autowired
    lateinit var pluginSwitchRepository: PluginSwitchRepository

    @Autowired
    lateinit var pluginService: PluginService

    fun setPluginSwitch(groupId: Long, pluginName: String, stop: Boolean) =
            pluginSwitchRepository.save(
                    PluginSwitch(
                            groupId = groupId,
                            pluginName = pluginName,
                            stop = stop)
            )

    fun isPluginStop(groupId: Long, pluginName: String): Boolean =
            pluginSwitchRepository.findPluginSwitchByGroupIdAndPluginName(
                    groupId = groupId,
                    pluginName = pluginName
            )?.stop ?: false

    fun getGroupPluginSwitchList(groupId: Long): List<PluginSwitch> {
        val pluginSwitchList = pluginSwitchRepository.findPluginSwitchesByGroupId(groupId = groupId).toMutableList()

        // 补充没修改过的开关
        val modifiedPluginNameList = pluginSwitchList.map { it.pluginName }
        pluginService.getPluginNameListWithSwitch().forEach {
            if (!modifiedPluginNameList.contains(it)) {
                pluginSwitchList.add(PluginSwitch(groupId = groupId, pluginName = it, stop = false))
            }
        }
        return pluginSwitchList
    }

    fun isPluginExist(pluginName: String): Boolean =
            pluginName == "回复" || pluginService.getPluginNameListWithSwitch().contains(pluginName)
}