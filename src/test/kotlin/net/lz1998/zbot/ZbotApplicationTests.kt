package net.lz1998.zbot

import net.lz1998.zbot.aop.annotations.SwitchFilter
import org.junit.jupiter.api.Test
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.context.annotation.ClassPathScanningCandidateComponentProvider
import org.springframework.core.type.filter.AnnotationTypeFilter

@SpringBootTest(classes = [ZbotApplication::class], webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
class ZbotApplicationTests {

    @Test
    fun contextLoads() {
        val provider = ClassPathScanningCandidateComponentProvider(false)
        provider.addIncludeFilter(AnnotationTypeFilter(SwitchFilter::class.java))
        val components = provider.findCandidateComponents("net.lz1998")
        println(components.map { it.beanClassName })
    }

}
