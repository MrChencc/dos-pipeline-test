package com.tod

import com.cloudbees.groovy.cps.NonCPS
import groovy.json.JsonSlurperClassic


/**
 * 自定义类型Pipeline 工具
 */
class CustomPipelineUtil {
/**
 * 生成Json配置文件
 * @param configStr 配置文件
 * @return
 */
    @NonCPS
    static def getJsonPipelineConfig(String configStr) {
        def jsonSlurper = new JsonSlurperClassic()
        def configJson = jsonSlurper.parseText(configStr)
        return new HashMap<>(configJson)
    }
}