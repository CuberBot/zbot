package net.lz1998.zbot.entity.wcads

data class PersonResultItem(
        var eventId: String = "",
        var singleResult: Long = 0,
        var averageResult: Long = 0
)

data class PersonResult(
        var wcaId: String = "",
        var name: String = "",
        var personResultItemList: List<PersonResultItem>? = null
)

data class PersonResultResponse(
        var data: List<PersonResult>? = null
)