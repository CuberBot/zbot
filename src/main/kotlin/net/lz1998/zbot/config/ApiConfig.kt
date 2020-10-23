package net.lz1998.zbot.config

import org.springframework.beans.factory.annotation.Value
import org.springframework.boot.context.properties.ConfigurationProperties
import org.springframework.stereotype.Component


@Component
@ConfigurationProperties("zbot.api")
object ApiConfig {
    @Value("\${zbot.api.expressAppCode}")
    var expressAppCode = ""
}