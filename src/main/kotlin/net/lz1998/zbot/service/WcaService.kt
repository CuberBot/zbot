@file:Suppress("unused")

package net.lz1998.zbot.service

import net.lz1998.zbot.config.ServiceConfig
import net.lz1998.zbot.entity.wcads.WcaPerson
import net.lz1998.zbot.entity.wcads.WcaRank
import net.lz1998.zbot.entity.WcaUser
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.core.ParameterizedTypeReference
import org.springframework.http.HttpMethod
import org.springframework.stereotype.Service
import org.springframework.web.client.RestTemplate


@Service
class WcaService {
    @Autowired
    lateinit var personalService: PersonalService

    @Autowired
    lateinit var restTemplate: RestTemplate
    val findPersonUrl: String get() = "http://${ServiceConfig.wcads}/wcaPerson/findPersonById?id={id}"
    val searchPeopleUrl: String get() = "http://${ServiceConfig.wcads}/wcaPerson/searchPeople?q={q}"
    val singleRankUrl: String get() = "http://${ServiceConfig.wcads}/wcaSingle/findBestResultsByPersonId?personId={personId}"
    val averageRankUrl: String get() = "http://${ServiceConfig.wcads}/wcaAverage/findBestResultsByPersonId?personId={personId}"


    fun getExactPerson(wcaId: String): WcaPerson? {
        return restTemplate.exchange(findPersonUrl, HttpMethod.GET, null, typeRef<Result<WcaPerson>>(), wcaId).body?.data
    }

    fun searchPeople(str: String?): List<WcaPerson> {
        return restTemplate.exchange(searchPeopleUrl, HttpMethod.GET, null, typeRef<ListResult<WcaPerson>>(), str).body?.data
                ?: listOf()
    }

    fun getSingleRankList(wcaId: String): List<WcaRank>? {
        return restTemplate.exchange(singleRankUrl, HttpMethod.GET, null, typeRef<ListResult<WcaRank>>(), wcaId).body?.data
    }

    fun getAverageRankList(wcaId: String): List<WcaRank>? {
        return restTemplate.exchange(averageRankUrl, HttpMethod.GET, null, typeRef<ListResult<WcaRank>>(), wcaId).body?.data
    }

    fun getWcaPersonResultString(wcaPerson: WcaPerson): String {
        val singleDataList: List<WcaRank> = getSingleRankList(wcaPerson.id) ?: return "ERROR!"
        val averageDataList: List<WcaRank> = getAverageRankList(wcaPerson.id) ?: return "ERROR!"
        val result = singleDataList
                .sortedBy { eventOrder.indexOf(it.eventId) }
                .joinToString("\n") { sin ->
                    "${sin.eventId} ${resultStringFormat(sin.best, sin.eventId)}" + (
                            averageDataList
                                    .firstOrNull { avg -> avg.eventId == sin.eventId }
                                    ?.let { avg -> "|${resultStringFormat(avg.best, avg.eventId)}" }
                                    ?: ""
                            )
                }
        return """${wcaPerson.name}
            #${wcaPerson.id},${wcaPerson.countryId},${if ("m" == wcaPerson.gender) "Male" else "Female"}
            #$result
        """.trimMargin("#")
    }

    fun resultStringFormat(result: Int, eventId: String): String {
        return if (result == -1) {
            "DNF"
        } else if (result == -2) {
            "DNS"
        } else if (result == 0) {
            return ""
        } else if ("333fm" == eventId) {
            if (result > 1000) {
                String.format("%.2f", result.toDouble() / 100)
            } else {
                result.toString()
            }
        } else if ("333mbf" == eventId) {
            val mbfDifference = 99 - result / 10000000
            val mbfMissed = result % 100
            val mbfSolved = mbfDifference + mbfMissed
            val mbfAttempted = mbfSolved + mbfMissed
            val mbfTime = result / 100
            var mbfSec = mbfTime % 10000
            val mbfMin = mbfSec / 60
            mbfSec %= 60
            String.format("%d/%d %d:%02d", mbfSolved, mbfAttempted, mbfMin, mbfSec)
        } else {
            var sec = result / 100
            val msec = result % 100
            if (sec > 59) {
                val min = sec / 60
                sec %= 60
                String.format("%d:%02d.%02d", min, sec, msec)
            } else {
                String.format("%d.%02d", sec, msec)
            }
        }
    }

    fun resultStringFormat(result: Long, eventId: String): String {
        val res = result.toString().toInt()
        return resultStringFormat(res, eventId)
    }

    fun isNumber(str: String): Boolean {
        str.forEach {
            if (!Character.isDigit(it)) return false
        }
        return true
    }

    fun handleWca(userId: Long, q: String, wcaPersonHandler: (WcaPerson) -> (String)): String {
        // 查询自己的成绩，需要绑定
        var userId = userId
        var q = q
        if ("me" == q) {
            val wcaUser: WcaUser = personalService.getWcaUser(userId) ?: return "在console.zbots.vip绑定wca官方账号后使用"
            val me = getExactPerson(wcaUser.wcaId) ?: return "ERROR!"
            return wcaPersonHandler(me)
        }

        // QQ或@ 查询成绩，需要绑定并且开启查询
        q = atDecode(q)
        if (isNumber(q.trim { it <= ' ' })) {
            try {
                userId = q.trim().toLong()
                val wcaUser: WcaUser? = personalService.getWcaUser(userId)
                if (wcaUser != null && wcaUser.open) {
                    val me = getExactPerson(wcaUser.wcaId) ?: return "ERROR!"
                    return wcaPersonHandler(me)
                }
            } catch (e: Exception) {
            }
        }
        val wcaPeople = searchPeople(q)
        if (wcaPeople.isEmpty()) {
            return "NOT FOUND!"
        }
        if (wcaPeople.size == 1) {
            return wcaPersonHandler(wcaPeople[0])
        }
        val keywordArray = q.split(" ").toTypedArray()
        // 如果只有一个精确的，直接返回  包含(q)  (李政)认为是精确
        val exactPersonList = wcaPeople.filter { wcaPerson ->
            for (keyword in keywordArray) {
                if (wcaPerson.name.contains("($keyword)")) {
                    return@filter true
                }
            }
            false
        }
        if (exactPersonList.size == 1) {
            return wcaPersonHandler(exactPersonList[0])
        }


        // 排序，精确在前，中国在前
        wcaPeople.sortedBy { wcaPerson ->
            // 任意关键词 和 名字 精准匹配相同，排在前面
            for (keyword in keywordArray) {
                if (wcaPerson.name.contains("($keyword)")) {
                    return@sortedBy -2
                }
            }
            // 中国排在前面
            if (wcaPerson.countryId == "China") -1 else 0
        }
        if (wcaPeople.size > 99) {
            return "搜索范围太大"
        }
        val resultBuilder = StringBuilder()
        resultBuilder.append(wcaPeople.size).append("items")

        // 正常最多显示5条
        // 如果最后一条和第一条名字相同，那么续一条
        // 外国不续（有很多Anonymous）
        var count = 0
        val firstName = wcaPeople[0].name
        val iterator = wcaPeople.iterator()
        while (iterator.hasNext()) {
            val nextPerson = iterator.next()
            val id = nextPerson.id
            val name = nextPerson.name
            val countryId = nextPerson.countryId
            resultBuilder.append("\n").append(id).append("|").append(name)
            if (++count > 4 && (name != firstName || countryId != "China")) {
                break
            }
        }
        if (iterator.hasNext()) {
            resultBuilder.append("\n...")
        }
        return resultBuilder.toString()
    }

    private fun atDecode(msg: String): String {
        // [mirai:at:875543533,@李政]
        var msg = msg.split('@')[0]
        msg = msg.replace("[mirai:at:", "")
        msg = msg.replace(",", "")
        msg = msg.trim { it <= ' ' }
        return msg
    }

    companion object {
        private val eventOrder = listOf("333", "222", "444", "555", "666", "777", "333bf", "333fm", "333oh", "333ft", "clock", "minx", "pyram", "skewb", "sq1", "444bf", "555bf", "333mbf")
    }
}

class ListResult<T>(
        val data: List<T>? = null,
        val retcode: Int? = null,
        val msg: String? = null
)

class Result<T>(
        val data: T? = null,
        val retcode: Int? = null,
        val msg: String? = null
)


inline fun <reified T : Any> typeRef(): ParameterizedTypeReference<T> = object : ParameterizedTypeReference<T>() {}