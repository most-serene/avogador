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
            steps {
                setBuildPending()
                echo "Build started"

                withGradle {
                    sh '''
                        cd backend/apigateway
                        gradle wrapper

                        ./gradlew clean assemble
                    '''
                    archiveArtifacts artifacts: 'backend/apigateway/build/libs/*.jar', fingerprint: true
                }
                
                withGradle {
                    sh '''
                        cd backend/services/courseservice
                        gradle wrapper

                        ./gradlew clean assemble
                    '''
                    archiveArtifacts artifacts: 'backend/services/courseservice/build/libs/*.jar', fingerprint: true
                }

                withGradle {
                    sh '''
                        cd backend/services/userservice
                        gradle wrapper
                        
                        ./gradlew clean assemble
                    '''
                    archiveArtifacts artifacts: 'backend/services/userservice/build/libs/*.jar', fingerprint: true
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


                //! REMOVE-BEFORE-FLIGHT
                echo "Building Storybook"
                sh """
                    cd frontend
                    yarn build-storybook
                    tar -czvf storybook.tar.gz storybook-static
                """

                sh """
                    cp frontend/storybook.tar.gz /share/storybook/storybook.tar.gz
                """
                //! REMOVE-BEFORE-FLIGHT

                script {

                    if (env.BRANCH_NAME == 'master') {
                        echo "Publish artifacts"
                        sh """
                            cp backend/apigateway/build/libs/* /share/jars/apigateway.jar
                            cp backend/services/courseservice/build/libs/* /share/jars/courseservice.jar
                            cp backend/services/userservice/build/libs/* /share/jars/userservice.jar
							cp frontend/webapp.tar.gz /share/jars/webapp.tar.gz
                        """
                    }

                }
                echo "Build finished"
            }
        }
        stage('Test') {
            steps {
                echo "Tests started"

                withGradle {
                    sh '''
                        mkdir -p backend/apigateway/src/test/resources
                        cp $JENKINS_HOME/.envvars/avogador/apigatewayTest backend/apigateway/src/test/resources/application.properties
                        cd backend/apigateway

                        ./gradlew clean test
                    '''
                }
                
                withGradle {
                    sh '''
                        mkdir -p backend/services/courseservice/src/test/resources
                        cp $JENKINS_HOME/.envvars/avogador/courseServiceTest backend/services/courseservice/src/test/resources/application.properties
                        cd backend/services/courseservice

                        ./gradlew clean test
                    '''
                }

                withGradle {
                    sh '''
                        mkdir -p backend/services/userservice/src/test/resources
                        cp $JENKINS_HOME/.envvars/avogador/userServiceTest backend/services/userservice/src/test/resources/application.properties
                        cd backend/services/userservice
                        
                        ./gradlew clean test
                    '''
                }
                
                sh '''
                    cd frontend
                    yarn test run
                    yarn lint
                '''
                
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
                    DOCKER_HOST=${STAGING_DOCKER_ENGINE} BRANCH=${env.BRANCH_NAME} docker compose -f docker-compose-prod.yml --project-name avogador --env-file $JENKINS_HOME/.envvars/avogador/staging.env build
                    DOCKER_HOST=${STAGING_DOCKER_ENGINE} BRANCH=${env.BRANCH_NAME} docker compose -f docker-compose-prod.yml --project-name avogador --env-file $JENKINS_HOME/.envvars/avogador/staging.env up -d
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
            junit allowEmptyResults: true, testResults: 'frontend/reports/*.xml'    
            junit allowEmptyResults: true, testResults: '**/test-results/**/*.xml'
            discordSend description: "Jenkins Avogador Build", footer: "execution done", link: env.BUILD_URL, result: currentBuild.currentResult, title: JOB_NAME, webhookURL: "https://discord.com/api/webhooks/1136310574217695282/vp-s3bAzIBYPx9O3-78Ke_JcEJ1Rrn-uJsLxk9ZnrNQPO3u-DixI408Iesw2rLqV1sK1"
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
        }
    }
}