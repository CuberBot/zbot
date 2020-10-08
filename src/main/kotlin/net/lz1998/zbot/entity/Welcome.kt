package net.lz1998.zbot.entity

import org.hibernate.annotations.UpdateTimestamp
import java.util.*
import javax.persistence.Column
import javax.persistence.Entity
import javax.persistence.Id
import javax.persistence.Table

@Entity
@Table(name = "zbot_welcome")
data class Welcome(
        @Id
        @Column(columnDefinition = "BIGINT NOT NULL COMMENT '群号'")
        var groupId: Long = 0,

        @Column(columnDefinition = "VARCHAR(1024) NOT NULL DEFAULT '' COMMENT '欢迎内容'")
        var welcomeMsg: String = "",

        @Column(columnDefinition = "BIGINT NOT NULL COMMENT '设置人QQ'")
        var adminId: Long = 0,

        @UpdateTimestamp
        @Column(columnDefinition = "TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间'")
        var gmtModified: Date? = null
)