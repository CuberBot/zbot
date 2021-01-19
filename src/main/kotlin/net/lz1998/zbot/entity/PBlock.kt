package net.lz1998.zbot.entity

import org.hibernate.annotations.UpdateTimestamp
import java.io.Serializable
import java.util.*
import javax.persistence.Column
import javax.persistence.Entity
import javax.persistence.Id
import javax.persistence.Table

data class PBlockKey(
    val userId: Long = 0
) : Serializable

@Entity
@Table(name = "zbot_pblock")
class PBlock (
    @Id
    @Column(columnDefinition = "BIGINT NOT NULL COMMENT '屏蔽人QQ'")
    val userId: Long = 0,
    @Column(columnDefinition = "BOOLEAN NOT NULL DEFAULT false COMMENT '是否已屏蔽'")
    val isPBlock: Boolean = false,
    @Column(columnDefinition = "BIGINT NOT NULL COMMENT '操作人QQ'")
    val adminId: Long = 0,
    @UpdateTimestamp
    @Column(columnDefinition = "TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间'")
    val gmtModified: Date? = null
)