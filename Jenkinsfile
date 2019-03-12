pipeline {
    agent any

    options {
        skipStagesAfterUnstable()
    }

    tools {
        maven 'maven'
    }


    stages {


        stage('Build') {
            steps {
                sh 'mvn compile'
            }
        }

        stage('Test') {
            steps {
                sh 'mvn test'
            }
        }

        stage('Deploy') {
            steps {
                sh 'mvn package'
            }
        }
    }

    post {
        always {
            echo "Pipeline is done"
        }
    }

}