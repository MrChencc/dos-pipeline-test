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
    def isBase = config.isBase ?: true

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
      port: ${config.containerPort}
      targetPort: ${config.targetPort}
      nodePort: ${config.nodePort}
    selector:
      project: ${jobName}
      group: dos
    type: NodePort
"""

    def deploymentBase = """
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
          image: ${config.image}
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

    def deploymentApp = """
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
          - name:  APP_HOME_CONF_DIR
              value: ${config.appHomeConfDir}
          - name:  KEYCLOAK.AUTH.URL
              value: ${config.authURL}
          image: ${config.image}
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

    if (isBase) {
        yaml = list + service + deploymentBase
    } else {
        yaml = list + service +deploymentApp
    }


    echo 'using resources:\n' + yaml
    return yaml

}

/**
 * 前台
 def yaml = getDosK8SYaml{
 version = proj_version
 image = docker_image
 exposeApp = true
 targetPort = 443
 containerPort = 29151
 nodePort = 29161
 isBase = true
 appHomeConfDir = /opt/dos/conf
 authURL = http://192.168.1.87:29167/auth
 }

 后台
 def yaml = getDosK8SYaml{
 version = proj_version
 image = docker_image
 exposeApp = false
 targetPort = 29152
 containerPort = 29152
 nodePort = 29162
 isBase = false
 appHomeConfDir = /opt/dos/conf
 authURL = http://192.168.1.87:29167/auth
 }

 */