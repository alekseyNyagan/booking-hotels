pipeline {
    agent any

    environment {
        DOCKERHUB_CREDENTIALS_ID = 'dockerhub-creds'  // Jenkins credentials ID
        REGISTRY = 'alekseynyagan607'
        VM_HOST = '192.168.1.45'
        VM_USER = 'aleksey'
        VM_REPO_PATH = '/home/aleksey/booking-hotels'
        SSH_CRED_ID = '0dca05a2-725b-48f4-bb0b-374158b89b15'
    }

    stages {
        stage('Detect Changed Services') {
            steps {
                script {
                    echo "📡 Получаем список изменений относительно origin/master"

                    // обновляем ветку
                    sh 'git fetch origin master'

                    // список изменённых файлов
                    def diff = sh(
                        script: "git diff --name-only origin/master..HEAD",
                        returnStdout: true
                    ).trim()

                    if (diff) {
                        echo "Изменённые файлы:\n${diff}"

                        // выделим изменённые сервисы (папки на верхнем уровне)
                        def services = []
                        diff.split("\n").each { file ->
                            def topDir = file.tokenize('/')[0]
                            if (file.contains("/") && !services.contains(topDir)) {
                                services << topDir
                            }
                        }

                        if (services) {
                            echo "🚀 Изменённые сервисы: ${services}"
                            env.CHANGED_SERVICES = services.join(" ")
                        } else {
                            echo "✅ Нет изменений в сервисах. Сборка остановлена."
                            currentBuild.result = 'SUCCESS'
                            error("No service changes")
                        }
                    } else {
                        echo "✅ Нет изменений. Сборка остановлена."
                        currentBuild.result = 'SUCCESS'
                        error("No changes")
                    }
                }
            }
        }

        stage('Build & Push Docker Images') {
            when { expression { return env.skipRemainingStages != "true" } }
            steps {
                script {
                    withCredentials([usernamePassword(
                        credentialsId: DOCKERHUB_CREDENTIALS_ID,
                        usernameVariable: 'DOCKER_USER',
                        passwordVariable: 'DOCKER_PASS'
                    )]) {
                        sh 'echo $DOCKER_PASS | docker login -u $DOCKER_USER --password-stdin'
                        env.CHANGED_SERVICES.split(',').each { svc ->
                            def image = "${REGISTRY}/${svc}:latest"
                            echo "🐳 Сборка и пуш образа: ${image}"
                            sh """
                                docker build -t ${image} -f ${svc}/Dockerfile .
                                docker push ${image}
                            """
                        }
                    }
                }
            }
        }

        stage('Deploy to Test VM') {
            when { expression { return env.skipRemainingStages != "true" } }
            steps {
                script {
                    withCredentials([usernamePassword(
                        credentialsId: DOCKERHUB_CREDENTIALS_ID,
                        usernameVariable: 'DOCKER_USER',
                        passwordVariable: 'DOCKER_PASS'
                    )]) {
                        sshagent([SSH_CRED_ID]) {
                            env.CHANGED_SERVICES.split(',').each { svc ->
                                echo "🚀 Деплой сервиса: ${svc} на VM"
                                sh """
                                    ssh -o StrictHostKeyChecking=no ${VM_USER}@${VM_HOST} \\
                                    "docker login -u $DOCKER_USER -p $DOCKER_PASS && \\
                                     docker compose -f ${VM_REPO_PATH}/docker/compose.dev.yaml pull ${svc} && \\
                                     docker compose -f ${VM_REPO_PATH}/docker/compose.dev.yaml up -d ${svc}"
                                """
                            }
                        }
                    }
                }
            }
        }

        stage('Update VM Repo') {
            when { expression { return env.skipRemainingStages != "true" } }
            steps {
                script {
                    echo "🔄 Обновляем локальную копию репозитория на VM"
                    sshagent([SSH_CRED_ID]) {
                        sh """
                            ssh -o StrictHostKeyChecking=no ${VM_USER}@${VM_HOST} \\
                            "cd ${VM_REPO_PATH} && git fetch origin master && git reset --hard HEAD && git pull origin master"
                        """
                    }
                }
            }
        }
    }

    post {
        always {
            script {
                echo "🔄 Синхронизируем workspace с GitHub"
                sh """
                    git fetch origin master
                    git reset --hard origin/master
                """
            }
        }
    }
}