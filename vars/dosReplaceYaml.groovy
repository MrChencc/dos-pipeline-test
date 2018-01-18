#!/usr/bin/groovy

def call(body) {
    def config = [:]
    body.resolveStrategy = Closure.DELEGATE_FIRST
    body.delegate = config
    body()

    def replaceYaml = """
        mvn clean -e -U deploy
        temp=\$(pwd)
        cd \$temp/PROJECT-GENERATOR
        pwd
        sed  -i 's/\\\\[namespace]/dosproj-pipeline-test/g'  dos-proj.yaml
        sed  -i 's/\\\\[version]/latest_test/g'  dos-proj.yaml
        sed  -i 's/PORTNUM/29164/g'  dos-proj.yaml
        cd \$temp/appbuildresources
        sed  -i 's/\\[namespace]/dosproj-pipeline-test/g'  conf/Config.properties
        sed  -i 's/\\[cluster-name]/iuser/g'  conf/Config.properties
        sed  -i 's/\\[cluster-password]/0000/g'  conf/Config.properties
        sed  -i 's/\\[fabric-namespace]/fabric8/g'  conf/Config.properties
        echo 'compelet!'
        cd \$temp
        cat pom.xml
        cd \$temp/PROJECT-GENERATOR 
    """
    return replaceYaml
}