#!/usr/bin/groovy

def call(Map parameters = [:], body) {

    def defaultLabel = "clients.${env.JOB_NAME}.${env.BUILD_NUMBER}".replace('-', '_').replace('/', '_')
    def label = parameters.get('label', defaultLabel)

    def nodeVersion = parameters.get('nodeVersion',"8.9.3");
    def clientsImage = parameters.get('clientsImage', 'fabric8/builder-clients:0.1')
    def buildImage = parameters.get('buildImage', "registry.timeondata.com/dos/jenkins-slave-nodejs:${nodeVersion}")
    def inheritFrom = parameters.get('inheritFrom', 'base')

    def flow = new io.fabric8.Fabric8Commands()

    echo 'Mounting docker socket to build docker images'
    podTemplate(label: label, inheritFrom: "${inheritFrom}",
            containers: [
                    [name: 'builder', image: "${buildImage}", command: 'cat', ttyEnabled: true],
                    [name: 'clients', image: "${clientsImage}", command: 'cat', ttyEnabled: true, privileged: true]
                   ],
            volumes: [persistentVolumeClaim(claimName: 'jenkins-nodejs-repository', mountPath: '/home/jenkins/.npm'),
                      secretVolume(secretName: 'jenkins-docker-cfg', mountPath: '/home/jenkins/.docker'),
                      secretVolume(secretName: 'jenkins-release-gpg', mountPath: '/home/jenkins/.gnupg'),
                      secretVolume(secretName: 'jenkins-hub-api-token', mountPath: '/home/jenkins/.apitoken'),
                      secretVolume(secretName: 'jenkins-ssh-config', mountPath: '/root/.ssh'),
                      secretVolume(secretName: 'jenkins-git-ssh', mountPath: '/root/.ssh-git'),
                      hostPathVolume(hostPath: '/var/run/docker.sock', mountPath: '/var/run/docker.sock')],
            envVars: [[key: 'DOCKER_HOST', value: 'unix:/var/run/docker.sock'], [key: 'DOCKER_CONFIG', value: '/home/jenkins/.docker/']]
    ) {

        body(

        )
    }

}
