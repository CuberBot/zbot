package net.lz1998.zbot.entity

import org.hibernate.annotations.UpdateTimestamp
import java.io.Serializable
import java.util.*
import javax.persistence.*

data class LearnKey(
        val groupId: Long = 0,
        val ask: String = ""
) : Serializable

@Entity
@IdClass(LearnKey::class)
@Table(name = "zbot_learn")
data class Learn(
        @Id
        @Column(columnDefinition = "BIGINT NOT NULL COMMENT '群号'")
        var groupId: Long = 0,

        @Id
        @Column(columnDefinition = "VARCHAR(512) NOT NULL DEFAULT '' COMMENT '问内容'")
        var ask: String = "",

        @Column(columnDefinition = "VARCHAR(2048) NOT NULL DEFAULT '' COMMENT '答内容'")
        var answer: String = "",

        @Column(columnDefinition = "BIGINT NOT NULL COMMENT '设置人QQ'")
        var adminId: Long = 0,

        @UpdateTimestamp
        @Column(columnDefinition = "TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间'")
        var gmtModified: Date? = null
)