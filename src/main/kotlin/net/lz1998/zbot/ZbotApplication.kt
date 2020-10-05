package net.lz1998.zbot

import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.runApplication

@SpringBootApplication
class ZbotApplication

fun main(args: Array<String>) {
    runApplication<ZbotApplication>(*args)
}
