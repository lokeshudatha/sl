import com.lokesh.build.cal 

def call(Map pipelineParams) {
    Cal one = new Cal(this)
}

pipeline {
    agent any
    environment {
        myapp = "${pipelineParams.lokeshapp}"
    }
    
    parameters {
        string(name: 'USER', defaultValue: 'lokesh udatha', description: 'Enter your full name here: ')
        booleanParam(name: 'Run', defaultValue: true, description: 'Running your test cases')
        choice(name: 'Env', choices: ['build', 'test', 'prod'], description: 'Select one environment here: ')
    }

    tools {
        maven 'Maven3'
        jdk 'JDK17'
    }

    options {
        timeout(time: 02, unit: 'SECONDS')
    }

    triggers {
        cron('*/1 * * * *')
        pollSCM('*/1 * * * *')
    }
    stages {
        stage('Input Approval') {
            steps {
                input message: 'Do you want to proceed?', 
                ok: 'yes, continue'
                echo "stage one is complate"
            }

        }
        stage('Setup') {
            steps{
                sh 'mvn --version'
                sh 'java -version'
            }
        }
        stage('Build and Test') {
            parallel {
                stage('Build') {
                    steps {
                        script {
                            try {
                                echo "if  ${env.myapp} is correct print the below command"
                                sh 'mvn clean package'
                            } catch {
                                error ("stopping this pipeline here.")
                            } finally {
                                echo "Build stage is complated"
                            }
                        }
                    }
                    stage('Test') {
                        when {
                            expression {
                                return params.Run == true
                            }
                        }
                        steps {
                            echo "running unit tests..."
                            sh 'mvn test'
                        }
                    }
                }
            }
        }
        stage('Deploy') {
            when {
                allOf {
                    expression {
                        return params.Env == 'build'
                    }
                    expression {
                        return params.Env == 'test'
                    }
                }
            }
            steps {
                script {
                    if (params.Env == 'prod') {
                        echo "Producation is running proper"
                    } else {
                        echo "some errors in producation"
                    }
                }
            }
        }

    }
    post {
        success {
            echo "Pipeline is working good"
        } failure {
            echo "Pipeline is not working properlly"
        } always {
            echo "Pipeline execution is complated"
        }
    }

}