package net.lz1998.zbot.security

import org.springframework.http.HttpStatus
import org.springframework.security.core.AuthenticationException
import org.springframework.security.web.AuthenticationEntryPoint
import org.springframework.stereotype.Component
import javax.servlet.http.HttpServletRequest
import javax.servlet.http.HttpServletResponse

/**
 * 未登录处理
 */
@Component
class MyAuthenticationEntryPoint : AuthenticationEntryPoint {
    override fun commence(httpServletRequest: HttpServletRequest, response: HttpServletResponse, e: AuthenticationException) {
        response.setHeader("Access-Control-Allow-Origin", "*")
        response.setHeader("Access-Control-Allow-Methods", "*")
        response.contentType = "application/json;charset=UTF-8"
        response.status = HttpStatus.UNAUTHORIZED.value()
        response.writer.print(HttpStatus.UNAUTHORIZED.reasonPhrase)
    }
}