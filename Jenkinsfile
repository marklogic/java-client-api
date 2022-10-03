@Library('shared-libraries') _
pipeline{
  triggers{
    parameterizedCron(env.BRANCH_NAME == "develop" ? "00 02 * * * % regressions=true" : "")
  }
  agent {label 'javaClientLinuxPool'}
  options {
    checkoutToSubdirectory 'java-client-api'
    buildDiscarder logRotator(artifactDaysToKeepStr: '7', artifactNumToKeepStr: '', daysToKeepStr: '30', numToKeepStr: '')
  }
  parameters{
    booleanParam(name: 'regressions', defaultValue: false, description: 'indicator if build is for regressions')
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
        junit '**/build/**/TEST*.xml'
      }
    }
    stage('functional-tests'){
        when { branch 'develop'}
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
          ./gradlew marklogic-client-api-functionaltests:test  || true
        '''
        junit '**/build/**/TEST*.xml'
        }
    }
    stage('regressions-10.0-9'){
        when{
            allOf{
                branch 'develop'
                expression {return params.regressions}
            }
        }
        steps{
            copyRPM 'Release','10.0-9.5'
            setUpML '$WORKSPACE/xdmp/src/Mark*.rpm'
            copyConvertersRPM 'Release','10.0-9.5'
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
                ./gradlew marklogic-client-api-functionaltests:test  || true
            '''
            junit '**/build/**/TEST*.xml'
        }
    }
  }
}
