#!/usr/bin/groovy
import io.fabric8.Utils

def call(body) {
    // evaluate the body block, and collect configuration into the object
    def config = [:]
    body.resolveStrategy = Closure.DELEGATE_FIRST
    body.delegate = config
    body()


    def utils = new Utils()
    def user =  utils.getNamespace()
    def artifactId = env.JOB_NAME.toLowerCase().replace('_', '-').replace('/', '-')

    def fabric8Registry = env.FABRIC8_DOCKER_REGISTRY_SERVICE_HOST + ':' + env.FABRIC8_DOCKER_REGISTRY_SERVICE_PORT + '/'
    def docker_image = "${fabric8Registry}${user}/${artifactId}:${config.version}"
    sh "docker build -f ${config.dockerfilePath} -t ${docker_image} ."
    retry(2) {
        sh "docker push ${docker_image}"
        sh "docker rmi ${docker_image}"
        echo "~#####*****${docker_image}*****#####~"
    }

    return docker_image
}
