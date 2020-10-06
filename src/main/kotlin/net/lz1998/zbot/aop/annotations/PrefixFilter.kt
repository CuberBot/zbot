package net.lz1998.zbot.aop.annotations

@Target(AnnotationTarget.FUNCTION)
@Retention(AnnotationRetention.RUNTIME)
@MustBeDocumented
annotation class PrefixFilter(val value: String)