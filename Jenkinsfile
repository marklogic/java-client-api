@Library('shared-libraries') _

def getJava(){
    if(env.JAVA_VERSION=="JAVA17"){
        return "/home/builder/java/jdk-17.0.2"
    }else if(env.JAVA_VERSION=="JAVA11"){
        return "/home/builder/java/jdk-11.0.2"
    }else{
        return "/home/builder/java/openjdk-1.8.0-262"
    }
}

def runtests(String type, String version){
            copyRPM type, version
            setUpML '$WORKSPACE/xdmp/src/Mark*.rpm'
            copyConvertersRPM type,version
            setUpMLConverters '$WORKSPACE/xdmp/src/Mark*Converters*.rpm'
            sh label:'deploy test app', script: '''#!/bin/bash
                export JAVA_HOME=$JAVA_HOME_DIR
                export GRADLE_USER_HOME=$WORKSPACE/$GRADLE_DIR
                export PATH=$GRADLE_USER_HOME:$JAVA_HOME/bin:$PATH
                cd java-client-api
                ./gradlew -i mlDeploy -PmlForestDataDirectory=/space
            '''
            sh label:'run marklogic-client-api tests', script: '''#!/bin/bash
                export JAVA_HOME=$JAVA_HOME_DIR
                export GRADLE_USER_HOME=$WORKSPACE/$GRADLE_DIR
                export PATH=$GRADLE_USER_HOME:$JAVA_HOME/bin:$PATH
                cd java-client-api
                ./gradlew marklogic-client-api:test  || true
            '''
            sh label:'run ml-development-tools tests', script: '''#!/bin/bash
                export JAVA_HOME=$JAVA_HOME_DIR
                export GRADLE_USER_HOME=$WORKSPACE/$GRADLE_DIR
                export PATH=$GRADLE_USER_HOME:$JAVA_HOME/bin:$PATH
                cd java-client-api
                ./gradlew ml-development-tools:setupTestServer || true
                ./gradlew ml-development-tools:generateTests || true
                ./gradlew ml-development-tools:test || true
            '''
            sh label:'run functional tests', script: '''#!/bin/bash
                export JAVA_HOME=$JAVA_HOME_DIR
                export GRADLE_USER_HOME=$WORKSPACE/$GRADLE_DIR
                export PATH=$GRADLE_USER_HOME:$JAVA_HOME/bin:$PATH
                cd java-client-api
                ./gradlew marklogic-client-api-functionaltests:runFunctionalTests || true
            '''
            sh label:'post-test-process', script: '''
                cd $WORKSPACE/java-client-api/marklogic-client-api/build/test-results/test/
                sed -i "s/classname=\\"/classname=\\"${STAGE_NAME}-/g" TEST*.xml
                cd $WORKSPACE/java-client-api/ml-development-tools/build/test-results/test/
                sed -i "s/classname=\\"/classname=\\"${STAGE_NAME}-/g" TEST*.xml
                cd $WORKSPACE/java-client-api/marklogic-client-api-functionaltests/build/test-results/runFragileTests/
                sed -i "s/classname=\\"/classname=\\"${STAGE_NAME}-/g" TEST*.xml
                cd $WORKSPACE/java-client-api/marklogic-client-api-functionaltests/build/test-results/runFastFunctionalTests/
                sed -i "s/classname=\\"/classname=\\"${STAGE_NAME}-/g" TEST*.xml
                cd $WORKSPACE/java-client-api/marklogic-client-api-functionaltests/build/test-results/runSlowFunctionalTests/
                sed -i "s/classname=\\"/classname=\\"${STAGE_NAME}-/g" TEST*.xml
            '''
}

pipeline{
  agent {label 'javaClientLinuxPool'}
  options {
    checkoutToSubdirectory 'java-client-api'
    buildDiscarder logRotator(artifactDaysToKeepStr: '7', artifactNumToKeepStr: '', daysToKeepStr: '7', numToKeepStr: '10')
  }
  parameters{
    booleanParam(name: 'regressions', defaultValue: false, description: 'indicator if build is for regressions')
    string(name: 'Email', defaultValue: '' ,description: 'Who should I say send the email to?')
    string(name: 'JAVA_VERSION', defaultValue: 'JAVA8' ,description: 'Who should I say send the email to?')
  }
  environment{
    JAVA_HOME_DIR= getJava()
    GRADLE_DIR   =".gradle"
    DMC_USER     = credentials('MLBUILD_USER')
    DMC_PASSWORD = credentials('MLBUILD_PASSWORD')
  }
  stages {
    stage('pull-request-tests') {
      when {
        not {
          expression {return params.regressions}
        }
      }
      steps {
        copyRPM 'Latest','11.0'
        setUpML '$WORKSPACE/xdmp/src/Mark*.rpm'
        copyConvertersRPM 'Latest','11.0'
        setUpMLConverters '$WORKSPACE/xdmp/src/Mark*Converters*.rpm'
        sh label:'deploy test app', script: '''#!/bin/bash
          export JAVA_HOME=$JAVA_HOME_DIR
          export GRADLE_USER_HOME=$WORKSPACE/$GRADLE_DIR
          export PATH=$GRADLE_USER_HOME:$JAVA_HOME/bin:$PATH
          cd java-client-api
          ./gradlew -i mlDeploy -PmlForestDataDirectory=/space
        '''
        sh label:'run marklogic-client-api tests', script: '''#!/bin/bash
          export JAVA_HOME=$JAVA_HOME_DIR
          export GRADLE_USER_HOME=$WORKSPACE/$GRADLE_DIR
          export PATH=$GRADLE_USER_HOME:$JAVA_HOME/bin:$PATH
          cd java-client-api
          ./gradlew -i marklogic-client-api:test  || true
        '''
        junit '**/build/**/TEST*.xml'
      }
      post{
        unsuccessful{
            script{
                if(params.regressions){
                    sendMail params.Email,'<h3>Some Tests Failed on Released 11.0 ML Nightly Server Single Node </h3><h4><a href=${JENKINS_URL}/blue/organizations/jenkins/java-client-api-regression/detail/$JOB_BASE_NAME/$BUILD_ID/tests><font color=red>Check the Test Report</font></a></h4><h4><a href=${RUN_DISPLAY_URL}>Check the Pipeline View</a></h4><h4> <a href=${BUILD_URL}/console> Check Console Output Here</a></h4><h4>Please create bugs for the failed regressions and fix them</h4>',false,'${STAGE_NAME} on  develop against ML 11.0-nightly Failed'
                }
            }
        }
      }
    }

    stage('regressions-11.0-Latest') {
      when {
        allOf {
          branch 'develop'
          expression {return params.regressions}
        }
      }
      steps {
        runtests('Latest','11.0')
        junit '**/build/**/TEST*.xml'
      }
      post {
        unsuccessful {
          sendMail params.Email,'<h3>Some Tests Failed on Released 11.0 ML Nightly Server Single Node </h3><h4><a href=${JENKINS_URL}/blue/organizations/jenkins/java-client-api-regression/detail/$JOB_BASE_NAME/$BUILD_ID/tests><font color=red>Check the Test Report</font></a></h4><h4><a href=${RUN_DISPLAY_URL}>Check the Pipeline View</a></h4><h4> <a href=${BUILD_URL}/console> Check Console Output Here</a></h4><h4>Please create bugs for the failed regressions and fix them</h4>',false,'${STAGE_NAME} on  develop against ML 11.0-nightly Failed'
        }
      }
    }

    stage('regressions-10.0-9') {
      when {
        allOf {
          branch 'develop'
          expression {return params.regressions}
        }
      }
      steps {
        runtests('Release','10.0-9.5')
        junit '**/build/**/TEST*.xml'
      }
      post {
        unsuccessful {
          sendMail params.Email,'<h3>Some Tests Failed on Released 10.0-9.5 ML  Server Single Node </h3><h4><a href=${JENKINS_URL}/blue/organizations/jenkins/java-client-api-regression/detail/$JOB_BASE_NAME/$BUILD_ID/tests><font color=red>Check the Test Report</font></a></h4><h4><a href=${RUN_DISPLAY_URL}>Check the Pipeline View</a></h4><h4> <a href=${BUILD_URL}/console> Check Console Output Here</a></h4><h4>Please create bugs for the failed regressions and fix them</h4>',false,'${STAGE_NAME} on  develop against ML 10.0-9.5 Failed'
        }
      }
    }
  }
}
