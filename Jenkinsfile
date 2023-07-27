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
    tools {nodejs "Node"}

    /*triggers {
        pollSCM 'H/5 * * * *'
    }*/
    stages {
        stage('Build') {
            steps {
                setBuildPending()
                echo "Build started"
                sh """
                BRANCH=${env.BRANCH_NAME} docker compose -f docker-compose-prod.yml --env-file $JENKINS_HOME/.envvars/avogador/development.env build
                """
                echo "Build finished"
            }
        }
        stage('Test') {
            steps {
                echo "Tests started"
                
                withGradle {
                    sh '''
                        cd backend/services/usercourse
                        gradle clean test
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
            junit 'backend/services/usercourse/build/test-results/**/*.xml'
            junit 'frontend/reports/*.xml'
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