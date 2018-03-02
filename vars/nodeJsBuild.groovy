def call(body) {
    // evaluate the body block, and collect configuration into the object
    def config = [:]
    body.resolveStrategy = Closure.DELEGATE_FIRST
    body.delegate = config
    body()

    def customConfig = config.custom
    echo 'switch node version...'
    def cmd = """
/switch ${customConfig.build.nodeversion}
${customConfig.build.buildShell}
"""

    sh "bash ${cmd}"
}