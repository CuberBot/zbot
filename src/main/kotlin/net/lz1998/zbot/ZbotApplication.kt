package net.lz1998.zbot

import com.fasterxml.jackson.annotation.JsonTypeInfo
import com.fasterxml.jackson.databind.ObjectMapper
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule
import net.lz1998.zbot.config.ZbotConfig
import net.lz1998.zbot.config.ServiceConfig
import net.lz1998.zbot.config.JwtConfig
import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.context.properties.EnableConfigurationProperties
import org.springframework.boot.runApplication
import org.springframework.cache.annotation.EnableCaching
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.EnableAspectJAutoProxy
import org.springframework.data.redis.cache.RedisCacheConfiguration
import org.springframework.data.redis.serializer.GenericJackson2JsonRedisSerializer
import org.springframework.data.redis.serializer.RedisSerializationContext
import org.springframework.http.converter.protobuf.ProtobufHttpMessageConverter
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder
import org.springframework.security.crypto.password.PasswordEncoder
import org.springframework.web.client.RestTemplate

@EnableAspectJAutoProxy
@SpringBootApplication
@EnableCaching
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

    @Bean
    fun redisCacheConfiguration(): RedisCacheConfiguration {
        val objectMapper =
                ObjectMapper()
//                        .registerModule(KotlinModule())
                        .registerModule(JavaTimeModule())
                        .enableDefaultTyping(ObjectMapper.DefaultTyping.NON_FINAL, JsonTypeInfo.As.PROPERTY)

        val serializer = GenericJackson2JsonRedisSerializer(objectMapper)

        return RedisCacheConfiguration
                .defaultCacheConfig()
                .serializeValuesWith(RedisSerializationContext.SerializationPair.fromSerializer(serializer))
    }
}

fun main(args: Array<String>) {
    runApplication<ZbotApplication>(*args)
}
