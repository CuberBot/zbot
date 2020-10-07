package net.lz1998.zbot.security

import org.springframework.beans.factory.annotation.Autowired
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken
import org.springframework.security.core.context.SecurityContextHolder
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource
import org.springframework.stereotype.Component
import org.springframework.util.StringUtils
import org.springframework.web.filter.OncePerRequestFilter
import javax.servlet.FilterChain
import javax.servlet.http.HttpServletRequest
import javax.servlet.http.HttpServletResponse

@Component
class JwtAuthenticationFilter : OncePerRequestFilter() {
    @Autowired
    lateinit var jwtUtil: JwtUtil
    override fun doFilterInternal(request: HttpServletRequest, response: HttpServletResponse, filterChain: FilterChain) {
        val jwt = jwtUtil.getJwtFromRequest(request)
        if (!StringUtils.isEmpty(jwt)) {
            try {
                val userId = jwtUtil.getUserIdFromJWT(jwt)
                // 必须写第三个参数，否则authenticated是false，没办法set
                val authentication = UsernamePasswordAuthenticationToken(userId, null, null)
                authentication.details = WebAuthenticationDetailsSource().buildDetails(request)
                SecurityContextHolder.getContext().authentication = authentication
                filterChain.doFilter(request, response)
            } catch (e: SecurityException) {
                filterChain.doFilter(request, response)
            }
        } else {
            filterChain.doFilter(request, response)
        }
    }
}