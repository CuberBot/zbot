package net.lz1998.zbot

import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.runApplication
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.EnableAspectJAutoProxy
import org.springframework.web.client.RestTemplate

@EnableAspectJAutoProxy
@SpringBootApplication
class ZbotApplication{
    @Bean
    fun restTemplate(): RestTemplate {
        return RestTemplate();
    }
}

fun main(args: Array<String>) {
    runApplication<ZbotApplication>(*args)
}
