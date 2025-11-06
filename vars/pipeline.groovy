import com.udatha.build.Cal 

def call(Map pipelineParams){
    Cal cal = new Cal(this)
}

pipeline {
    agent any
    environment{
        APP = "${pipelineParams.myapp}"
    }
    parameters {
        string(name: 'user', defaultValue: 'lokesh', description: 'Enter your name here:')
    }
    stages {
        stage('para') {
            when {
                expression {
                    return params.user == 'lokesh'
                }
            }
            steps {
                sh '''
                    mkdir -p udatha
                    cd udatha
                    echo "success"
                '''
            }
        }
    }
}
