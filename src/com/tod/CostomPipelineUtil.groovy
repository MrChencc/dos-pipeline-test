package com.tod;

import groovy.json.JsonSlurper

/**
 * 自定义类型Pipeline 工具
 */
class CostomPipelineUtil {
/**
 * 生成Json配置文件
 * @param configStr 配置文件
 * @return
 */
    static def getJsonPipelineConfig(def configStr) {
        def jsonSlurper = new JsonSlurper()
        def configJson = jsonSlurper.parseText(configStr)
        return configJson;
    }
}