pipeline {
    agent any

    environment {
        COMPOSE_PATH = 'compose.dev.yaml'
    }

    stages {
        stage('Pull latest Docker images') {
            steps {
                dir('docker') {
                    sh "docker compose -f $COMPOSE_PATH pull"
                }
            }
        }

        stage('Deploy with Docker Compose') {
            steps {
                dir('docker') {
                    sh "docker compose -f $COMPOSE_PATH up -d"
                }
            }
        }
    }
}