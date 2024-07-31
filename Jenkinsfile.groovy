pipeline {
    agent any

    stages {
        stage('Validar') {
            steps {
                echo '>>>>> OpenShift <<<<<'
            }
        }

        stage('OpenShift Client') {
            steps {
                script {
                        echo '>>>>> OpenShift Client <<<<<'
                        openshift.withCluster('dev') {
                            echo "Usando : ${openshift.cluster()}'s / project: ${openshift.project()}"
                            openshift.withProject('redhat-ssa') {
                                echo "Trocar projeto ${openshift.project()}"
                            }
                    }
                }
            }
        }
    }
}