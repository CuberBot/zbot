package net.lz1998.zbot.utils

import dto.HttpDto
import net.lz1998.zbot.entity.WcaUser

fun WcaUser.toDto(): HttpDto.WcaUser = HttpDto.WcaUser.newBuilder()
        .setUserId(this.userId)
        .setWcaId(this.wcaId)
        .setName(this.name)
        .setGender(this.gender)
        .setOpen(this.open)
        .setDefaultAttend(this.defaultAttend)
        .setEnabled(this.enabled)
        .build()