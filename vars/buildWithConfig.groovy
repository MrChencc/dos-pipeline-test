
def call(body) {
    // evaluate the body block, and collect configuration into the object
    def config = [:]
    body.resolveStrategy = Closure.DELEGATE_FIRST
    body.delegate = config
    body()

    def customConfig = config.custom
    container(name: 'node') {
        sh 'source /etc/profile'
        sh 'sh ' + customConfig.build.buildShell
    }
}