package net.lz1998.zbot.controller

import dto.HttpDto
import net.lz1998.zbot.service.UserService
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.web.bind.annotation.*

@RestController
@RequestMapping("/api/security")
@CrossOrigin
class SecurityController {
    @Autowired
    lateinit var userService: UserService

    @PostMapping("/login", produces = ["application/x-protobuf"], consumes = ["application/x-protobuf"])
    fun login(@RequestBody param: HttpDto.LoginReq): HttpDto.LoginResp {
        val userId = param.userId
        val password = param.password
        val token: String? = userService.login(userId, password)
        return if (token != null) {
            HttpDto.LoginResp.newBuilder().setType(HttpDto.LoginResp.Type.Token).setResult(token).build()
        } else {
            // 登陆失败，注册/修改密码，获取验证码
            val verificationCode = userService.setTmpUser(userId, password)
            HttpDto.LoginResp.newBuilder().setType(HttpDto.LoginResp.Type.CaptchaCode).setResult(verificationCode).build()
        }
    }
}