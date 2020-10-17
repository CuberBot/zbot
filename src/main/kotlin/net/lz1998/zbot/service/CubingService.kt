package net.lz1998.zbot.service

import kotlinx.serialization.json.Json
import net.lz1998.zbot.config.ServiceConfig
import net.lz1998.zbot.entity.cubing.Competition
import net.lz1998.zbot.entity.cubing.CompetitionResponse
import org.springframework.stereotype.Service
import java.net.URL

@Service
class CubingService {

    val competitionUrl: String get() = "https://${ServiceConfig.cubing}/api/v0/competition"
    var cachedCompetitionList: List<Competition>? = null // 比赛列表缓存
    var lastRefreshTime = 0L // 上次刷新时间
    var refreshInterval = 3600000L // 刷新间隔 1小时

    var json: Json = Json {
        isLenient = true
        ignoreUnknownKeys = true
    }

    @Synchronized
    fun getCompetitionList(): List<Competition> {
        val now = System.currentTimeMillis()
        if (now - lastRefreshTime > refreshInterval || cachedCompetitionList == null) {
            lastRefreshTime = now
            val result = URL(competitionUrl).readText()
            val response = json.decodeFromString(CompetitionResponse.serializer(), result)
            cachedCompetitionList = response.data
        }
        return cachedCompetitionList ?: listOf()
    }
}