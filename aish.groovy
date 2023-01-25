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
                withCredentials([sshUserPrivateKey(credentialsId: 'tomm1', keyFileVariable: 'tommm1', usernameVariable: 'ubuntu')]) {
                    sh '''
                    ssh -i ${tommm1} -o StrictHostKeyChecking=no ubuntu@54.211.238.104<<EOF
                    sudo apt-get update -y
                    sudo apt-get install unzip -y
                    sudo apt-get install default-jre -y
                    sudo apt-get install default-jdk -y
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