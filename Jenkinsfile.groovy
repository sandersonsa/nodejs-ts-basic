
pipeline {
    agent any
    
    environment {
        PATH = '$PATH:/var/jenkins_home/tools/com.openshift.jenkins.plugins.OpenShiftClientTools/oc412'
    }


    stages {
        stage('Validar') {
            steps {
                /* This method is implemented in vars/syncOpenShiftSecret.groovy */
                // syncOpenShiftSecret params.STAGE_SECRET_NAME
                echo '>>>>> OpenShift <<<<<'
            }
        }

        stage('OpenShift Client') {
            steps {
                script {
                    // withEnv(["PATH+OC=${tool 'oc412'}"]) {
                    // withEnv(["PATH=$PATH:/var/jenkins_home/tools/com.openshift.jenkins.plugins.OpenShiftClientTools/oc412"]) {
                /** The logical name references a Jenkins cluster configuration which implies **/
                /** API Server URL, default credentials, and a default project to use within the closure body. **/
                        echo '>>>>> OpenShift Client <<<<<'
                        openshift.withCluster('dev') {
                            echo "Usando : ${openshift.cluster()}'s / project: ${openshift.project()}"

                            // But we can easily change project contexts
                            openshift.withProject('redhat-ssa') {
                                echo 'pegar BC'
                                def buildConfig = openshift.selector('bc', 'frontend')
                                // # we started the build process
                                openshift.startBuild('frontend')
                            // def builds = buildConfig.related('builds')
                            }
                        // }
                    }
                }
            }
        }
    }
}