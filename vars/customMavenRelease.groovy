#!/usr/bin/groovy
import io.fabric8.Utils

def call(body) {
    // evaluate the body block, and collect configuration into the object
    def config = [:]
    body.resolveStrategy = Closure.DELEGATE_FIRST
    body.delegate = config
    body()

//    def flow = new io.fabric8.Fabric8Commands()

//    sh "git checkout -b ${env.JOB_NAME}-${config.version}"
//    sh "mvn org.codehaus.mojo:versions-maven-plugin:2.2:set -U -DnewVersion=${config.version}" // 更新统一的版本
//    sh "mvn clean -f ${config.bulidPom} -e -U deploy" // 推送jar包到nexus

//    def s2iMode = flow.isOpenShiftS2I()
//    echo "s2i mode: ${s2iMode}"

    def m = readMavenPom file: "${config.pom}"
    def groupId
    if (m.groupId == null) {
        groupId = m.parent.groupId.split('\\.')
    } else {
        groupId = m.groupId.split('\\.')
    }
    def user = groupId[groupId.size() - 1].trim()
    def artifactId = m.artifactId.toLowerCase()
    def version = m.version

    def fabric8Registry = env.FABRIC8_DOCKER_REGISTRY_SERVICE_HOST + ':' + env.FABRIC8_DOCKER_REGISTRY_SERVICE_PORT + '/'
    def docker_image = "${fabric8Registry}${user}/${artifactId}:${config.version}"
    sh "docker build -f ${config.dockerfilePath} -t ${docker_image} ."
    retry(2) {
        sh "docker push ${docker_image}"
        sh "docker rmi ${docker_image}"
    }

    return docker_image
}
