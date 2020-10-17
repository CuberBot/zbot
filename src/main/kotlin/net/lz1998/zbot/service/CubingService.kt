package net.lz1998.zbot.service

import kotlinx.serialization.json.Json
import net.lz1998.zbot.config.ServiceConfig
import net.lz1998.zbot.entity.cubing.Competition
import net.lz1998.zbot.entity.cubing.CompetitionResponse
import net.lz1998.zbot.entity.cubing.Competitor
import net.lz1998.zbot.entity.cubing.CompetitorResponse
import org.springframework.stereotype.Service
import java.net.URL

@Service
class CubingService {

    val competitionUrl: String get() = "https://${ServiceConfig.cubing}/api/v0/competition"
    var cachedCompetitionList = listOf<Competition>()
    var cachedCompetitorListMap = mutableMapOf<String, List<Competitor>>()
    var lastCompetitionRefreshTime = 0L // 上次赛事刷新时间
    var lastCompetitorRefreshTime = 0L // 上次选手刷新时间
    var refreshInterval = 3600000L // 刷新间隔 1小时

    var json: Json = Json {
        isLenient = true
        ignoreUnknownKeys = true
    }

    @Synchronized
    fun getCompetitionList(): List<Competition> {
        val now = System.currentTimeMillis()
        if (now - lastCompetitionRefreshTime > refreshInterval) {
            lastCompetitionRefreshTime = now
            val result = URL(competitionUrl).readText()
            val response = json.decodeFromString(CompetitionResponse.serializer(), result)
            cachedCompetitionList = response.data
        }
        return cachedCompetitionList
    }

    @Synchronized
    fun getCompetitorList(competitionAlias: String): List<Competitor> {
        val now = System.currentTimeMillis()
        if (now - lastCompetitorRefreshTime > refreshInterval) {
            lastCompetitorRefreshTime = now
            cachedCompetitorListMap.clear()
        }
        return cachedCompetitorListMap.getOrDefault(competitionAlias, null) ?: {
            val result = URL("${competitionUrl}/${competitionAlias}/competitors").readText()
            val response = json.decodeFromString(CompetitorResponse.serializer(), result)
            cachedCompetitorListMap[competitionAlias] = response.data
            response.data
        }()
    }
}