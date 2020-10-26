package net.lz1998.zbot.controller

import dto.HttpDto
import net.lz1998.zbot.service.WcaGroupService
import net.lz1998.zbot.service.ZbotService
import net.lz1998.zbot.service.PermissionService
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

    @Autowired
    lateinit var permissionService: PermissionService


    // 用户查询自己加了哪些群
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

    // 用户设置自己是否参与群排名
    @PostMapping("/setWcaGroupAttend", produces = ["application/x-protobuf"], consumes = ["application/x-protobuf"])
    fun setWcaGroupAttend(@RequestBody req: HttpDto.SetWcaGroupAttendReq): HttpDto.SetWcaGroupAttendResp {
        val userId = SecurityContextHolder.getContext().authentication.principal as Long
        wcaGroupService.attendWcaGroupRank(req.groupId, userId, req.attend)
        return HttpDto.SetWcaGroupAttendResp.newBuilder().build()
    }

    // 获取一个群所有WCA用户
    @PostMapping("/getWcaGroupUsers", produces = ["application/x-protobuf"], consumes = ["application/x-protobuf"])
    fun getWcaGroupUsers(@RequestBody req: HttpDto.GetWcaGroupUsersReq): HttpDto.GetWcaGroupUsersResp {
        wcaGroupService.refresh(req.groupId)
        val groupName = zbotService.getMyGroupInfo(req.groupId)?.groupName ?: "未获取到群名称"
        val userList = wcaGroupService.getWcaGroupUser(req.groupId).map {
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
            builder.groupName = groupName
            builder.build()
        }
        val operatorId = SecurityContextHolder.getContext().authentication.principal as Long
        val isGroupAdmin = permissionService.isGroupAdmin(req.groupId, operatorId)
        return HttpDto.GetWcaGroupUsersResp.newBuilder().setGroupName(groupName).setCanUpdate(isGroupAdmin).addAllUsers(userList).build()
    }

    // 管理员设置用户 统计开始时间、结束时间、是否禁止参与统计
    @PostMapping("/updateWcaGroupUser", produces = ["application/x-protobuf"], consumes = ["application/x-protobuf"])
    fun updateWcaGroupUser(@RequestBody req: HttpDto.UpdateWcaGroupUserReq): HttpDto.UpdateWcaGroupUserResp {
        val wcaUser = req.user
        val operatorId = SecurityContextHolder.getContext().authentication.principal as Long
        if (permissionService.isGroupAdmin(wcaUser.groupId, operatorId)) {
            wcaGroupService.manageWcaGroupUser(wcaUser.groupId, wcaUser.userId, wcaUser.startTime, wcaUser.endTime, wcaUser.ban)
        }
        // TODO 403 提示
        return HttpDto.UpdateWcaGroupUserResp.newBuilder().build()
    }
}