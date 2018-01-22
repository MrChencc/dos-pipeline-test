#!/usr/bin/groovy
import io.fabric8.Utils
import io.fabric8.Fabric8Commands

def call(body) {
    // evaluate the body block, and collect configuration into the object
    def config = [:]
    body.resolveStrategy = Closure.DELEGATE_FIRST
    body.delegate = config
    body()
    def yaml = getKubernetesYaml{
        label = 'dos-proj'
        version = customConfig.version
        pom = customConfig.build.pomfile
    }

}