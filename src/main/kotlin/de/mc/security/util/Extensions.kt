package de.mc.security.util

import org.slf4j.LoggerFactory

/**
 * @author Ralf Ulrich
 * 02.09.17
 */
fun Any.getLogger() = LoggerFactory.getLogger(javaClass)