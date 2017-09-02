package de.mc.security.controller

import org.springframework.stereotype.Controller
import org.springframework.web.bind.annotation.RequestMapping

/**
 * @author Ralf Ulrich
 * 02.09.17
 */
@Controller
class SignupController {


    @RequestMapping(value = "/signup")
    fun signup() = "/signup"



}