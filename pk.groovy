pipeline {
    agent {
        node ('agent-ec2')
    }
    stages {
        stage('git-pull'){
            steps {
                sh 'sudo apt-get update -y'
                sh 'sudo apt-get install git -y'
                git 'https://github.com/kawalepankaj/centralight.git'
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
                curl "https://awscli.amazonaws.com/awscli-exe-linux-x86_64.zip" -o "awscliv2.zip"
                unzip awscliv2.zip
                sudo ./aws/install
                aws s3 ls
                sudo mv /home/ubuntu/workspace/jenkins-second/target/studentapp-2.2-SNAPSHOT.war /home/ubuntu/student-${BUILD_ID}.war
                aws s3 cp /home/ubuntu/student-${BUILD_ID}.war s3://tomcat-installation
                '''
           }
        }
        stage('tomcat-deploy'){
            steps {
                withCredentials([sshUserPrivateKey(credentialsId: '95be8677-08fe-4b50-86b0-df1b5fd4fd98', keyFileVariable: 'tomcat', usernameVariable: 'ubuntu')]) {
                    sh'''
                    ssh -i ${new} -o StrictHostKeyChecking=no ubuntu@13.233.186.255<<EOF
                    sudo apt-get update -y
                    sudo apt-get install unzip -y
                        sudo apt-get install openjdk-11-jre -y
                        curl "https://awscli.amazonaws.com/awscli-exe-linux-x86_64.zip" -o "awscliv2.zip"
                        unzip awscliv2.zip
                        sudo ./aws/install
                        aws s3 cp s3://tomcat-installation/student-${BUILD_ID}.war .
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