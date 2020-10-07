package net.lz1998.zbot

import net.lz1998.zbot.config.ZbotConfig
import net.lz1998.zbot.config.ServiceConfig
import net.lz1998.zbot.config.JwtConfig
import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.context.properties.EnableConfigurationProperties
import org.springframework.boot.runApplication
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.EnableAspectJAutoProxy
import org.springframework.http.converter.protobuf.ProtobufHttpMessageConverter
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder
import org.springframework.security.crypto.password.PasswordEncoder
import org.springframework.web.client.RestTemplate

@EnableAspectJAutoProxy
@SpringBootApplication
@EnableConfigurationProperties(ZbotConfig::class, ServiceConfig::class, JwtConfig::class)
class ZbotApplication {
    @Bean
    fun restTemplate(): RestTemplate {
        return RestTemplate();
    }

    @Bean
    fun createProtobufConverter(): ProtobufHttpMessageConverter {
        return ProtobufHttpMessageConverter();
    }

    @Bean
    fun createPasswordEncoder(): PasswordEncoder {
        return BCryptPasswordEncoder()
    }
}

fun main(args: Array<String>) {
    runApplication<ZbotApplication>(*args)
}
