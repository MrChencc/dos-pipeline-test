#!/usr/bin/groovy
import io.fabric8.Utils

def call(body) {
    // evaluate the body block, and collect configuration into the object
    def config = [:]
    body.resolveStrategy = Closure.DELEGATE_FIRST
    body.delegate = config
    body()

    def flow = new io.fabric8.Fabric8Commands()

//    sh "git checkout -b ${env.JOB_NAME}-${config.version}"
//    sh "mvn org.codehaus.mojo:versions-maven-plugin:2.2:set -U -DnewVersion=${config.version}" // 更新统一的版本
//    sh "mvn clean -f ${config.bulidPom} -e -U deploy" // 推送jar包到nexus

    def s2iMode = flow.isOpenShiftS2I()
    echo "s2i mode: ${s2iMode}"

    def m = readMavenPom file: "${config.pom}"
    def groupId
    if (m.groupId == null){
        groupId = m.parent.groupId.split( '\\.' )
    } else {
        groupId = m.groupId.split( '\\.' )
    }
    def user = groupId[groupId.size()-1].trim()
    def artifactId = m.artifactId.toLowerCase()
    def version = m.version

    def fabric8Registry = env.FABRIC8_DOCKER_REGISTRY_SERVICE_HOST+':'+env.FABRIC8_DOCKER_REGISTRY_SERVICE_PORT+'/'

    if (!s2iMode) {
        retry(1){
            sh "docker build -f ${config.dockerfilePath} -t ${fabric8Registry}${user}/${artifactId}:${config.version} ."
            sh "docker push  ${fabric8Registry}${user}/${artifactId}:${config.version}"
//            sh "docker rmi -f ${user}/${artifactId}:${version}"
        }
      }

    if (flow.hasService("content-repository")) {
      try {
        sh 'mvn site site:deploy'
      } catch (err) {
        // lets carry on as maven site isn't critical
        echo 'unable to generate maven site'
      }
    } else {
      echo 'no content-repository service so not deploying the maven site report'
    }
  }
