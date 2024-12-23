@Library('shared-libraries') _

def getJava(){
    if(env.JAVA_VERSION=="JAVA17"){
        return "/home/builder/java/jdk-17.0.2"
    }else if(env.JAVA_VERSION=="JAVA11"){
        return "/home/builder/java/jdk-11.0.2"
    }else if(env.JAVA_VERSION=="JAVA21"){
		return "/home/builder/java/jdk-21.0.1"
	}else{
        return "/home/builder/java/openjdk-1.8.0-262"
    }
}

def runAllTests(String type, String version, Boolean useReverseProxy){
            copyRPM type, version
            sh 'sudo /usr/local/sbin/mladmin removeforest /space/Forests'
            setUpML '$WORKSPACE/xdmp/src/Mark*.rpm'
            copyConvertersRPM type,version
            setUpMLConverters '$WORKSPACE/xdmp/src/Mark*Converters*.rpm'

            sh label:'deploy test app', script: '''#!/bin/bash
                export JAVA_HOME=$JAVA_HOME_DIR
                export GRADLE_USER_HOME=$WORKSPACE/$GRADLE_DIR
                export PATH=$GRADLE_USER_HOME:$JAVA_HOME/bin:$PATH
                cd java-client-api
                ./gradlew -i mlDeploy mlReloadSchemas -PmlForestDataDirectory=/space
            '''

            if (useReverseProxy) {
							sh label:'run marklogic-client-api tests with reverse proxy', script: '''#!/bin/bash
									export JAVA_HOME=$JAVA_HOME_DIR
									export GRADLE_USER_HOME=$WORKSPACE/$GRADLE_DIR
									export PATH=$GRADLE_USER_HOME:$JAVA_HOME/bin:$PATH
									cd java-client-api
									mkdir -p marklogic-client-api/build/test-results/test
                  ./gradlew -PtestUseReverseProxyServer=true test-app:runReverseProxyServer marklogic-client-api:test || true
							'''
            } else {
							sh label:'run marklogic-client-api tests', script: '''#!/bin/bash
									export JAVA_HOME=$JAVA_HOME_DIR
									export GRADLE_USER_HOME=$WORKSPACE/$GRADLE_DIR
									export PATH=$GRADLE_USER_HOME:$JAVA_HOME/bin:$PATH
									cd java-client-api
									mkdir -p marklogic-client-api/build/test-results/test
									./gradlew marklogic-client-api:test  || true
							'''
            }

            sh label:'run ml-development-tools tests', script: '''#!/bin/bash
                export JAVA_HOME=$JAVA_HOME_DIR
                export GRADLE_USER_HOME=$WORKSPACE/$GRADLE_DIR
                export PATH=$GRADLE_USER_HOME:$JAVA_HOME/bin:$PATH
                cd java-client-api
                mkdir -p ml-development-tools/build/test-results/test
                ./gradlew ml-development-tools:test || true
            '''

            if (useReverseProxy) {
            	sh label:'run fragile functional tests with reverse proxy', script: '''#!/bin/bash
									export JAVA_HOME=$JAVA_HOME_DIR
									export GRADLE_USER_HOME=$WORKSPACE/$GRADLE_DIR
									export PATH=$GRADLE_USER_HOME:$JAVA_HOME/bin:$PATH
									cd java-client-api
									./gradlew mlDeploy -PmlForestDataDirectory=/space
									./gradlew -PtestUseReverseProxyServer=true test-app:runReverseProxyServer marklogic-client-api-functionaltests:runFragileTests || true
							'''
            } else {
							sh label:'run fragile functional tests', script: '''#!/bin/bash
									export JAVA_HOME=$JAVA_HOME_DIR
									export GRADLE_USER_HOME=$WORKSPACE/$GRADLE_DIR
									export PATH=$GRADLE_USER_HOME:$JAVA_HOME/bin:$PATH
									cd java-client-api
									./gradlew mlDeploy -PmlForestDataDirectory=/space
									./gradlew marklogic-client-api-functionaltests:runFragileTests || true
							'''
            }

            if (useReverseProxy) {
							sh label:'run fast functional tests with reverse proxy', script: '''#!/bin/bash
									export JAVA_HOME=$JAVA_HOME_DIR
									export GRADLE_USER_HOME=$WORKSPACE/$GRADLE_DIR
									export PATH=$GRADLE_USER_HOME:$JAVA_HOME/bin:$PATH
									cd java-client-api
									./gradlew -PtestUseReverseProxyServer=true test-app:runReverseProxyServer marklogic-client-api-functionaltests:runFastFunctionalTests || true
							'''
            } else {
							sh label:'run fast functional tests', script: '''#!/bin/bash
									export JAVA_HOME=$JAVA_HOME_DIR
									export GRADLE_USER_HOME=$WORKSPACE/$GRADLE_DIR
									export PATH=$GRADLE_USER_HOME:$JAVA_HOME/bin:$PATH
									cd java-client-api
									./gradlew marklogic-client-api-functionaltests:runFastFunctionalTests || true
							'''
            }

            if (useReverseProxy) {
            	sh label:'run slow functional tests with reverse proxy', script: '''#!/bin/bash
									export JAVA_HOME=$JAVA_HOME_DIR
									export GRADLE_USER_HOME=$WORKSPACE/$GRADLE_DIR
									export PATH=$GRADLE_USER_HOME:$JAVA_HOME/bin:$PATH
									cd java-client-api
									./gradlew -PtestUseReverseProxyServer=true test-app:runReverseProxyServer marklogic-client-api-functionaltests:runSlowFunctionalTests || true
							'''
            } else {
							sh label:'run slow functional tests', script: '''#!/bin/bash
									export JAVA_HOME=$JAVA_HOME_DIR
									export GRADLE_USER_HOME=$WORKSPACE/$GRADLE_DIR
									export PATH=$GRADLE_USER_HOME:$JAVA_HOME/bin:$PATH
									cd java-client-api
									./gradlew marklogic-client-api-functionaltests:runSlowFunctionalTests || true
							'''
            }

            sh label:'post-test-process', script: '''
            		cd java-client-api
								mkdir -p marklogic-client-api-functionaltests/build/test-results/runFragileTests
								mkdir -p marklogic-client-api-functionaltests/build/test-results/runFastFunctionalTests
								mkdir -p marklogic-client-api-functionaltests/build/test-results/runSlowFunctionalTests
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
        copyRPM 'Latest','11'
        sh 'sudo /usr/local/sbin/mladmin removeforest /space/Forests'
        setUpML '$WORKSPACE/xdmp/src/Mark*.rpm'
        copyConvertersRPM 'Latest','11'
        setUpMLConverters '$WORKSPACE/xdmp/src/Mark*Converters*.rpm'
        sh label:'deploy test app', script: '''#!/bin/bash
          export JAVA_HOME=$JAVA_HOME_DIR
          export GRADLE_USER_HOME=$WORKSPACE/$GRADLE_DIR
          export PATH=$GRADLE_USER_HOME:$JAVA_HOME/bin:$PATH
          cd java-client-api
          ./gradlew -i mlDeploy mlReloadSchemas -PmlForestDataDirectory=/space
        '''
        sh label:'run marklogic-client-api tests', script: '''#!/bin/bash
          export JAVA_HOME=$JAVA_HOME_DIR
          export GRADLE_USER_HOME=$WORKSPACE/$GRADLE_DIR
          export PATH=$GRADLE_USER_HOME:$JAVA_HOME/bin:$PATH
          cd java-client-api
          ./gradlew cleanTest marklogic-client-api:test
          ./gradlew -PtestUseReverseProxyServer=true test-app:runReverseProxyServer marklogic-client-api-functionaltests:runFastFunctionalTests || true
        '''
        junit '**/build/**/TEST*.xml'
      }
    }
    stage('publish'){
      when {
        branch 'develop'
        not {
          expression {return params.regressions}
        }
      }
      steps{
        sh label:'publish', script: '''#!/bin/bash
          export JAVA_HOME=$JAVA_HOME_DIR
          export GRADLE_USER_HOME=$WORKSPACE/$GRADLE_DIR
          export PATH=$GRADLE_USER_HOME:$JAVA_HOME/bin:$PATH
          cp ~/.gradle/gradle.properties $GRADLE_USER_HOME;
          cd java-client-api
          ./gradlew publish
        '''
      }
    }

		stage('regressions-11.2.0') {
			when {
				allOf {
					branch 'develop'
					expression {return params.regressions}
				}
			}
			steps {
			runAllTests('Release', '11.2.0', false)
				junit '**/build/**/TEST*.xml'
			}
		}

		stage('regressions-11') {
			when {
				allOf {
					branch 'develop'
					expression {return params.regressions}
				}
			}
			steps {
				runAllTests('Latest', '11', false)
				junit '**/build/**/TEST*.xml'
			}
		}

		stage('regressions-11-reverseProxy') {
			when {
				allOf {
					branch 'develop'
					expression {return params.regressions}
				}
			}
			steps {
				runAllTests('Latest', '11', true)
				junit '**/build/**/TEST*.xml'
			}
		}

		stage('regressions-12') {
			when {
				allOf {
					branch 'develop'
					expression {return params.regressions}
				}
			}
			steps {
				runAllTests('Latest', '12', false)
				junit '**/build/**/TEST*.xml'
			}
		}

		stage('regressions-10.0') {
			when {
				allOf {
					branch 'develop'
					expression {return params.regressions}
				}
			}
			steps {
				runAllTests('Latest', '10.0', false)
				junit '**/build/**/TEST*.xml'
			}
		}

		stage('regressions-10.0-10.2') {
			when {
				allOf {
					branch 'develop'
					expression {return params.regressions}
				}
			}
			steps {
				runAllTests('Release', '10.0-10.2', false)
				junit '**/build/**/TEST*.xml'
			}
		}

  }
}
