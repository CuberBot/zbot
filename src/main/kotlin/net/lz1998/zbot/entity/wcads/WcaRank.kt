package net.lz1998.zbot.entity.wcads

data class WcaRank(
        val id: Long = 0,
        val personId: String = "",
        val eventId: String = "",
        val best: Int = 0,
        val worldRank: Int = 0,
        val continentRank: Int = 0,
        val countryRank: Int = 0
)