package net.lz1998.zbot.controller

import dto.HttpDto
import net.lz1998.zbot.service.PersonalService
import net.lz1998.zbot.utils.toDto
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.security.core.context.SecurityContextHolder
import org.springframework.web.bind.annotation.*

@RestController
@RequestMapping("/api/personal")
@CrossOrigin
class PersonalController {
    @Autowired
    lateinit var personalService: PersonalService

    @PostMapping("/setWcaCode", produces = ["application/x-protobuf"], consumes = ["application/x-protobuf"])
    fun setWcaCode(@RequestBody param: HttpDto.SetWcaCodeReq): HttpDto.SetWcaCodeResp {
        val wcaUser = personalService.setWcaCode(param.code, param.redirectUri)
        val msg = if (wcaUser == null) "设置失败" else "设置成功"
        return HttpDto.SetWcaCodeResp.newBuilder().setMsg(msg).setWcaUser(wcaUser?.toDto()).build()
    }

    @PostMapping("/getWcaUser", produces = ["application/x-protobuf"], consumes = ["application/x-protobuf"])
    fun getWcaUser(): HttpDto.GetWcaUserResp {
        val userId = SecurityContextHolder.getContext().authentication.principal as Long
        val wcaUser = personalService.getWcaUser(userId)
        return HttpDto.GetWcaUserResp.newBuilder().setWcaUser(wcaUser?.toDto()).build()
    }
}