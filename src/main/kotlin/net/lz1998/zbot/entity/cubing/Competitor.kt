package net.lz1998.zbot.entity.cubing

import kotlinx.serialization.Serializable

@Serializable
data class CompetitorInfo(
        var name: String,
        var gender: String,
        var wcaid: String,
        var region: String
)

@Serializable
data class Competitor(
        var number: Int,
        var competitor: CompetitorInfo,
        var events: List<String>
)

@Serializable
data class CompetitorResponse(
        var status: Int,
        var data: List<Competitor>,
        var message: String
)


