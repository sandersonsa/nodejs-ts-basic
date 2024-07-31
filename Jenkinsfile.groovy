
pipeline {
    agent any
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
                /** The logical name references a Jenkins cluster configuration which implies **/
                /** API Server URL, default credentials, and a default project to use within the closure body. **/
                    echo '>>>>> OpenShift Client <<<<<'
                    openshift.withCluster('dev') {
                        echo "Hello from ${openshift.cluster()}'s default project: ${openshift.project()}"

                        // But we can easily change project contexts
                        openshift.withProject('redhat-ssa') {
                            echo "Hello from a non-default project: ${openshift.project()}"
                        }

                        // And even scope operations to other clusters within the same script
                        // openshift.withCluster( 'kafka-' ) {
                        //     echo "Hello from ${openshift.cluster()}'s default project: ${openshift.project()}"
                        // }
                    }
                }
            }
        }

        /** Stage - OpenShift Template
         *
         * This is generally duplicated from the Dev - OpenShift Template
         * See inline documentation above.
         */
        // stage('Stage - OpenShift Template') {
        //     environment {
        //         STAGE = credentials("${params.STAGE_SECRET_NAME}")
        //     }
        //     steps {
        //         script {
        //             openshift.withCluster(params.STAGE_URI, env.STAGE_PSW) {
        //                 openshift.withProject(params.STAGE_PROJECT) {
        //                     def openShiftApplyArgs = ""
        //                     if (findFileChanges(params.APP_TEMPLATE_PATH)
        //                             || !openshift.selector("template/${params.APP_DC_NAME}").exists()) {
        //                         openshift.apply(readFile(params.APP_TEMPLATE_PATH))
        //                     } else {
        //                         openShiftApplyArgs = "--dry-run"
        //                         openshift.tag("--source=docker",
        //                                 "${imageName}",
        //                                 "${openshift.project()}/${params.IMAGE_STREAM_NAME}:${params.IMAGE_STREAM_LATEST_TAG}")
        //                     }

        //                     def model = openshift.process(params.IMAGE_STREAM_NAME,
        //                             "-l app=${params.APP_DC_NAME}",
        //                             "-p",
        //                             "TAG=${env.TAG}",
        //                             "IMAGESTREAM_TAG=${params.IMAGE_STREAM_LATEST_TAG}",
        //                             "REGISTRY_PROJECT=${params.REGISTRY_PROJECT}",
        //                             "REGISTRY=${params.REGISTRY_URI}")

        //                     if (openshift.selector("secret/${params.APP_DC_NAME}").exists()) {
        //                         def count = 0
        //                         for (item in model) {
        //                             if (item.kind == 'Secret') {
        //                                 model.remove(count)
        //                             }
        //                             count++
        //                         }
        //                     }
        //                     createdObjects = openshift.apply(model, openShiftApplyArgs)

    //                     /* The stage environment does not need OpenShift BuildConfig objects */
    //                     if (createdObjects.narrow('bc').exists()) {
    //                         createdObjects.narrow('bc').delete()
    //                     }
    //                 }
    //             }
    //         }
    //     }
    // }
    }
}
