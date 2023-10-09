import groovy.json.JsonOutput

void setBuildPending() {
    step([
        $class: "GitHubSetCommitStatusBuilder",
        contextSource: [$class: "ManuallyEnteredCommitContextSource", context: "ci/jenkins/build-status"],
    ]);
}

void setBuildStatus(String message, String state) {
    step([
        $class: "GitHubCommitStatusSetter",
        reposSource: [$class: "ManuallyEnteredRepositorySource", url: "https://github.com/most-serene/avogador"],
        contextSource: [$class: "ManuallyEnteredCommitContextSource", context: "ci/jenkins/build-status"],
        errorHandlers: [[$class: "ChangingBuildStatusErrorHandler", result: "UNSTABLE"]],
        statusResultSource: [ $class: "ConditionalStatusResultSource", results: [[$class: "AnyBuildResult", message: message, state: state]] ]
    ]);
}

void setBuildBadge(String apiKey, String projectId, String status) {
    httpRequest contentType: "APPLICATION_JSON", httpMode: "POST", ignoreSslErrors: false,
        requestBody: JsonOutput.toJson([status: status, api_key: apiKey]), url: "https://status-api.mostserene.eu/projects/" + projectId
}

pipeline {
    agent any
    /* { 
        node {
            label 'core'
            }
    }*/
    tools {
        nodejs "Node"
        gradle "Gradle00"
    }

    /*triggers {
        pollSCM 'H/5 * * * *'
    }*/
    stages {
        stage('Build') {
            when {
               anyOf { branch 'PR-*'; branch 'master'; tag "release-*" }
            }
            steps {
                setBuildPending()
                echo "Build started"

                withGradle {
                    sh '''
                        cd backend/apigateway

                        ./gradlew assemble --no-daemon
                    '''
                    archiveArtifacts artifacts: 'backend/apigateway/build/libs/*.jar', fingerprint: true
                    script {
                        if (env.BRANCH_NAME == 'master') {
                            echo "Generate javadoc"
                            sh '''
                                cd backend/apigateway
                                ./gradlew javadoc --no-daemon
                            '''
                        }
                    }
                }
                
                withGradle {
                    sh '''
                        cd backend/services/courseservice

                        ./gradlew assemble --no-daemon
                    '''
                    archiveArtifacts artifacts: 'backend/services/courseservice/build/libs/*.jar', fingerprint: true
                    script {
                        if (env.BRANCH_NAME == 'master') {
                            echo "Generate javadoc"
                            sh '''
                                cd backend/services/courseservice
                                ./gradlew javadoc --no-daemon
                            '''
                        }
                    }
                }

                withGradle {
                    sh '''
                        cd backend/services/userservice
                        
                        ./gradlew assemble --no-daemon
                    '''
                    archiveArtifacts artifacts: 'backend/services/userservice/build/libs/*.jar', fingerprint: true
                    script {
                        if (env.BRANCH_NAME == 'master') {
                            echo "Generate javadoc"
                            sh '''
                                cd backend/services/userservice
                                ./gradlew javadoc --no-daemon
                            '''
                        }
                    }
                }

                withGradle {
                    sh '''
                        cd backend/services/exerciseservice
                        
                        ./gradlew assemble --no-daemon
                    '''
                    archiveArtifacts artifacts: 'backend/services/exerciseservice/build/libs/*.jar', fingerprint: true
                    script {
                        if (env.BRANCH_NAME == 'master') {
                            echo "Generate javadoc"
                            sh '''
                                cd backend/services/exerciseservice
                                ./gradlew javadoc --no-daemon
                            '''
                        }
                    }
                }

                withGradle {
                    sh '''
                        cd backend/services/filesystemservice
                        
                        ./gradlew assemble --no-daemon
                    '''
                    archiveArtifacts artifacts: 'backend/services/filesystemservice/build/libs/*.jar', fingerprint: true
                    script {
                        if (env.BRANCH_NAME == 'master') {
                            echo "Generate javadoc"
                            sh '''
                                cd backend/services/filesystemservice
                                ./gradlew javadoc --no-daemon
                            '''
                        }
                    }
                }

                withGradle {
                    sh '''
                        cd backend/services/executorservice
                        
                        ./gradlew assemble --no-daemon
                    '''
                    archiveArtifacts artifacts: 'backend/services/executorservice/build/libs/*.jar', fingerprint: true
                    script {
                        if (env.BRANCH_NAME == 'master') {
                            echo "Generate javadoc"
                            sh '''
                                cd backend/services/executorservice
                                ./gradlew javadoc --no-daemon
                            '''
                        }
                    }
                }
                
                // cp -r $JENKINS_HOME/.envvars/avogador/node_modules .
                sh '''
                    cd frontend
                    cp $JENKINS_HOME/.envvars/avogador/web.staging.env ./.env.staging
                    yarn
                    yarn build:staging
                    tar -czvf webapp.tar.gz dist
                '''
                archiveArtifacts artifacts: 'frontend/webapp.tar.gz', fingerprint: true

                script {
                    if (env.BRANCH_NAME == 'master') {
                        /*
                        echo "Building Storybook"
                        sh """
                            cd frontend
                            yarn build-storybook
                            tar -czvf storybook.tar.gz storybook-static
                        """
                        */

                        echo "Publish artifacts"
                        sh """
                            cp backend/apigateway/build/libs/* /share/avogador/artifacts/apigateway.jar
                            cp backend/services/courseservice/build/libs/* /share/avogador/artifacts/courseservice.jar
                            cp backend/services/userservice/build/libs/* /share/avogador/artifacts/userservice.jar
                            cp backend/services/exerciseservice/build/libs/* /share/avogador/artifacts/exerciseservice.jar
                            cp backend/services/filesystemservice/build/libs/* /share/avogador/artifacts/filesystemservice.jar
                            cp backend/services/executorservice/build/libs/* /share/avogador/artifacts/executorservice.jar
							
                            cp frontend/webapp.tar.gz /share/avogador/artifacts/webapp.tar.gz
                        """
                            // cp frontend/storybook.tar.gz /share/avogador/storybook/storybook.tar.gz
                        
                        echo "Publish javadoc"
                        sh '''
                            cp -r backend/apigateway/build/docs/javadoc/* /share/avogador/javadoc/apigateway/
                            cp -r backend/services/courseservice/build/docs/javadoc/* /share/avogador/javadoc/courseService/
                            cp -r backend/services/userservice/build/docs/javadoc/* /share/avogador/javadoc/userService/
                            cp -r backend/services/exerciseservice/build/docs/javadoc/* /share/avogador/javadoc/exerciseService/
                            cp -r backend/services/filesystemservice/build/docs/javadoc/* /share/avogador/javadoc/filesystemservice/
                            cp -r backend/services/executorservice/build/docs/javadoc/* /share/avogador/javadoc/executorservice/
                        '''
                    }

                }
                echo "Build finished"
            }
        }
        stage('Test') {
            when {
               anyOf { branch 'PR-*'; branch 'master'; tag "release-*" }
            }
            steps {
                echo "Tests started"

                withGradle {
                    sh '''
                        mkdir -p backend/apigateway/src/test/resources
                        cp $JENKINS_HOME/.envvars/avogador/apigatewayTest backend/apigateway/src/test/resources/application.properties
                        cd backend/apigateway

                        ./gradlew test --no-daemon
                    '''
                }
                
                withGradle {
                    sh '''
                        mkdir -p backend/services/courseservice/src/test/resources
                        cp $JENKINS_HOME/.envvars/avogador/courseServiceTest backend/services/courseservice/src/test/resources/application.properties
                        cd backend/services/courseservice

                        ./gradlew test --no-daemon
                    '''
                }

                withGradle {
                    sh '''
                        mkdir -p backend/services/userservice/src/test/resources
                        cp $JENKINS_HOME/.envvars/avogador/userServiceTest backend/services/userservice/src/test/resources/application.properties
                        cd backend/services/userservice
                        
                        ./gradlew test --no-daemon
                    '''
                }

                withGradle {
                    sh '''
                        mkdir -p backend/services/exerciseservice/src/test/resources
                        cp $JENKINS_HOME/.envvars/avogador/exerciseServiceTest backend/services/exerciseservice/src/test/resources/application.properties
                        cd backend/services/exerciseservice
                        
                        ./gradlew test --no-daemon
                    '''
                }

                withGradle {
                    sh '''
                        mkdir -p backend/services/filesystemservice/src/test/resources
                        cp $JENKINS_HOME/.envvars/avogador/filesystemTest backend/services/filesystemservice/src/test/resources/application.properties
                        cd backend/services/filesystemservice
                        
                        ./gradlew test --no-daemon
                    '''
                }

                withGradle {
                    sh '''
                        mkdir -p backend/services/executorservice/src/test/resources
                        cp $JENKINS_HOME/.envvars/avogador/executorTest backend/services/executorservice/src/test/resources/application.properties
                        cd backend/services/executorservice
                        
                        ./gradlew test --no-daemon
                    '''
                }
                
                /*
                sh '''
                    cd frontend
                    yarn test run
                    yarn lint
                '''
                */
                
                echo "Tests finished"
            }
        }
        stage('Deliver-staging') {
            when {
                branch 'master'
            }
            steps {
                echo 'Staging Deliver started'

                /*
                sh '''
                    cd frontend
                    cp -r node_modules $JENKINS_HOME/.envvars/avogador/node_modules
                '''
                */


                //ssh ${STAGING_HOST} 'bin/MaintenanceAvogador' || true
                //ssh ${STAGING_HOST} 'bin/NotMaintenanceAvogador'
                withEnv(readFile("$JENKINS_HOME/.envvars/avogador/jenkinsEnv.txt").split('\n') as List) {
                    sh """
                    DOCKER_HOST=${STAGING_DOCKER_ENGINE} BRANCH=${env.BRANCH_NAME} docker compose -f docker-compose-staging.yml --project-name avogador --env-file $JENKINS_HOME/.envvars/avogador/staging.env build
                    DOCKER_HOST=${STAGING_DOCKER_ENGINE} BRANCH=${env.BRANCH_NAME} docker compose -f docker-compose-staging.yml --project-name avogador --env-file $JENKINS_HOME/.envvars/avogador/staging.env up -d --force-recreate
                    DOCKER_HOST=${STAGING_DOCKER_ENGINE} docker container ls -a
                    """
                }
                echo 'Staging Deliver finished'
            }
        }
        stage('Deliver-production') {
            when {
                tag "release-*"
            }
            steps {
                echo 'Production Deliver started'
                //ssh ${PRODUCTION_HOST} 'bin/MaintenanceAvogador' || true
                //ssh ${PRODUCTION_HOST} 'bin/NotMaintenanceAvogador'
                echo '$TAG_NAME'

                sh """
                    echo ${env.TAG_NAME}
                """

                /*
                withEnv(readFile("$JENKINS_HOME/.envvars/avogador/jenkinsEnv.txt").split('\n') as List) {
                    sh """
                    DOCKER_HOST=${PRODUCTION_DOCKER_ENGINE} BRANCH=${env.BRANCH_NAME} docker compose -f docker-compose-prod.yml --project-name avogador --env-file $JENKINS_HOME/.envvars/avogador/production.env build
                    DOCKER_HOST=${PRODUCTION_DOCKER_ENGINE} BRANCH=${env.BRANCH_NAME} docker compose -f docker-compose-prod.yml --project-name avogador --env-file $JENKINS_HOME/.envvars/avogador/production.env up -d --force-recreate
                    DOCKER_HOST=${PRODUCTION_DOCKER_ENGINE} docker container ls -a
                    """
                }
                */
                echo 'Production Deliver finished'
            }
        }
    }
    post {
        
        always {

            script {
                if (env.BRANCH_NAME == 'master' || env.BRANCH_NAME.startsWith('PR') || 
                    sh(returnStdout: true, script: "git tag --contains").trim()) {
                    
                    junit allowEmptyResults: true, testResults: '**/test-results/**/*.xml'
                }
            }

            // junit allowEmptyResults: true, testResults: 'frontend/reports/*.xml'    
        }
        success {
            setBuildStatus("Build succeeded", "SUCCESS");
            script {
                if (env.BRANCH_NAME == 'master') {
                    withEnv(readFile("$JENKINS_HOME/.envvars/buildStatusApi/jenkinsEnv.txt").split('\n') as List) {
                        setBuildBadge(env.API_KEY, "5", "success");
                    }
                }
            }
            discordSend description: "Jenkins Avogador Build", footer: "success", link: env.BUILD_URL, result: currentBuild.currentResult, title: JOB_NAME, webhookURL: "https://discord.com/api/webhooks/1136310574217695282/vp-s3bAzIBYPx9O3-78Ke_JcEJ1Rrn-uJsLxk9ZnrNQPO3u-DixI408Iesw2rLqV1sK1"
        }
        failure {
            setBuildStatus("Build failed", "FAILURE");
            script {
                if (env.BRANCH_NAME == 'master') {
                    withEnv(readFile("$JENKINS_HOME/.envvars/buildStatusApi/jenkinsEnv.txt").split('\n') as List) {
                        setBuildBadge(env.API_KEY, "5", "failed");
                    }
                }
            }
            discordSend description: "Jenkins Avogador Build", footer: "failure", link: env.BUILD_URL, result: currentBuild.currentResult, title: JOB_NAME, webhookURL: "https://discord.com/api/webhooks/1136310574217695282/vp-s3bAzIBYPx9O3-78Ke_JcEJ1Rrn-uJsLxk9ZnrNQPO3u-DixI408Iesw2rLqV1sK1"
        }
    }
}