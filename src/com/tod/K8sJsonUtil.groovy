package com.tod

import io.fabric8.Fabric8Commands
import io.fabric8.Utils

class K8sJsonUtil {
    /**
     * 获取对应的Json文件
     * @param config 配置文件
     * @param envs 环境变量
     */
    K8sJsonUtil(def config, def envs) {
        def utils = new Utils()

        def expose = config.exposeApp ?: 'true'
        def m = readMavenPom file: "${config.pom}"
        def groupId
        if (m.groupId == null) {
            groupId = m.parent.groupId.split('\\.')
        } else {
            groupId = m.groupId.split('\\.')
        }
        def artifactId = m.artifactId.toLowerCase()
        def user = groupId[groupId.size() - 1].trim()

        def isSha = ''
        def jobName = env.JOB_NAME.toLowerCase().replace('_', '-').replace('/', '-')

        def sha
        def image = "${env.FABRIC8_DOCKER_REGISTRY_SERVICE_HOST}:${env.FABRIC8_DOCKER_REGISTRY_SERVICE_PORT}/${user}/${artifactId}:${config.version}"

        service = """
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
   selector:
      project: ${jobName}
      provider: fabric8
      group: dos
    ports:
    - port: 80
      protocol: TCP
      targetPort: ${config.port}
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
        provider: fabric8
        project: ${jobName}
        group: dos
    template:
      metadata:
        labels:
          project: ${jobName}
          version: ${config.version}
          group: dos
      spec:
        terminationGracePeriodSeconds: 2
        containers:

          image: ${image}
          imagePullPolicy: IfNotPresent
          name: ${jobName}
        - env:
          - name: KUBERNETES_NAMESPACE
            valueFrom:
              fieldRef:
                fieldPath: metadata.namespace
          - name:  APP_HOME_CONF_DIR
            value: /opt/dos/conf
          ports:
          - containerPort: ${config.port}
            name: http
"""
    }

    String withEnv(def envs) {
        if (JenkinsUtil.isMap(envs)) {

        }
    }

    String withPort(def ports) {

    }

    String withSvcPort(def ports) {

    }

    /**
     * k8sResList
     */
    private final static def k8sResList = """
---
apiVersion: v1
kind: List
items:
"""

}
