package net.lz1998.zbot.service

import net.lz1998.zbot.config.ApiConfig
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.http.HttpEntity
import org.springframework.http.HttpHeaders
import org.springframework.http.HttpMethod
import org.springframework.stereotype.Service
import org.springframework.web.client.RestTemplate

@Service
class ExpressService {
    @Autowired
    lateinit var restTemplate: RestTemplate

    val expressUrl: String get() = "http://jisukdcx.market.alicloudapi.com/express/query?number={number}&type={type}"

    // 这里写的不好，但是因为不同API处理方式不同，只能直接返回要发送的内容
    fun queryExpress(number: String, type: String = "auto"): String {
        val header = HttpHeaders()
        header["Authorization"] = "APPCODE ${ApiConfig.expressAppCode}"
        val req = HttpEntity(null, header)

        val resp = restTemplate.exchange(expressUrl, HttpMethod.GET, req, ExpressResponse::class.java, number, type).body?.result
                ?: return "单号错误"
        var result = "${resp.typename} ${resp.number}"
        val list = resp.list ?: return "${result}\n暂无快递信息"
        list.reversed().takeLast(5).forEach {
            result += "\n${it.time}\n${it.status}"
        }
        return result
    }
}


// https://market.aliyun.com/products/57126001/cmapi011120.html
// 因为和API是完全绑定的，所以不放entity，写在这
data class ExpressResponse(
        var msg: String = "",
        var status: Int = 0,
        var result: ExpressResult? = null
)

data class ExpressResult(
        var issign: Int = 0,
        var number: String = "",
        var deliverystatus: Int = 0,
        var type: String = "", // 英文名
        var typename: String = "", // 中文名
        var list: List<ExpressItem>? = null
)

data class ExpressItem(
        var time: String = "",
        var status: String = ""
)