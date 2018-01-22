package com.tod

import com.cloudbees.groovy.cps.NonCPS
import groovy.json.JsonSlurperClassic
import groovy.json.internal.LazyMap


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