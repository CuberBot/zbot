package net.lz1998.zbot.controller

import kotlinx.serialization.json.add
import kotlinx.serialization.json.buildJsonArray
import kotlinx.serialization.json.buildJsonObject
import kotlinx.serialization.json.put
import net.lz1998.zbot.config.ServiceConfig
import net.lz1998.zbot.service.WcaService
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.core.io.Resource
import org.springframework.http.*
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController
import org.springframework.web.client.RestTemplate

@RestController
@RequestMapping(("/trend"))
class TrendController {

    @Autowired
    lateinit var restTemplate: RestTemplate

    @Autowired
    lateinit var wcaService: WcaService


    @RequestMapping("/getImage", produces = ["image/png"])
    fun getImage(wcaId: String, eventId: String, type: String, theme: String): ByteArray? {
        val results = wcaService.getResultList(wcaId, eventId) ?: return null

        val t = if (type.toUpperCase() == "AVG") {
            "AVG"
        } else {
            "SIN"
        }

        val option = buildJsonObject {
            put("title", buildJsonObject {
                put("text", "$wcaId $eventId $t")
            })
            put("xAxis", buildJsonObject {
                put("type", "category")
                put("data", buildJsonArray {
                    results.forEach {
                        add(it.competitionId)
                    }
                })
            })
            put("yAxis", buildJsonObject {
                put("type", "value")
            })
            put("series", buildJsonArray {
                add(buildJsonObject {
                    put("name", "成绩")
                    put("type", "line")
                    put("data", buildJsonArray {
                        results.forEach {
                            if (t == "AVG") {
                                add(resultStringFormat(it.average, it.eventId))
                            } else {
                                add(resultStringFormat(it.best, it.eventId))
                            }
                        }
                    })
                })
            })
            put("theme", "macarons")
        }
        val header = HttpHeaders()
        header.contentType = MediaType.APPLICATION_JSON
        val req = HttpEntity(option.toString(), header)
        val entity: ResponseEntity<Resource> = restTemplate.exchange("http://${ServiceConfig.echarts}/?theme=${theme}", HttpMethod.POST, req, Resource::class.java)
        return entity.body?.inputStream?.readBytes()
    }

    // 暂时不考虑三盲
    fun resultStringFormat(result: Int, eventId: String): Number? {
        if (result == -1) {
            return null
        }
        if (result == -2) {
            return null
        }
        if (result == 0) {
            return null
        }
        return if ("333fm" == eventId) {
            if (result > 1000) {
                String.format("%.2f", result.toDouble() / 100).toDoubleOrNull()
            } else {
                result.toString().toDoubleOrNull()
            }
        } else {
            val sec = result / 100
            val msec = result % 100
            String.format("%d.%02d", sec, msec).toDoubleOrNull()
        }
    }

}