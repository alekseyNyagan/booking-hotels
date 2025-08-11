FROM jenkins/jenkins:lts

USER root

# Установка зависимостей и Docker Engine + Compose V2
RUN apt-get update && apt-get install -y \
    ca-certificates \
    curl \
    gnupg \
    lsb-release && \
    install -m 0755 -d /etc/apt/keyrings && \
    curl -fsSL https://download.docker.com/linux/debian/gpg | gpg --dearmor -o /etc/apt/keyrings/docker.gpg && \
    echo "deb [arch=$(dpkg --print-architecture) signed-by=/etc/apt/keyrings/docker.gpg] \
    https://download.docker.com/linux/debian $(lsb_release -cs) stable" \
    > /etc/apt/sources.list.d/docker.list && \
    apt-get update && \
    apt-get install -y containerd.io docker-buildx-plugin docker-ce docker-ce-cli docker-compose-plugin && \
    apt-get clean && \
    usermod -aG docker jenkins

USER jenkins
