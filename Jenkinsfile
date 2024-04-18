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
    }
    tools {
        dockerTool "docker-00"
    }
    */

    /*triggers {
        pollSCM 'H/5 * * * *'
    }*/
    stages {
        stage('Build & Testing') {
            when {
               anyOf { branch 'PR-*'; branch 'master'; tag "release-*" }
            }
            steps {
                setBuildPending()
                echo "Importing application.properties"

                sh """
                    cat backend/apigateway/build.gradle.kts
                """

                sh """
                    cp /envvars/avogador/web.staging.env frontend/.env.staging
                    
                    mkdir -p backend/apigateway/src/test/resources
                    cp /envvars/avogador/apigatewayTest backend/apigateway/src/test/resources/application.properties
                        
                    mkdir -p backend/services/courseservice/src/test/resources
                    cp /envvars/avogador/courseServiceTest backend/services/courseservice/src/test/resources/application.properties
                        
                    mkdir -p backend/services/userservice/src/test/resources
                    cp /envvars/avogador/userServiceTest backend/services/userservice/src/test/resources/application.properties
                    
                    mkdir -p backend/services/exerciseservice/src/test/resources
                    cp /envvars/avogador/exerciseServiceTest backend/services/exerciseservice/src/test/resources/application.properties
                    
                    mkdir -p backend/services/storageservice/src/test/resources
                    cp /envvars/avogador/storageTest backend/services/storageservice/src/test/resources/application.properties

                    mkdir -p backend/services/executorservice/src/test/resources
                    cp /envvars/avogador/executorTest backend/services/executorservice/src/test/resources/application.properties         
                """

                echo "Building docker images"

                script {
                    if (env.BRANCH_NAME == 'master' || env.BRANCH_NAME == 'pipeline-fixes') {
                        sh """
                            BRANCH=${env.BRANCH_NAME} docker compose -f docker-compose-staging.yml --project-name avogador --env-file /envvars/avogador/staging.env build webapp
                            BRANCH=${env.BRANCH_NAME} docker compose -f docker-compose-staging.yml --project-name avogador --env-file /envvars/avogador/staging.env build apigateway
                            BRANCH=${env.BRANCH_NAME} docker compose -f docker-compose-staging.yml --project-name avogador --env-file /envvars/avogador/staging.env build users
                            BRANCH=${env.BRANCH_NAME} docker compose -f docker-compose-staging.yml --project-name avogador --env-file /envvars/avogador/staging.env build courses
                            BRANCH=${env.BRANCH_NAME} docker compose -f docker-compose-staging.yml --project-name avogador --env-file /envvars/avogador/staging.env build exercises
                            BRANCH=${env.BRANCH_NAME} docker compose -f docker-compose-staging.yml --project-name avogador --env-file /envvars/avogador/staging.env build storage
                            BRANCH=${env.BRANCH_NAME} docker compose -f docker-compose-staging.yml --project-name avogador --env-file /envvars/avogador/staging.env build executor
                            
                            BRANCH=${env.BRANCH_NAME} docker compose -f docker-compose-staging.yml --project-name avogador --env-file /envvars/avogador/staging.env push
                        """
                    } else {
                        sh """
                            docker compose -f docker-compose-staging.yml --project-name avogador --env-file /envvars/avogador/staging.env build webapp
                            docker compose -f docker-compose-staging.yml --project-name avogador --env-file /envvars/avogador/staging.env build apigateway
                            docker compose -f docker-compose-staging.yml --project-name avogador --env-file /envvars/avogador/staging.env build users
                            docker compose -f docker-compose-staging.yml --project-name avogador --env-file /envvars/avogador/staging.env build courses
                            docker compose -f docker-compose-staging.yml --project-name avogador --env-file /envvars/avogador/staging.env build exercises
                            docker compose -f docker-compose-staging.yml --project-name avogador --env-file /envvars/avogador/staging.env build storage
                            docker compose -f docker-compose-staging.yml --project-name avogador --env-file /envvars/avogador/staging.env build executor
                        """
                    }
                }

            }
        }
        stage('Exporting test reports') {
            when {
               anyOf { branch 'PR-*'; branch 'master'; tag "release-*" }
            }
            steps {
                echo "Not implemented yet, skipping"
                /*
                sh '''
                    cd backend/apigateway

                    docker build . --target reporter -o 
                '''
                */
            }
        }
        stage('Deliver-staging') {
            when {
                anyOf { branch 'pipeline-fixes'; branch 'master' } 
            }
            steps {
                echo 'Staging Deliver started'

                //ssh ${STAGING_HOST} 'bin/MaintenanceAvogador' || true
                //ssh ${STAGING_HOST} 'bin/NotMaintenanceAvogador'
                withEnv(readFile("/envvars/avogador/jenkinsEnv.txt").split('\n') as List) {
                    sh """
                    DOCKER_HOST=${STAGING_DOCKER_ENGINE} BRANCH=${env.BRANCH_NAME} docker compose -f docker-compose-staging.yml --project-name avogador --env-file /envvars/avogador/staging.env pull
                    DOCKER_HOST=${STAGING_DOCKER_ENGINE} BRANCH=${env.BRANCH_NAME} docker compose -f docker-compose-staging.yml --project-name avogador --env-file /envvars/avogador/staging.env up -d --build
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
                echo 'Production Deliver started - $TAG_NAME'

                /*
                withEnv(readFile("/envvars/avogador/jenkinsEnv.txt").split('\n') as List) {
                    sh """
                    DOCKER_HOST=${PRODUCTION_DOCKER_ENGINE} BRANCH=${env.BRANCH_NAME} docker compose -f docker-compose-prod.yml --project-name avogador --env-file /envvars/avogador/production.env build
                    ssh ${PRODUCTION_HOST} 'bin/MaintenanceJupyter' || true
                    DOCKER_HOST=${PRODUCTION_DOCKER_ENGINE} BRANCH=${env.BRANCH_NAME} docker compose -f docker-compose-prod.yml --project-name avogador --env-file /envvars/avogador/production.env up -d
                    ssh ${PRODUCTION_HOST} 'bin/NotMaintenanceJupyter'
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
                    withEnv(readFile("/envvars/buildStatusApi/jenkinsEnv.txt").split('\n') as List) {
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
                    withEnv(readFile("/envvars/buildStatusApi/jenkinsEnv.txt").split('\n') as List) {
                        setBuildBadge(env.API_KEY, "5", "failed");
                    }
                }
            }
            discordSend description: "Jenkins Avogador Build", footer: "failure", link: env.BUILD_URL, result: currentBuild.currentResult, title: JOB_NAME, webhookURL: "https://discord.com/api/webhooks/1136310574217695282/vp-s3bAzIBYPx9O3-78Ke_JcEJ1Rrn-uJsLxk9ZnrNQPO3u-DixI408Iesw2rLqV1sK1"
        }
    }
}