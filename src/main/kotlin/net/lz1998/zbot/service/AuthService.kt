package net.lz1998.zbot.service

import net.lz1998.zbot.entity.Auth
import net.lz1998.zbot.repository.AuthRepository
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.cache.annotation.CacheConfig
import org.springframework.cache.annotation.CachePut
import org.springframework.cache.annotation.Cacheable
import org.springframework.data.jpa.repository.Modifying
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

@Service
@CacheConfig(cacheNames = ["auth"])
class AuthService {
    @Autowired
    lateinit var authRepository: AuthRepository

    @Cacheable(key = "#groupId")
    fun isAuth(groupId: Long): Boolean {
        return authRepository.findAuthByGroupId(groupId)?.isAuth ?: false
    }

    @Transactional
    @Modifying
    fun isDelete(groupId: Long) {
        return authRepository.deleteAuthByGroupId(groupId)
    }

    @CachePut(key = "#groupId")
    fun setAuth(groupId: Long, isAuth: Boolean, adminId: Long): Boolean =
            authRepository.save(Auth(
                    groupId = groupId,
                    isAuth = isAuth,
                    adminId = adminId
            )).isAuth
}