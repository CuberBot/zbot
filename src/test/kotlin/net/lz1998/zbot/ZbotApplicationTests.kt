package net.lz1998.zbot

import net.lz1998.zbot.service.PersonalService
import net.lz1998.zbot.service.WcaService
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest

@SpringBootTest(classes = [ZbotApplication::class], webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
class ZbotApplicationTests {
    @Autowired
    lateinit var wcaService: WcaService

    @Autowired
    lateinit var personalService: PersonalService

    @Test
    fun contextLoads() {
        val result = wcaService.handleWca(875543543, "李政 2016") { wcaService.getWcaPersonResultString(it) }
        println(result)
    }

}
