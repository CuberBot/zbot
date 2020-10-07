package net.lz1998.zbot.config

import org.springframework.beans.factory.annotation.Value
import org.springframework.boot.context.properties.ConfigurationProperties
import org.springframework.stereotype.Component

@Component
@ConfigurationProperties("zbot.config")
object ZbotConfig {
    @Value("\${zbot.config.superAdminList}")
    var superAdminList = listOf(
            875543533L, 535660742L
    )

    @Value("\${zbot.config.mainAdmin}")
    var mainAdmin = 875543533L

    @Value("\${zbot.config.mainGroupId}")
    var mainGroupId = 374735267L

    @Value("\${zbot.config.adminGroupId}")
    var adminGroupId = 0L

    @Value("\${zbot.config.mainRobotId}")
    var mainRobotId = 2490390725L

    @Value("\${zbot.config.wcaClientId}")
    var wcaClientId = "xxx"

    @Value("\${zbot.config.wcaClientSecret}")
    var wcaClientSecret = "xxx"
}