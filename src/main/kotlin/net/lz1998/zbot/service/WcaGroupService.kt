package net.lz1998.zbot.service

import net.lz1998.zbot.config.ServiceConfig
import net.lz1998.zbot.entity.WcaGroupUser
import net.lz1998.zbot.entity.WcaUser
import net.lz1998.zbot.repository.WcaGroupUserRepository
import net.lz1998.zbot.repository.WcaUserRepository
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.data.domain.Page
import org.springframework.data.domain.Pageable
import org.springframework.stereotype.Service

// TODO 查询时间段内每个人的最好成绩

@Service
class WcaGroupService {
    val groupResultUrl: String get() = "http://${ServiceConfig.wcads}/groupResult"
    val WORLD_START_TIME = 0L //最初时间

    val WORLD_END_TIME = 4102415999000L // 世界末日

    @Autowired
    lateinit var zbotService: ZbotService

    @Autowired
    lateinit var wcaGroupUserRepository: WcaGroupUserRepository

    @Autowired
    lateinit var wcaUserRepository: WcaUserRepository

    // 刷新群成员
    fun refresh(groupId: Long) {
        val bot = zbotService.getBotInstance(groupId) ?: return
        val memberList = bot.getGroupMemberList(groupId)?.groupMemberList ?: return
        val memberIdList = memberList.map { it.userId }

        val groupWcaUserList = wcaUserRepository.findWcaUsersByUserIdIn(memberIdList)
        // 登记过的人的QQ
        val wcaGroupUserIdList = wcaGroupUserRepository.findWcaGroupUsersByGroupId(groupId).map { it.userId }

        deleteUsersNotInGroup(groupId, wcaGroupUserIdList, memberIdList)
        addNewUsersInGroup(groupId, wcaGroupUserIdList, groupWcaUserList)
    }

    // 查询
    fun getWcaGroupUserPage(groupId: Long, pageable: Pageable): Page<WcaGroupUser> {
        return wcaGroupUserRepository.findWcaGroupUsersByGroupIdAndAttendIsTrue(groupId, pageable)
    }

    // 群管理员使用，设置 统计开始时间、结束时间、是否禁止参与
    fun manageWcaGroupUser(groupId: Long, userId: Long, startTime: Long, endTime: Long, ban: Boolean) {
        val wcaGroupUser = wcaGroupUserRepository.findWcaGroupUserByGroupIdAndUserId(groupId, userId) ?: return
        wcaGroupUser.startTime = startTime
        wcaGroupUser.endTime = endTime
        wcaGroupUser.ban = ban
        wcaGroupUserRepository.save(wcaGroupUser)
    }

    // 群成员使用，设置自己是否愿意参与排名
    fun attendWcaGroupRank(groupId: Long, userId: Long, attend: Boolean) {
        val wcaGroupUser = wcaGroupUserRepository.findWcaGroupUserByGroupIdAndUserId(groupId, userId) ?: return
        wcaGroupUser.attend = attend
        wcaGroupUserRepository.save(wcaGroupUser)
    }


    /**
     * 获取参与排名的人，愿意参与且没被禁止
     *
     * @param groupId 群号
     * @return
     */
    fun getWcaGroupUserInRank(groupId: Long): List<WcaGroupUser> {
        return wcaGroupUserRepository.findWcaGroupUsersByGroupIdAndAttendIsTrueAndBanIsFalse(groupId)
    }

    /**
     * 获取自己加了哪些群
     *
     * @return
     */
    fun getUserGroupList(userId: Long): List<WcaGroupUser> {
        return wcaGroupUserRepository.findWcaGroupUsersByUserId(userId)
    }


    /**
     * 删除不在群里的人
     *
     * @param groupId            群号
     * @param wcaGroupUserIdList 登记过的人的QQ
     * @param groupUserIdList    在群里的人QQ
     */
    private fun deleteUsersNotInGroup(groupId: Long, wcaGroupUserIdList: List<Long>, groupUserIdList: List<Long>) {
        // 删除不在群里的人
        val userIdsNotInGroup = wcaGroupUserIdList.filter { !groupUserIdList.contains(it) }
        wcaGroupUserRepository.deleteWcaGroupUsersByGroupIdAndUserIdIn(groupId, userIdsNotInGroup)
    }

    /**
     * 添加新进入群的人
     *
     * @param groupId            群号
     * @param wcaGroupUserIdList 登记过的人的QQ
     * @param groupWcaUserList   群里有WCA ID的所有人
     */
    private fun addNewUsersInGroup(groupId: Long, wcaGroupUserIdList: List<Long>, groupWcaUserList: List<WcaUser>) {
        groupWcaUserList.forEach { wcaUser: WcaUser ->
            if (!wcaGroupUserIdList.contains(wcaUser.userId)) {
                val wcaGroupUser = WcaGroupUser(
                        groupId = groupId,
                        userId = wcaUser.userId,
                        wcaId = wcaUser.wcaId,
                        name = wcaUser.name,
                        gender = wcaUser.gender,
                        attend = wcaUser.defaultAttend,
                        ban = false,
                        inGroup = true,
                        startTime = WORLD_START_TIME,
                        endTime = WORLD_END_TIME,
                )
                wcaGroupUserRepository.save(wcaGroupUser)
            }
        }
    }
}