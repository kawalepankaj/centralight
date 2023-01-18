////line {
    agent any
    stages {
    stage('git pull') {
        steps {
            echo 'git pull from origin'
        }
    }
     stage('build') {
         steps {
              echo 'sudo apt update -y build'
            }
        }
        stage('test') {
            steps {
                echo 'sudo apt update -y test'
            }
        }
        stage('deploy') {
            steps {
                echo 'sudo apt update -y deploy'
            }
        }            
    }
}
