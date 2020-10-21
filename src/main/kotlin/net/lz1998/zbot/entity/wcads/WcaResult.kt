package net.lz1998.zbot.entity.wcads

data class WcaResult(
        var average: Int = 0,
        var best: Int = 0,
        var competitionId: String = "",
        var eventId: String = "",
        var formatId: String = "",
        var personCountryId: String = "",
        var personId: String = "",
        var personName: String = "",
        var pos: Int = 0,
        var roundTypeId: String = "",
        var value1: Int = 0,
        var value2: Int = 0,
        var value3: Int = 0,
        var value4: Int = 0,
        var value5: Int = 0,
)