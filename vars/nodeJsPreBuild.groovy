def call(body) {
    // evaluate the body block, and collect configuration into the object
    def config = [:]
    body.resolveStrategy = Closure.DELEGATE_FIRST
    body.delegate = config
    body()

    def customConfig = config.custom
    echo 'switch node version...'
    cmd1 = """       
. /.nvm/nvm.sh
nvm install  ${customConfig.build.nodeversion}
nvm use ${customConfig.build.nodeversion}
"""
    sh "bash -c ${cmd1}"
}