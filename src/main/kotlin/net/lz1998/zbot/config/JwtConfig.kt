package net.lz1998.zbot.config

import org.springframework.beans.factory.annotation.Value
import org.springframework.boot.context.properties.ConfigurationProperties

@ConfigurationProperties(prefix = "zbot.jwt")
object JwtConfig {
    /**
     * jwt 加密 key，默认值：xkcoding.
     */
    @Value("\${zbot.jwt.key}")
    var key: String = "xxx"

    /**
     * jwt 过期时间，默认值：600000 `10 分钟`.
     */
    @Value("\${zbot.jwt.ttl}")
    val ttl: Long = 600000L

    /**
     * 开启 记住我 之后 jwt 过期时间，默认值 604800000 `7 天`
     */
    @Value("\${zbot.jwt.remember}")
    val remember: Long = 604800000L

    @Value("\${zbot.jwt.redisKeyPrefix}")
    val redisKeyPrefix: String = "jwt_"
}

