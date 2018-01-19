#!/usr/bin/groovy
import io.fabric8.Utils

def call(body) {
    // evaluate the body block, and collect configuration into the object
    def config = [:]
    body.resolveStrategy = Closure.DELEGATE_FIRST
    body.delegate = config
    body()

    def flow = new io.fabric8.Fabric8Commands()

    sh "git checkout -b ${env.JOB_NAME}-${config.version}"
    sh "mvn org.codehaus.mojo:versions-maven-plugin:2.2:set -U -DnewVersion=${config.version}"
//    sh "mvn clean -f pom-project-generator.xml -e -U deploy"

    def s2iMode = flow.isOpenShiftS2I()
    echo "s2i mode: ${s2iMode}"

        def m = readMavenPom file: 'PROJECT-GENERATOR/pom.xml'
        def groupId = m.parent.groupId.split( '\\.' )
        def user = groupId[groupId.size()-1].trim()
        def artifactId = m.artifactId.toLowerCase()
        def version = m.version

        def fabric8Registry = env.FABRIC8_DOCKER_REGISTRY_SERVICE_HOST+':'+env.FABRIC8_DOCKER_REGISTRY_SERVICE_PORT+'/'

    if (!s2iMode) {
        retry(1){
            sh "docker tag ${user}/${artifactId}:${version} ${fabric8Registry}dos/proj-test:latest"
            sh "docker push  ${fabric8Registry}dos/proj-test:latest"
            sh "docker rmi -f ${user}/${artifactId}:${version}"
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
