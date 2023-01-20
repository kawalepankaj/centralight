pipeline {
    agent {
        label('ECS-agent')  
    }
    stages {
        stage ('git pull origin') {
            steps {
                git credentialsId: 'pankaj', url: 'https://github.com/kawalepankaj/student-ui.git' 
            }
        }
        stage ('maven build') {
            steps {
                sh 'sudo apt update -y'
                sh 'sudo apt install maven -y'
                sh 'mvn clean package'
            }
        }
        stage ('push-artifact') {
            steps {
            sh 'sudo apt install awscli -y'
            }
        }    
    }
}