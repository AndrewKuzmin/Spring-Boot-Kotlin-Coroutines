package com.example.demo.config

import org.slf4j.Logger
import org.slf4j.LoggerFactory

/**
 * Created by GEDC_AAKuzmin on 26.05.2020.
 */
abstract class Log {
    val logger: Logger = LoggerFactory.getLogger(this.javaClass)
}