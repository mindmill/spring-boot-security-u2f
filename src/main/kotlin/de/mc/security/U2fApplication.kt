package de.mc.security

import com.yubico.u2f.U2F
import org.slf4j.LoggerFactory
import org.springframework.boot.SpringApplication
import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.context.annotation.Bean

@SpringBootApplication
class U2fApplication

fun main(args: Array<String>) {
    SpringApplication.run(U2fApplication::class.java, *args)
}


/**
 * simple shortcut
 */
fun Any.getLogger() = LoggerFactory.getLogger(javaClass)