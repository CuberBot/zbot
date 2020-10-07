package net.lz1998.zbot.security

import io.jsonwebtoken.*
import net.lz1998.zbot.config.JwtConfig
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.context.properties.EnableConfigurationProperties
import org.springframework.context.annotation.Configuration
import org.springframework.data.redis.core.StringRedisTemplate
import org.springframework.stereotype.Component
import org.springframework.util.StringUtils
import java.util.*
import java.util.concurrent.TimeUnit
import javax.servlet.http.HttpServletRequest

/**
 * Jwt工具类,从springboot-demo抄的
 */
@Component
@EnableConfigurationProperties(JwtConfig::class)
@Configuration
class JwtUtil {

    @Autowired
    lateinit var stringRedisTemplate: StringRedisTemplate

    /**
     * 创建JWT
     *
     * @param rememberMe 记住我
     * @param userId     用户id
     * @return JWT
     */
    fun createJWT(rememberMe: Boolean, userId: Long): String {
        val now = Date()
        val builder = Jwts.builder()
                .setId(userId.toString())
                .setSubject(userId.toString())
                .setIssuedAt(now)
                .signWith(SignatureAlgorithm.HS256, JwtConfig.key)

        // 设置过期时间
        val ttl = if (rememberMe) JwtConfig.remember else JwtConfig.ttl
        if (ttl > 0) {
            builder.setExpiration(Date(System.currentTimeMillis() + ttl))
        }
        val jwt = builder.compact()
        // 将生成的JWT保存至Redis
        stringRedisTemplate.opsForValue()[JwtConfig.redisKeyPrefix + userId, jwt, ttl] = TimeUnit.MILLISECONDS
        return jwt
    }

    /**
     * 解析JWT
     *
     * @param jwt JWT
     * @return [Claims]
     */
    fun parseJWT(jwt: String?): Claims {
        return try {
            val claims = Jwts.parser()
                    .setSigningKey(JwtConfig.key)
                    .parseClaimsJws(jwt)
                    .body
            val userId = java.lang.Long.valueOf(claims.id)
            val redisKey = JwtConfig.redisKeyPrefix + userId

            // 校验redis中的JWT是否存在
            val expire = stringRedisTemplate.getExpire(redisKey, TimeUnit.MILLISECONDS)
            if (Objects.isNull(expire) || expire <= 0) {
                throw SecurityException("登陆过期")
            }

            // 校验redis中的JWT是否与当前的一致，不一致则代表用户已注销/用户在不同设备登录，均代表JWT已过期
            val redisToken = stringRedisTemplate.opsForValue()[redisKey]
            if (jwt != redisToken) {
                throw SecurityException("账号在其他地方登陆")
            }
            claims
        } catch (e: ExpiredJwtException) {
            throw SecurityException("Token 已过期")
        } catch (e: UnsupportedJwtException) {
            throw SecurityException("不支持的 Token")
        } catch (e: MalformedJwtException) {
            throw SecurityException("Token 无效")
        } catch (e: SignatureException) {
            throw SecurityException("无效的 Token 签名")
        } catch (e: IllegalArgumentException) {
            throw SecurityException("Token 参数不存在")
        }
    }

    /**
     * 设置JWT过期
     *
     * @param request 请求
     */
    fun invalidateJWT(request: HttpServletRequest) {
        val jwt = getJwtFromRequest(request)
        val userId = getUserIdFromJWT(jwt)
        // 从redis中清除JWT
        stringRedisTemplate.delete(JwtConfig.redisKeyPrefix + userId)
    }

    /**
     * 根据 jwt 获取用户名
     *
     * @param jwt JWT
     * @return 用户名
     */
    fun getUserIdFromJWT(jwt: String?): Long {
        val claims = parseJWT(jwt)
        return java.lang.Long.valueOf(claims.id)
    }

    /**
     * 从 request 的 header 中获取 JWT
     *
     * @param request 请求
     * @return JWT
     */
    fun getJwtFromRequest(request: HttpServletRequest): String? {
        val bearerToken = request.getHeader("Authorization")
        return if (!StringUtils.isEmpty(bearerToken) && bearerToken.startsWith("Bearer ")) {
            bearerToken.substring(7)
        } else null
    }
}