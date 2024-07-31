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
                    withEnv(["PATH+OC=${tool 'oc'}"]) {
                /** The logical name references a Jenkins cluster configuration which implies **/
                /** API Server URL, default credentials, and a default project to use within the closure body. **/
                    echo '>>>>> OpenShift Client <<<<<'
                    openshift.withCluster('dev') {
                        echo "Hello from ${openshift.cluster()}'s default project: ${openshift.project()}"

                        // But we can easily change project contexts
                        openshift.withProject('redhat-ssa') {
                            echo "pegar BC"
                            def buildConfig = openshift.selector("bc", "frontend")
                            // # we started the build process
                            openshift.startBuild("frontend")
                            // def builds = buildConfig.related('builds')

                            // Run `oc new-app https://github.com/openshift/ruby-hello-world` . It
                            // returns a Selector which will select the objects it created for you.
                            def created = openshift.newApp('https://github.com/openshift/ruby-hello-world')

                            // This Selector exposes the same operations you have already seen.
                            // (And many more that you haven't!).
                            echo "new-app created ${created.count()} objects named: ${created.names()}"
                            created.describe()

                            // We can create a Selector from the larger set which only selects
                            // the build config which new-app just created.
                            def bc = created.narrow('bc')

                            // Let's output the build logs to the Jenkins console. bc.logs()
                            // would run `oc logs bc/ruby-hello-world`, but that might only
                            // output a partial log if the build is in progress. Instead, we will
                            // pass '-f' to `oc logs` to follow the build until it terminates.
                            // Arguments to logs get passed directly on to the oc command line.
                            def result = bc.logs('-f')

                            // Many operations, like logs(), return a Result object (even a Selector
                            // is a subclass of Result). A Result object contains detailed information about
                            // what actions, if any, took place to accomplish an operation.
                            echo "The logs operation require ${result.actions.size()} oc interactions"

                            // You can even see exactly what oc command was executed.
                            echo "Logs executed: ${result.actions[0].cmd}"

                            // And even obtain the standard output and standard error of the command.
                            def logsString = result.actions[0].out
                            def logsErr = result.actions[0].err

                            // And if after some processing within your pipeline, if you decide
                            // you need to initiate a new build after the one initiated by
                            // new-app, simply call the `oc start-build` equivalent:
                            def buildSelector = bc.startBuild()
                            buildSelector.logs('-f')

                        // echo "Hello from a non-default project: ${openshift.project()}"
                        // echo "criar build"
                        // def bc = created.narrow('bc')
                        // echo "criar build"
                        // def buildSelector = bc.startBuild()
                        // echo "start build"
                        // buildSelector.logs('-f')
                        // echo "build logs"
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
}