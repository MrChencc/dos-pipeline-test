def call(body) {
    // evaluate the body block, and collect configuration into the object
    def config = [:]
    body.resolveStrategy = Closure.DELEGATE_FIRST
    body.delegate = config
    body()

    def customConfig = config.custom
    echo 'switch node version...'
    try {
        sh """
#!/bin/bash        
source /.nvm/nvm.sh
nvm use ${customConfig.build.nodeversion}
        """
    } catch (error) {
        sh """
#!/bin/bash        
source /.nvm/nvm.sh
nvm install  ${customConfig.build.nodeversion}
nvm use ${customConfig.build.nodeversion}
        """
    }
}