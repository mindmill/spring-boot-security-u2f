package com.example

import de.mc.security.U2fApplication
import org.junit.Test
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.test.context.web.WebAppConfiguration

@SpringBootTest(classes = arrayOf(U2fApplication::class))
@WebAppConfiguration
class SpringBootSecurityU2fApplicationTests {

    @Test
    fun contextLoads() {
    }

}
