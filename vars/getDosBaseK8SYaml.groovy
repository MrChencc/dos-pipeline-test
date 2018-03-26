#!/usr/bin/groovy
import io.fabric8.Utils
import io.fabric8.Fabric8Commands

def call(body) {
    // evaluate the body block, and collect configuration into the object
    def config = [:]
    body.resolveStrategy = Closure.DELEGATE_FIRST
    body.delegate = config
    body()

    def expose = config.exposeApp ?: 'true'
    def requestCPU = config.resourceRequestCPU ?: '0'
    def requestMemory = config.resourceRequestMemory ?: '0'
    def limitCPU = config.resourceLimitMemory ?: '0'
    def limitMemory = config.resourceLimitMemory ?: '0'

    def yaml

    def jobName = env.JOB_NAME.toLowerCase().replace('_', '-').replace('/', '-')

    def list = """
---
apiVersion: v1
kind: List
items:
"""

    def service = """
- apiVersion: v1
  kind: Service
  metadata:
    labels:
      project: ${jobName}
      expose: '${expose}'
      version: ${config.version}
      group: dos
    name: ${jobName}
  spec:
    ports:
    - port: 
      protocol: TCP
      targetPort: ${config.targetPort}
    selector:
      project: ${jobName}
      group: dos
"""

    def deployment = """
- apiVersion: extensions/v1beta1
  kind: Deployment
  metadata:
    labels:
      project: ${jobName}
      version: ${config.version}
      group: dos
    name: ${jobName}
  spec:
    replicas: 1
    selector:
      matchLabels:
        project: ${jobName}
        group: dos
    template:
      metadata:
        labels:
          project: ${jobName}
          version: ${config.version}
          group: dos
      spec:
        containers:
        - env:
          - name: KUBERNETES_NAMESPACE
            valueFrom:
              fieldRef:
                fieldPath: metadata.namespace
          image: ${env.FABRIC8_DOCKER_REGISTRY_SERVICE_HOST}:${env.FABRIC8_DOCKER_REGISTRY_SERVICE_PORT}/fabric8/${jobName}:${config.version}
          imagePullPolicy: IfNotPresent
          name: ${jobName}
          ports:
          - containerPort: ${config.containerPort}
            name: http
          resources:
            limits:
              cpu: ${requestCPU}
              memory: ${requestMemory}
            requests:
              cpu: ${limitCPU}
              memory: ${limitMemory}
"""

    yaml = list + service + deployment


    echo 'using resources:\n' + yaml
    return yaml

}