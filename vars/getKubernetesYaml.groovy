#!/usr/bin/groovy
import io.fabric8.Utils
import io.fabric8.Fabric8Commands

def call(body) {
    // evaluate the body block, and collect configuration into the object
    def config = [:]
    body.resolveStrategy = Closure.DELEGATE_FIRST
    body.delegate = config
    body()

    def flow = new Fabric8Commands()
    def utils = new Utils()

    def expose = config.exposeApp ?: 'true'
    def requestCPU = config.resourceRequestCPU ?: '0'
    def requestMemory = config.resourceRequestMemory ?: '0'
    def limitCPU = config.resourceLimitMemory ?: '0'
    def limitMemory = config.resourceLimitMemory ?: '0'
    def m = readMavenPom file: "${config.pom}"
    def groupId
    if (m.groupId == null){
        groupId = m.parent.groupId.split( '\\.' )
    } else {
        groupId = m.groupId.split( '\\.' )
    }
    def artifactId = m.artifactId.toLowerCase()
    def user = groupId[groupId.size()-1].trim()
    def yaml

    def isSha = ''
    if (flow.isOpenShift()){
        isSha = utils.getImageStreamSha(env.JOB_NAME)
    }
    def jobName = env.JOB_NAME.toLowerCase().replace('_', '-').replace('/', '-')

    def sha
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
    annotations:
      fabric8.io/iconUrl: ${config.icon}
    labels:
      provider: fabric8
      project: ${jobName}
      expose: '${expose}'
      version: ${config.version}
      group: dos
    name: ${jobName}
  spec:
    ports:
    - port: 80
      protocol: TCP
      targetPort: ${config.port}
    selector:
      project: ${jobName}
      provider: fabric8
      group: dos
"""

    def deployment = """
- apiVersion: extensions/v1beta1
  kind: Deployment
  metadata:
    annotations:
      fabric8.io/iconUrl: ${config.icon}
    labels:
      provider: fabric8
      project: ${jobName}
      version: ${config.version}
      group: dos
    name: ${jobName}
  spec:
    replicas: 1
    selector:
      matchLabels:
        provider: fabric8
        project: ${jobName}
        group: dos
    template:
      metadata:
        labels:
          provider: fabric8
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
          - name:  APP_HOME_CONF_DIR
            value: /opt/dos/conf
          image: ${env.FABRIC8_DOCKER_REGISTRY_SERVICE_HOST}:${env.FABRIC8_DOCKER_REGISTRY_SERVICE_PORT}/${user}/${artifactId}:${config.version}
          imagePullPolicy: IfNotPresent
          name: ${jobName}
          ports:
          - containerPort: ${config.port}
            name: http
          resources:
            limits:
              cpu: ${requestCPU}
              memory: ${requestMemory}
            requests:
              cpu: ${limitCPU}
              memory: ${limitMemory}
        terminationGracePeriodSeconds: 2
"""

    def deploymentConfig = """
- apiVersion: v1
  kind: DeploymentConfig
  metadata:
    annotations:
      fabric8.io/iconUrl: ${config.icon}
    labels:
      provider: fabric8
      project: ${jobName}
      version: ${config.version}
      group: dos
    name: ${jobName}
  spec:
    replicas: 1
    selector:
      provider: fabric8
      project: ${jobName}
      group: dos
    template:
      metadata:
        labels:
          provider: fabric8
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
          - name:  APP_HOME_CONF_DIR
            value: /opt/dos/conf
          image: ${env.FABRIC8_DOCKER_REGISTRY_SERVICE_HOST}:${env.FABRIC8_DOCKER_REGISTRY_SERVICE_PORT}/${user}/${artifactId}:${config.version}
          imagePullPolicy: IfNotPresent
          name: ${jobName}
          ports:
          - containerPort: ${config.port}
            name: http
          resources:
            limits:
              cpu: ${requestCPU}
              memory: ${requestMemory}
            requests:
              cpu: ${limitCPU}
              memory: ${limitMemory}
        terminationGracePeriodSeconds: 2
    triggers:
    - type: ConfigChange
    - imageChangeParams:
        automatic: true
        containerNames:
        - ${jobName}
        from:
          kind: ImageStreamTag
          name: ${jobName}:${config.version}
      type: ImageChange
"""

    def is = """
- apiVersion: v1
  kind: ImageStream
  metadata:
    name: ${jobName}
  spec:
    tags:
    - from:
        kind: ImageStreamImage
        name: ${jobName}@${isSha}
        namespace: ${utils.getNamespace()}
      name: ${config.version}
"""

    if (flow.isOpenShift()){
        yaml = list + service + is + deploymentConfig
    } else {
        yaml = list + service + deployment
    }

    echo 'using resources:\n' + yaml
    return yaml

}