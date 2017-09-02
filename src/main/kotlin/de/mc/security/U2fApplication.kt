package de.mc.security

import com.yubico.u2f.U2F
import org.springframework.boot.SpringApplication
import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.context.annotation.Bean

@SpringBootApplication
class U2fApplication {

    @Bean
    fun u2f() = U2F()
}

fun main(args: Array<String>) {
    SpringApplication.run(U2fApplication::class.java, *args)
}
