def call(body) {
    // evaluate the body block, and collect configuration into the object
    def config = [:]
    body.resolveStrategy = Closure.DELEGATE_FIRST
    body.delegate = config
    body()

    def customConfig = config.custom
    echo 'switch node version...'
    def cmd = """
#!/bin/bash    
source /switch ${customConfig.build.nodeversion} && \
source ${customConfig.build.buildShell}
"""
    print "${cmd}"
    sh "echo ${cmd} > /run.sh"
    sh "chmod -R 777 /run.sh"
    sh "bash -c /run.sh"
}