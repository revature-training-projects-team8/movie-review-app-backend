pipeline {
    agent any
    
    environment {
        SECRET_BUCKET = 'kyles-secret-bucket'
        DOCKER_IMAGE = 'movie-review-app-backend'
        EXTERNAL_PORT = '8088'
        INTERNAL_PORT = '8080'
    }
    
    stages {
        stage('Checkout') {
            steps {
                git branch: 'main',
                    url: 'https://github.com/revature-training-projects-team8/movie-review-app-backend.git'
            }
        }
        
        stage('Fetch Secrets') {
            steps {
                sh 'aws s3 cp s3://kyles-secret-bucket/team8/application.properties movie-review-app-backend/src/main/resources/'
            }
        }
        
        stage('Build Spring Backend') {
            steps {
                dir('movie-review-app-backend') {
                    sh 'mvn clean package -DskipTests'
                }
            }
        }
        
        stage('Cleanup Old Docker Resources') {
            steps {
                sh '''
                    docker stop ${DOCKER_IMAGE} || true
                    docker rm ${DOCKER_IMAGE} || true
                    docker system prune -a -f
                '''
            }
        }
        
        stage('Build Docker Image') {
            steps {
                sh 'docker build -t ${DOCKER_IMAGE}:${BUILD_NUMBER} .'
                sh 'docker tag ${DOCKER_IMAGE}:${BUILD_NUMBER} ${DOCKER_IMAGE}:latest'
            }
        }
        
        stage('Deploy Backend Container') {
            steps {
                sh 'docker run -d --name ${DOCKER_IMAGE} -p ${EXTERNAL_PORT}:${INTERNAL_PORT} ${DOCKER_IMAGE}:latest'
            }
        }
    }
    
    post {
        success {
            echo 'Deployment successful!'
        }
        failure {
            echo 'Deployment failed!'
        }
    }
}