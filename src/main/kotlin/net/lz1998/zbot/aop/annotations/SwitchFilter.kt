package net.lz1998.zbot.aop.annotations

@Target(AnnotationTarget.CLASS)
@Retention(AnnotationRetention.RUNTIME)
@MustBeDocumented
annotation class SwitchFilter(val value: String)