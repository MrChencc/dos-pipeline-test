package com.tod

import com.cloudbees.groovy.cps.NonCPS
import groovy.json.JsonSlurperClassic

/**
 * 生成Json配置文件
 * @param configStr 配置文件
 * @return
 */
def getJsonPipelineConfig(String configStr) {
    def jsonSlurper = new JsonSlurperClassic()
    def configJson = jsonSlurper.parseText(configStr)
    return configJson
}
