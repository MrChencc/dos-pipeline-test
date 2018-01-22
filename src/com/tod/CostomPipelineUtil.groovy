package com.tod

import groovy.json.JsonSlurperClassic;


/**
 * 自定义类型Pipeline 工具
 */
class CostomPipelineUtil {
/**
 * 生成Json配置文件
 * @param configStr 配置文件
 * @return
 */
    static def getJsonPipelineConfig(String configStr) {
        def jsonSlurper = new JsonSlurperClassic()
        def configJson = jsonSlurper.parseText(configStr.toString())
        return configJson
    }
}