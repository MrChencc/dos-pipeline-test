#!/usr/bin/groovy
def call(Map parameters = [:], body) {

    def defaultLabel = "custom.${env.JOB_NAME}.${env.BUILD_NUMBER}".replace('-', '_').replace('/', '_')
    def label = parameters.get('label', defaultLabel)

    customTemplate(parameters) {
        node(label) {
            body()
        }
    }
}
