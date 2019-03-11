pipeline {
    agent any

    options {
        skipStagesAfterUnstable()
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

}