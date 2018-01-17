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
    sh 'source /etc/profile'
    sh 'mvn -f pom-project-generator.xml clean package -DskipTests'
    sh "mvn org.codehaus.mojo:versions-maven-plugin:2.2:set -U -DnewVersion=${config.version}"
    sh "mvn clean -e -U deploy"

    def s2iMode = flow.isOpenShiftS2I()
    echo "s2i mode: ${s2iMode}"

//    if (flow.isSingleNode()){
//        echo 'Running on a single node, skipping docker push as not needed'
//        def m = readMavenPom file: 'pom.xml'
//        def groupId = m.groupId.split( '\\.' )
//        def user = groupId[groupId.size()-1].trim()
//        def artifactId = m.artifactId
//
//       if (!s2iMode) {
//           sh "docker tag ${user}/${artifactId}:${config.version} ${env.FABRIC8_DOCKER_REGISTRY_SERVICE_HOST}:${env.FABRIC8_DOCKER_REGISTRY_SERVICE_PORT}/${user}/${artifactId}:${config.version}"
//       }
//    } else {
      if (!s2iMode) {
          def utils = new Utils()
          def namespace = utils.getNamespace()
        retry(3){
            sh "sudo docker login  registry.timeondata.com -uguanshengyang -pGuanYang1"
            sh "mvn fabric8:push -Ddocker.push.registry=registry.timeondata.com"
//            sh 'sudo docker rmi -f registry.timeondata.com/dos/appprojserver:latest_test'
        }
      }
//    }

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