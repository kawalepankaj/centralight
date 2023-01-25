pipeline {
    agent any
    stages {
        stage('git-pull'){
            steps {
                sh 'sudo apt-get update -y'
                sh 'sudo apt-get install git -y'
                git 'https://github.com/kawalepankaj/student-ui.git'
           }
        }
        stage('maven-build'){
            steps {
                sh 'sudo apt-get update -y'
                sh 'sudo apt-get install maven -y'
                sh 'mvn clean package'
            }
        }
        stage('push-artifact'){
            steps { 
                sh '''
                sudo apt-get install unzip -y
                sudo apt install awscli -y
                aws s3 ls
                sudo mv /var/lib/jenkins/workspace/aish/target/studentapp-2.2-SNAPSHOT.war /mnt/student-${BUILD_ID}.war
                aws s3 cp /mnt/student-${BUILD_ID}.war s3://studentapp-arti12
                '''
           }
        }
        stage('tomcat-deploy'){
            steps {
                withCredentials([sshUserPrivateKey(credentialsId: 'tommy1', keyFileVariable: 'tommy1', usernameVariable: 'tommy1')]) {
                    sh '''
                    ssh -i ${tommy1} -o -t StrictHostKeyChecking=no ubuntu@44.210.125.94<<'EOT'
                    sudo apt-get update -y
                    sudo apt-get install unzip -y
                    sudo apt-get install openjdk-11-jre -y
                    sudo apt-get install openjdk-11-jdk -y
                    sudo apt install awscli -y
                    aws s3 cp s3://tomcat-installation/student-${BUILD_ID}.war .
                    sudo ls
                    curl -O https://dlcdn.apache.org/tomcat/tomcat-8/v8.5.85/bin/apache-tomcat-8.5.85.tar.gz
                    sudo tar -xvf apache-tomcat-8.5.85.tar.gz -C /opt/
                    sudo sh /opt/apache-tomcat-8.5.85/bin/shutdown.sh
                    sudo cp -rv student-${BUILD_ID}.war studentapp.war
                    sudo cp -rv studentapp.war /opt/apache-tomcat-8.5.85/webapps/
                    sudo sh /opt/apache-tomcat-8.5.85/bin/startup.sh
                    '''
                }
            }
        }
     }            
}