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
                }
                
                withGradle {
                    sh '''
                        cd backend/services/courseservice
                        gradle wrapper

                        ./gradlew clean assemble
                    '''
                }

                withGradle {
                    sh '''
                        cd backend/services/userservice
                        gradle wrapper
                        
                        ./gradlew clean assemble
                    '''
                }
                
                sh '''
                    cd frontend
                    yarn
                    yarn build
                '''
                
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
                    yarn
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
                    DOCKER_HOST=${PRODUCTION_DOCKER_ENGINE} BRANCH=${env.BRANCH_NAME} docker compose -f docker-compose-prod.yml --project-name avogador --env-file $JENKINS_HOME/.envvars/avogador/production.env up -d
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
            // archiveArtifacts artifacts: 'services/codeExecutor/build/libs/**/*.jar', fingerprint: true
            // archiveArtifacts artifacts: 'services/projectService/build/libs/**/*.jar', fingerprint: true
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