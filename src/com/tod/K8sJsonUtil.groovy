package com.tod

class K8sJsonUtil {
    /**
     * 获取对应的Json文件
     * @param config 配置文件
     * @param envs 环境变量
     */
    static getK8sJson(def config, def env) {
        if (!config.deployment) {
            return k8sResList;
        }

        def expose = config.exposeApp ?: 'true'
        def jobName = env.JOB_NAME.toLowerCase().replace('_', '-').replace('/', '-')
        def image = config.image

        def yamlEnv = withEnv(config.deployment.envs)
        def yamlPort = withPort(config.deployment.ports)
        def yamlSvcPort = withSvcPort(config.deployment.ports)

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
    selector:
      project: ${jobName}
      provider: fabric8
      group: dos
    ports:${yamlSvcPort}"""

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
                  fieldPath: metadata.namespace${yamlEnv}
          ports:${yamlPort}
"""
        return k8sResList + service + deployment;
    }

    private static String withEnv(def envs) {
        String finalVal = '';
        if (JenkinsUtil.isMap(envs)) {
            Map envsMap = (Map) envs;
            for (String mk : envsMap.keySet()) {
                finalVal += makeEnv(mk, envsMap.get(mk));
            }
        }
        return finalVal;
    }

    private static String makeEnv(def key, def val) {
        return """
            - name:  ${key}
              value: ${val}"""
    }

    private static String withPort(def ports) {
        String finalVal = '';
        if (JenkinsUtil.isMap(ports) && ((Map) ports).keySet().size() > 0) {
            Map portMap = (Map) ports
            for (String mk : portMap.keySet()) {
                finalVal += makePort(mk)
            }
        } else {
            finalVal += makePort(80)
        }
        return finalVal;
    }

    private static String makePort(def targetPort) {
        String newName = 'http';
        String targetPortStr = new String(targetPort + "");
        if (!targetPort.equals('80')) {
            newName += targetPortStr
        }
        return """
          - containerPort: ${targetPort}
            name: ${newName}"""
    }

    private static String withSvcPort(def ports) {
        String finalVal = '';
        if (JenkinsUtil.isMap(ports) && ((Map) ports).keySet().size() > 0) {
            Map portMap = (Map) ports
            for (String mk : portMap.keySet()) {
                finalVal += makeSvcPort(mk, portMap.get(mk))
            }
        } else {
            finalVal += makeSvcPort(80, 80)
        }
        return finalVal;
    }

    private static String makeSvcPort(def targetPort, def port) {
        return """
    - port: ${port}
      protocol: TCP
      targetPort: ${targetPort}"""
    }

    /**
     * k8sResList
     */
    private static def k8sResList = """
apiVersion: v1
kind: List
items:"""

}
