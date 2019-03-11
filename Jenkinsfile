
pipeline {
    agent any

    options {
        skipStagesAfterUnstable()
    }

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