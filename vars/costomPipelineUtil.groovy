import groovy.json.JsonSlurper

/**
 * 生成Json配置文件
 * @param configStr 配置文件
 * @return
 */
def getJsonPipelineConfig(def configStr) {
    def jsonSlurper = new JsonSlurper()
    def configJson = jsonSlurper.parseText(configStr)
    echo configJson.toString();
    return configJson;
}
