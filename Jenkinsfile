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

def setupDockerMarkLogic(String image){
	sh label:'mlsetup', script: '''#!/bin/bash
	echo "Removing any running MarkLogic server and clean up MarkLogic data directory"
    sudo /usr/local/sbin/mladmin remove
    sudo /usr/local/sbin/mladmin cleandata
    cd java-client-api/test-app
    docker compose down -v || true
    docker volume prune -f
    echo "Using image: "'''+image+'''
    docker pull '''+image+'''
    MARKLOGIC_IMAGE='''+image+''' MARKLOGIC_LOGS_VOLUME=marklogicLogs docker compose up -d --build
	  echo "mlPassword=admin" > gradle-local.properties
    echo "Waiting for MarkLogic server to initialize."
    sleep 60s
    cd ..
	  echo "mlPassword=admin" > gradle-local.properties
		export GRADLE_USER_HOME=$WORKSPACE/$GRADLE_DIR
		export PATH=$GRADLE_USER_HOME:$JAVA_HOME/bin:$PATH
		./gradlew mlTestConnections
   	./gradlew -i mlDeploy mlReloadSchemas
  '''
}

def runTests(String image) {
	setupDockerMarkLogic(image)

	sh label:'run marklogic-client-api tests', script: '''#!/bin/bash
			export JAVA_HOME=$JAVA_HOME_DIR
			export GRADLE_USER_HOME=$WORKSPACE/$GRADLE_DIR
			export PATH=$GRADLE_USER_HOME:$JAVA_HOME/bin:$PATH
			cd java-client-api
			mkdir -p marklogic-client-api/build/test-results/test
			./gradlew marklogic-client-api:test  || true
	'''

	sh label:'run ml-development-tools tests', script: '''#!/bin/bash
			export JAVA_HOME=$JAVA_HOME_DIR
			export GRADLE_USER_HOME=$WORKSPACE/$GRADLE_DIR
			export PATH=$GRADLE_USER_HOME:$JAVA_HOME/bin:$PATH
			cd java-client-api
			mkdir -p ml-development-tools/build/test-results/test
			./gradlew ml-development-tools:test || true
	'''

	sh label:'run fragile functional tests', script: '''#!/bin/bash
			export JAVA_HOME=$JAVA_HOME_DIR
			export GRADLE_USER_HOME=$WORKSPACE/$GRADLE_DIR
			export PATH=$GRADLE_USER_HOME:$JAVA_HOME/bin:$PATH
			cd java-client-api
			./gradlew mlDeploy -PmlForestDataDirectory=/space
			./gradlew marklogic-client-api-functionaltests:runFragileTests || true
	'''

	sh label:'run fast functional tests', script: '''#!/bin/bash
			export JAVA_HOME=$JAVA_HOME_DIR
			export GRADLE_USER_HOME=$WORKSPACE/$GRADLE_DIR
			export PATH=$GRADLE_USER_HOME:$JAVA_HOME/bin:$PATH
			cd java-client-api
			./gradlew marklogic-client-api-functionaltests:runFastFunctionalTests || true
	'''

	sh label:'run slow functional tests', script: '''#!/bin/bash
			export JAVA_HOME=$JAVA_HOME_DIR
			export GRADLE_USER_HOME=$WORKSPACE/$GRADLE_DIR
			export PATH=$GRADLE_USER_HOME:$JAVA_HOME/bin:$PATH
			cd java-client-api
			./gradlew marklogic-client-api-functionaltests:runSlowFunctionalTests || true
	'''

	postProcessTestResults()
}

def runTestsWithReverseProxy(String image) {
	setupDockerMarkLogic(image)

	sh label:'run fragile functional tests with reverse proxy', script: '''#!/bin/bash
			export JAVA_HOME=$JAVA_HOME_DIR
			export GRADLE_USER_HOME=$WORKSPACE/$GRADLE_DIR
			export PATH=$GRADLE_USER_HOME:$JAVA_HOME/bin:$PATH
			cd java-client-api
			./gradlew -PtestUseReverseProxyServer=true test-app:runReverseProxyServer marklogic-client-api-functionaltests:runFragileTests || true
	'''

	sh label:'run fast functional tests with reverse proxy', script: '''#!/bin/bash
			export JAVA_HOME=$JAVA_HOME_DIR
			export GRADLE_USER_HOME=$WORKSPACE/$GRADLE_DIR
			export PATH=$GRADLE_USER_HOME:$JAVA_HOME/bin:$PATH
			cd java-client-api
			./gradlew -PtestUseReverseProxyServer=true test-app:runReverseProxyServer marklogic-client-api-functionaltests:runFastFunctionalTests || true
	'''

	sh label:'run slow functional tests with reverse proxy', script: '''#!/bin/bash
			export JAVA_HOME=$JAVA_HOME_DIR
			export GRADLE_USER_HOME=$WORKSPACE/$GRADLE_DIR
			export PATH=$GRADLE_USER_HOME:$JAVA_HOME/bin:$PATH
			cd java-client-api
			./gradlew -PtestUseReverseProxyServer=true test-app:runReverseProxyServer marklogic-client-api-functionaltests:runSlowFunctionalTests || true
	'''

	postProcessTestResults()
}

def postProcessTestResults() {
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

def tearDownDocker() {
	sh label:'tearDownDocker', script: '''#!/bin/bash
		cd java-client-api/test-app
		docker compose down -v || true
		docker volume prune -f
	'''
}

pipeline{
  agent {label 'javaClientLinuxPool'}

  options {
    checkoutToSubdirectory 'java-client-api'
    buildDiscarder logRotator(artifactDaysToKeepStr: '7', artifactNumToKeepStr: '', daysToKeepStr: '7', numToKeepStr: '10')
  }

  parameters {
    booleanParam(name: 'regressions', defaultValue: false, description: 'indicator if build is for regressions')
    string(name: 'Email', defaultValue: '' ,description: 'Who should I say send the email to?')
    string(name: 'JAVA_VERSION', defaultValue: 'JAVA8' ,description: 'Who should I say send the email to?')
  }

  environment {
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
	      setupDockerMarkLogic("ml-docker-db-dev-tierpoint.bed-artifactory.bedford.progress.com/marklogic/marklogic-server-ubi:latest-12")
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
			post {
				always {
					tearDownDocker()
				}
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
				runTests("ml-docker-db-dev-tierpoint.bed-artifactory.bedford.progress.com/marklogic/marklogic-server-ubi:11.2.0-ubi")
				junit '**/build/**/TEST*.xml'
			}
			post {
				always {
					tearDownDocker()
				}
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
				runTests("ml-docker-db-dev-tierpoint.bed-artifactory.bedford.progress.com/marklogic/marklogic-server-ubi:latest-11")
				junit '**/build/**/TEST*.xml'
			}
			post {
				always {
					tearDownDocker()
				}
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
				runTestsWithReverseProxy("ml-docker-db-dev-tierpoint.bed-artifactory.bedford.progress.com/marklogic/marklogic-server-ubi:latest-11")
				junit '**/build/**/TEST*.xml'
			}
			post {
				always {
					tearDownDocker()
				}
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
				runTests("ml-docker-db-dev-tierpoint.bed-artifactory.bedford.progress.com/marklogic/marklogic-server-ubi:latest-12")
				junit '**/build/**/TEST*.xml'
			}
			post {
				always {
					tearDownDocker()
				}
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
				runTests("ml-docker-db-dev-tierpoint.bed-artifactory.bedford.progress.com/marklogic/marklogic-server-ubi:latest-10")
				junit '**/build/**/TEST*.xml'
			}
			post {
				always {
					tearDownDocker()
				}
			}
		}

  }
}
