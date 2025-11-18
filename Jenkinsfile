@Library('shared-libraries') _

def getJavaHomePath() {
	if (env.JAVA_VERSION == "JAVA21") {
		return "/home/builder/java/jdk-21.0.1"
	} else {
		return "/home/builder/java/jdk-17.0.2"
	}
}

def setupDockerMarkLogic(String image) {
	cleanupDocker()
	sh label: 'mlsetup', script: '''#!/bin/bash
	echo "Removing any running MarkLogic server and clean up MarkLogic data directory"
    sudo /usr/local/sbin/mladmin remove
    sudo /usr/local/sbin/mladmin cleandata
    cd java-client-api
    docker compose down -v || true
    docker volume prune -f
    echo "Using image: "''' + image + '''
    docker pull ''' + image + '''
    MARKLOGIC_IMAGE=''' + image + ''' MARKLOGIC_LOGS_VOLUME=marklogicLogs docker compose up -d --build
		export JAVA_HOME=$JAVA_HOME_DIR
		export GRADLE_USER_HOME=$WORKSPACE/$GRADLE_DIR
		export PATH=$JAVA_HOME/bin:$PATH
		./gradlew -i mlWaitTillReady
    sleep 3
    ./gradlew -i mlWaitTillReady
    ./gradlew mlTestConnections
   	./gradlew -i mlDeploy mlReloadSchemas
  '''
}

def runTests(String image) {
	setupDockerMarkLogic(image)

	sh label: 'run marklogic-client-api tests', script: '''#!/bin/bash
			export JAVA_HOME=$JAVA_HOME_DIR
			export GRADLE_USER_HOME=$WORKSPACE/$GRADLE_DIR
			export PATH=$GRADLE_USER_HOME:$JAVA_HOME/bin:$PATH
			cd java-client-api

			echo "Temporary fix for mysterious issue with okhttp3 being corrupted in local Maven cache."
			ls -la ~/.m2/repository/com/squareup
			rm -rf ~/.m2/repository/com/squareup/okhttp3/

			echo "Ensure all subprojects can be built first."
      ./gradlew clean build -x test

			./gradlew marklogic-client-api:test  || true
	'''

	sh label: 'run ml-development-tools tests', script: '''#!/bin/bash
			export JAVA_HOME=$JAVA_HOME_DIR
			export GRADLE_USER_HOME=$WORKSPACE/$GRADLE_DIR
			export PATH=$GRADLE_USER_HOME:$JAVA_HOME/bin:$PATH
			cd java-client-api
			./gradlew ml-development-tools:test || true
	'''

	sh label: 'run fragile functional tests', script: '''#!/bin/bash
			export JAVA_HOME=$JAVA_HOME_DIR
			export GRADLE_USER_HOME=$WORKSPACE/$GRADLE_DIR
			export PATH=$GRADLE_USER_HOME:$JAVA_HOME/bin:$PATH
			cd java-client-api
			./gradlew mlDeploy -PmlForestDataDirectory=/space
			./gradlew marklogic-client-api-functionaltests:runFragileTests || true
	'''

	sh label: 'run fast functional tests', script: '''#!/bin/bash
			export JAVA_HOME=$JAVA_HOME_DIR
			export GRADLE_USER_HOME=$WORKSPACE/$GRADLE_DIR
			export PATH=$GRADLE_USER_HOME:$JAVA_HOME/bin:$PATH
			cd java-client-api
			./gradlew marklogic-client-api-functionaltests:runFastFunctionalTests || true
	'''

	sh label: 'run slow functional tests', script: '''#!/bin/bash
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

	sh label: 'run marklogic-client-api tests with reverse proxy', script: '''#!/bin/bash
			export JAVA_HOME=$JAVA_HOME_DIR
			export GRADLE_USER_HOME=$WORKSPACE/$GRADLE_DIR
			export PATH=$GRADLE_USER_HOME:$JAVA_HOME/bin:$PATH
			cd java-client-api

			echo "Temporary fix for mysterious issue with okhttp3 being corrupted in local Maven cache."
			ls -la ~/.m2/repository/com/squareup
			rm -rf ~/.m2/repository/com/squareup/okhttp3/

			echo "Ensure all subprojects can be built first."
      ./gradlew clean build -x test

      echo "Running marklogic-client-api tests with reverse proxy."
			./gradlew -PtestUseReverseProxyServer=true runReverseProxyServer marklogic-client-api:test || true
	'''

	sh label: 'run fragile functional tests with reverse proxy', script: '''#!/bin/bash
			export JAVA_HOME=$JAVA_HOME_DIR
			export GRADLE_USER_HOME=$WORKSPACE/$GRADLE_DIR
			export PATH=$GRADLE_USER_HOME:$JAVA_HOME/bin:$PATH
			cd java-client-api
			./gradlew -PtestUseReverseProxyServer=true runReverseProxyServer marklogic-client-api-functionaltests:runFragileTests || true
	'''

	sh label: 'run fast functional tests with reverse proxy', script: '''#!/bin/bash
			export JAVA_HOME=$JAVA_HOME_DIR
			export GRADLE_USER_HOME=$WORKSPACE/$GRADLE_DIR
			export PATH=$GRADLE_USER_HOME:$JAVA_HOME/bin:$PATH
			cd java-client-api
			./gradlew -PtestUseReverseProxyServer=true runReverseProxyServer marklogic-client-api-functionaltests:runFastFunctionalTests || true
	'''

	sh label: 'run slow functional tests with reverse proxy', script: '''#!/bin/bash
			export JAVA_HOME=$JAVA_HOME_DIR
			export GRADLE_USER_HOME=$WORKSPACE/$GRADLE_DIR
			export PATH=$GRADLE_USER_HOME:$JAVA_HOME/bin:$PATH
			cd java-client-api
			./gradlew -PtestUseReverseProxyServer=true runReverseProxyServer marklogic-client-api-functionaltests:runSlowFunctionalTests || true
	'''

	postProcessTestResults()
}

def postProcessTestResults() {
	sh label: 'post-test-process', script: '''
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
	sh label: 'tearDownDocker', script: '''#!/bin/bash
		cd java-client-api
		docker compose down -v || true
		docker volume prune -f
	'''
	cleanupDocker()
}

pipeline {
	agent { label 'javaClientLinuxPool' }

	options {
		checkoutToSubdirectory 'java-client-api'
		buildDiscarder logRotator(artifactDaysToKeepStr: '7', artifactNumToKeepStr: '', daysToKeepStr: '7', numToKeepStr: '10')
	}

	parameters {
		booleanParam(name: 'regressions', defaultValue: false, description: 'indicator if build is for regressions')
		string(name: 'JAVA_VERSION', defaultValue: 'JAVA17', description: 'Either JAVA17 or JAVA21')
		string(name: 'MARKLOGIC_IMAGE_TAGS', defaultValue: 'marklogic-server-ubi:latest-11,marklogic-server-ubi:latest-12', description: 'Comma-delimited list of MarkLogic image tags including variant (e.g., marklogic-server-ubi:latest-11,marklogic-server-ubi-rootless:11.3.2). The registry/org (ml-docker-db-dev-tierpoint.bed-artifactory.bedford.progress.com/marklogic) path will be prepended automatically.')
	}

	environment {
		JAVA_HOME_DIR = getJavaHomePath()
		GRADLE_DIR = ".gradle"
		DMC_USER = credentials('MLBUILD_USER')
		DMC_PASSWORD = credentials('MLBUILD_PASSWORD')
	}

	stages {

		stage('pull-request-tests') {
			when {
				not {
					expression { return params.regressions }
				}
			}
			steps {
				setupDockerMarkLogic("ml-docker-db-dev-tierpoint.bed-artifactory.bedford.progress.com/marklogic/marklogic-server-ubi:latest-12")
				sh label: 'run marklogic-client-api tests', script: '''#!/bin/bash
          export JAVA_HOME=$JAVA_HOME_DIR
          export GRADLE_USER_HOME=$WORKSPACE/$GRADLE_DIR
          export PATH=$GRADLE_USER_HOME:$JAVA_HOME/bin:$PATH
          cd java-client-api

          echo "Temporary fix for mysterious issue with okhttp3 being corrupted in local Maven cache."
          ls -la ~/.m2/repository/com/squareup
					rm -rf ~/.m2/repository/com/squareup/okhttp3/

          echo "Ensure all subprojects can be built first."
          ./gradlew clean build -x test

          echo "Run a sufficient number of tests to verify the PR."
					./gradlew marklogic-client-api:test --tests ReadDocumentPageTest || true

					echo "Run a test with the reverse proxy server to ensure it's fine."
					./gradlew -PtestUseReverseProxyServer=true runReverseProxyServer marklogic-client-api-functionaltests:test --tests SearchWithPageLengthTest || true
        '''
			}
			post {
				always {
					junit '**/build/**/TEST*.xml'
					updateWorkspacePermissions()
					tearDownDocker()
				}
			}
		}

		stage('publish') {
			when {
				branch 'develop'
				not {
					expression { return params.regressions }
				}
			}
			steps {
				sh label: 'publish', script: '''#!/bin/bash
          export JAVA_HOME=$JAVA_HOME_DIR
          export GRADLE_USER_HOME=$WORKSPACE/$GRADLE_DIR
          export PATH=$GRADLE_USER_HOME:$JAVA_HOME/bin:$PATH
          cp ~/.gradle/gradle.properties $GRADLE_USER_HOME;
          cd java-client-api
          ./gradlew publish
        '''
			}
		}

		stage('regressions') {
			when {
				allOf {
					branch 'develop'
					expression { return params.regressions }
				}
			}

			steps {
				script {
					def imageTags = params.MARKLOGIC_IMAGE_TAGS.split(',')
					def imagePrefix = 'ml-docker-db-dev-tierpoint.bed-artifactory.bedford.progress.com/marklogic/'

					imageTags.each { tag ->
						def fullImage = imagePrefix + tag.trim()
						def stageName = "regressions-${tag.trim().replace(':', '-')}"

						stage(stageName) {
							try {
								runTests(fullImage)
							} finally {
								junit '**/build/**/TEST*.xml'
								updateWorkspacePermissions()
								tearDownDocker()
							}
						}
					}
				}
			}
		}
	}
}
