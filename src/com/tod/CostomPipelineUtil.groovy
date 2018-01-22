package com.tod

import com.cloudbees.groovy.cps.NonCPS
import groovy.json.JsonSlurper;


/**
 * 自定义类型Pipeline 工具
 */
class CostomPipelineUtil {
/**
 * 生成Json配置文件
 * @param configStr 配置文件
 * @return
 */
    @NonCPS
    static def getJsonPipelineConfig(def configStr) {
        def jsonSlurper = new JsonSlurper()
        def configJson = jsonSlurper.parseText(configStr)
        if(configJson instanceof groovy.json.internal.LazyMap) {
            return new HashMap<>(configJson)
        }
        return configJson
    }
}