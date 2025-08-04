pipeline {
    agent any

    environment {
        COMPOSE_PATH = './deploy/docker-compose.yml'
    }

    stages {
        stage('Pull latest Docker images') {
            steps {
                sh "docker compose -f $COMPOSE_PATH pull"
            }
        }

        stage('Deploy with Docker Compose') {
            steps {
                sh "docker compose -f $COMPOSE_PATH up -d"
            }
        }
    }
}