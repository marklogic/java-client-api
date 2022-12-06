@Library('shared-libraries') _

def runtests(String type, String version){
            copyRPM type, version
            setUpML '$WORKSPACE/xdmp/src/Mark*.rpm'
            copyConvertersRPM type,version
            setUpMLConverters '$WORKSPACE/xdmp/src/Mark*Converters*.rpm'
            sh label:'deploy project', script: '''#!/bin/bash
                export JAVA_HOME=$JAVA_HOME_DIR
                export GRADLE_USER_HOME=$WORKSPACE/$GRADLE_DIR
                export PATH=$GRADLE_USER_HOME:$JAVA_HOME/bin:$PATH
                cd java-client-api
                ./gradlew mlDeploy
            '''
            sh label:'marklogic client test', script: '''#!/bin/bash
                export JAVA_HOME=$JAVA_HOME_DIR
                export GRADLE_USER_HOME=$WORKSPACE/$GRADLE_DIR
                export PATH=$GRADLE_USER_HOME:$JAVA_HOME/bin:$PATH
                cd java-client-api
                ./gradlew marklogic-client-api:test  || true
            '''
            sh label:'ml development tool test', script: '''#!/bin/bash
                export JAVA_HOME=$JAVA_HOME_DIR
                export GRADLE_USER_HOME=$WORKSPACE/$GRADLE_DIR
                export PATH=$GRADLE_USER_HOME:$JAVA_HOME/bin:$PATH
                cd java-client-api
                ./gradlew ml-development-tools:setupTestServer || true
                ./gradlew ml-development-tools:generateTests || true
                ./gradlew ml-development-tools:test || true
            '''
            sh label:'test', script: '''#!/bin/bash
                export JAVA_HOME=$JAVA_HOME_DIR
                export GRADLE_USER_HOME=$WORKSPACE/$GRADLE_DIR
                export PATH=$GRADLE_USER_HOME:$JAVA_HOME/bin:$PATH
                cd java-client-api
                ./gradlew marklogic-client-api-functionaltests:test || true
            '''
            sh label:'post-test-process', script: '''
                cd $WORKSPACE/java-client-api/marklogic-client-api/build/test-results/test/
                sed -i "s/classname=\\"/classname=\\"${STAGE_NAME}-/g" TEST*.xml
                cd $WORKSPACE/java-client-api/ml-development-tools/build/test-results/test/
                sed -i "s/classname=\\"/classname=\\"${STAGE_NAME}-/g" TEST*.xml
                cd $WORKSPACE/java-client-api/marklogic-client-api-functionaltests/build/test-results/test/
                sed -i "s/classname=\\"/classname=\\"${STAGE_NAME}-/g" TEST*.xml
            '''
}

pipeline{
  agent {label 'javaClientLinuxPool'}
  options {
    checkoutToSubdirectory 'java-client-api'
    buildDiscarder logRotator(artifactDaysToKeepStr: '7', artifactNumToKeepStr: '', daysToKeepStr: '30', numToKeepStr: '')
  }
  parameters{
    booleanParam(name: 'regressions', defaultValue: false, description: 'indicator if build is for regressions')
    string(name: 'Email', defaultValue: '' ,description: 'Who should I say send the email to?')
  }
  environment{
    JAVA_HOME_DIR="/home/builder/java/openjdk-1.8.0-262"
    GRADLE_DIR   =".gradle"
    DMC_USER     = credentials('MLBUILD_USER')
    DMC_PASSWORD = credentials('MLBUILD_PASSWORD')
  }
  stages{
    stage('tests'){
      steps{
        copyRPM 'Latest','11.0'
        setUpML '$WORKSPACE/xdmp/src/Mark*.rpm'
        copyConvertersRPM 'Latest','11.0'
        setUpMLConverters '$WORKSPACE/xdmp/src/Mark*Converters*.rpm'
        sh label:'deploy project', script: '''#!/bin/bash
          export JAVA_HOME=$JAVA_HOME_DIR
          export GRADLE_USER_HOME=$WORKSPACE/$GRADLE_DIR
          export PATH=$GRADLE_USER_HOME:$JAVA_HOME/bin:$PATH
          cd java-client-api
          ./gradlew -i mlDeploy -PmlForestDataDirectory=/space
        '''
        sh label:'marklogic client test', script: '''#!/bin/bash
          export JAVA_HOME=$JAVA_HOME_DIR
          export GRADLE_USER_HOME=$WORKSPACE/$GRADLE_DIR
          export PATH=$GRADLE_USER_HOME:$JAVA_HOME/bin:$PATH
          cd java-client-api
          ./gradlew marklogic-client-api:test  || true
        '''
        sh label:'ml development tool test', script: '''#!/bin/bash
          export JAVA_HOME=$JAVA_HOME_DIR
          export GRADLE_USER_HOME=$WORKSPACE/$GRADLE_DIR
          export PATH=$GRADLE_USER_HOME:$JAVA_HOME/bin:$PATH
          cd java-client-api
          ./gradlew ml-development-tools:setupTestServer || true
          ./gradlew ml-development-tools:generateTests || true
          ./gradlew ml-development-tools:test || true
        '''
        sh '''
            cd $WORKSPACE/java-client-api/marklogic-client-api/build/test-results/test/
            sed -i "s/classname=\\"/classname=\\"${STAGE_NAME}-/g" TEST*.xml
            cd $WORKSPACE/java-client-api/ml-development-tools/build/test-results/test/
            sed -i "s/classname=\\"/classname=\\"${STAGE_NAME}-/g" TEST*.xml
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
    stage('functional-tests'){
         when{
            allOf{
                branch 'develop'
                expression {return params.regressions}
            }
        }
        steps{
        copyRPM 'Latest','11.0'
        setUpML '$WORKSPACE/xdmp/src/Mark*.rpm'
        copyConvertersRPM 'Latest','11.0'
        setUpMLConverters '$WORKSPACE/xdmp/src/Mark*Converters*.rpm'
        sh label:'test', script: '''#!/bin/bash
          export JAVA_HOME=$JAVA_HOME_DIR
          export GRADLE_USER_HOME=$WORKSPACE/$GRADLE_DIR
          export PATH=$GRADLE_USER_HOME:$JAVA_HOME/bin:$PATH
          cd java-client-api
          ./gradlew -i mlDeploy -PmlForestDataDirectory=/space
          ./gradlew marklogic-client-api-functionaltests:test || true
        '''
        sh '''
            cd $WORKSPACE/java-client-api/marklogic-client-api-functionaltests/build/test-results/test/
            sed -i "s/classname=\\"/classname=\\"${STAGE_NAME}-/g" TEST*.xml
        '''
        junit '**/marklogic-client-api-functionaltests/**/build/**/TEST*.xml'
        }
        post{
            unsuccessful{
                sendMail params.Email,'<h3>Some Tests Failed on Released 11.0 ML Nightly Server Single Node </h3><h4><a href=${JENKINS_URL}/blue/organizations/jenkins/java-client-api-regression/detail/$JOB_BASE_NAME/$BUILD_ID/tests><font color=red>Check the Test Report</font></a></h4><h4><a href=${RUN_DISPLAY_URL}>Check the Pipeline View</a></h4><h4> <a href=${BUILD_URL}/console> Check Console Output Here</a></h4><h4>Please create bugs for the failed regressions and fix them</h4>',false,'${STAGE_NAME} on  develop against ML 11.0-nightly Failed'
            }
        }
    }
  }
}
