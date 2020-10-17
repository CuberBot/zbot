package net.lz1998.zbot.entity.cubing

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class CompetitionDateRange(
        var from: Int = 0, // 10位时间戳
        var to: Int = 0 // 10位时间戳
)

@Serializable
data class CompetitionLocation(
        @SerialName("competition_id") var competitionId: Int = 0,
        @SerialName("location_id") var locationId: Int = 0,
        var province: String = "",
        var city: String = "",
        var venue: String = "",
        var longitude: Double = 0.0,
        var latitude: Double = 0.0
)

@Serializable
data class Competition(
        var id: Int = 0,
        var name: String = "",
        var type: String = "",
        var alias: String = "",
        var url: String = "",
        var date: CompetitionDateRange,
        var locations: List<CompetitionLocation>,
        var registration: CompetitionDateRange,
        @SerialName("competitor_limit") var competitorLimit: Int = 0,
        @SerialName("registered_competitors") var registeredCompetitors: Int = 0
)

@Serializable
data class CompetitionResponse(
        var status: Int = 0,
        var message: String = "",
        var data: List<Competition>
)