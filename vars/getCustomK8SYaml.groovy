#!/usr/bin/groovy
import com.tod.K8sJsonUtil

def call(body) {
    // evaluate the body block, and collect configuration into the object
    def config = [:]
    body.resolveStrategy = Closure.DELEGATE_FIRST
    body.delegate = config
    body()
    echo config.deployment
    def yaml = K8sJsonUtil.getK8sJson(config, env);
    echo yaml
    return yaml
}