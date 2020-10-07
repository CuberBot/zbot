@file:Suppress("unused")

package net.lz1998.zbot.service

import net.lz1998.pbbot.bot.BotPlugin
import net.lz1998.zbot.aop.annotations.SwitchFilter
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.context.annotation.ClassPathScanningCandidateComponentProvider
import org.springframework.core.type.filter.AnnotationTypeFilter
import org.springframework.stereotype.Service

@Service
class PluginService {
    @Autowired
    lateinit var pluginList: List<BotPlugin>
    fun getAllBotPlugin(): List<BotPlugin> = pluginList

    fun getPluginNameListWithSwitch(): List<String> {
        val provider = ClassPathScanningCandidateComponentProvider(false)
        provider.addIncludeFilter(AnnotationTypeFilter(SwitchFilter::class.java))
        val components = provider.findCandidateComponents("net.lz1998")
        return components.map {
            Class.forName(it.beanClassName).getAnnotation(SwitchFilter::class.java)?.value ?: ""
        }.filter { it != "" }
    }
}