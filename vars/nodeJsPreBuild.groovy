def call(body) {
    // evaluate the body block, and collect configuration into the object
    def config = [:]
    body.resolveStrategy = Closure.DELEGATE_FIRST
    body.delegate = config
    body()

    def customConfig = config.custom
    echo 'switch node version...'
    sh 'ls -lta'
    try {
        sh 'nvm use' + customConfig.build.nodeversion
    } catch (error) {
        sh 'nvm install' + customConfig.build.nodeversion
        sh 'nvm use' + customConfig.build.nodeversion
    }
}