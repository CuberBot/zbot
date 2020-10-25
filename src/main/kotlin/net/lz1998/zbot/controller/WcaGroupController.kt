package net.lz1998.zbot.controller

import dto.HttpDto
import net.lz1998.zbot.service.WcaGroupService
import net.lz1998.zbot.service.ZbotService
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.security.core.context.SecurityContextHolder
import org.springframework.web.bind.annotation.*

@CrossOrigin
@RestController
@RequestMapping("/api/wcaGroup")
class WcaGroupController {
    @Autowired
    lateinit var wcaGroupService: WcaGroupService

    @Autowired
    lateinit var zbotService: ZbotService


    @PostMapping("/getWcaUserGroupList", produces = ["application/x-protobuf"], consumes = ["application/x-protobuf"])
    fun getWcaUserGroupList(): HttpDto.GetWcaUserGroupListResp {
        val userId = SecurityContextHolder.getContext().authentication.principal as Long
        val groups = wcaGroupService.getUserGroupList(userId).map {
            val builder = HttpDto.WcaGroupUser.newBuilder()
            builder.groupId = it.groupId
            builder.userId = it.userId
            builder.wcaId = it.wcaId
            builder.name = it.name
            builder.gender = it.gender
            builder.startTime = it.startTime
            builder.endTime = it.endTime
            builder.ban = it.ban
            builder.attend = it.attend
            builder.inGroup = it.inGroup
            builder.groupName = zbotService.getMyGroupInfo(it.groupId)?.groupName ?: "未获取到群名称"
            builder.build()
        }
        return HttpDto.GetWcaUserGroupListResp.newBuilder().addAllGroups(groups).build()
    }

    @PostMapping("/setWcaGroupAttend", produces = ["application/x-protobuf"], consumes = ["application/x-protobuf"])
    fun setWcaGroupAttend(@RequestBody req: HttpDto.SetWcaGroupAttendReq): HttpDto.SetWcaGroupAttendResp {
        val userId = SecurityContextHolder.getContext().authentication.principal as Long
        wcaGroupService.attendWcaGroupRank(req.groupId, userId, req.attend)
        return HttpDto.SetWcaGroupAttendResp.newBuilder().build()
    }
}