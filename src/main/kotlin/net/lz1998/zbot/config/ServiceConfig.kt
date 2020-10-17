package net.lz1998.zbot.config

import org.springframework.beans.factory.annotation.Value
import org.springframework.boot.context.properties.ConfigurationProperties
import org.springframework.stereotype.Component

@Component
@ConfigurationProperties("zbot.service")
object ServiceConfig {
    @Value("\${zbot.service.tnoodle}")
    var tnoodle = "tnoodle.lz1998.xin"

    @Value("\${zbot.service.wcads}")
    var wcads = "wcads.lz1998.xin"

    @Value("\${zbot.service.vscube}")
    var vscube = "vscube.lz1998.xin"

    @Value("\${zbot.service.rank}")
    var rank = "sunshy.sinaapp.com"

    @Value("\${zbot.service.cubing}")
    var cubing = "cubingchina.com"
}