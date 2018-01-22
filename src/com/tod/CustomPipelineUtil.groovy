package com.tod

import groovy.json.JsonSlurper

/**
 * 自定义类型Pipeline 工具
 */
class CustomPipelineUtil {
/**
 * 生成Json配置文件
 * @param configStr 配置文件
 * @return
 */
    static def getJsonPipelineConfig(String configStr) {
        def jsonSlurper = new JsonSlurper()
        def configJson = jsonSlurper.parseText(configStr)
        return tryTransMap(configJson);
    }

    private static def tryTransMap(def oldVal) {
        if (!(oldVal instanceof Map)) {
            return oldVal
        }
        Map map = oldVal;
        Map newMap = new HashMap();
        String[] mapKey = map.keySet()
        for (String mKey : mapKey) {
            newMap.put(mKey, tryTransMap(map.get(mKey)))
        }
        return newMap
    }
}