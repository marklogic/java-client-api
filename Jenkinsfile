@Library('shared-libraries') _

def getJavaHomePath() {
	if (env.JAVA_VERSION == "JAVA21") {
		return "/home/builder/java/jdk-21.0.1"
	} else {
		return "/home/builder/java/jdk-17.0.2"
	}
}

def getJavaHomePathForARM() {
	def version = (env.JAVA_VERSION == "JAVA21") ? "21" : "17"
	return "/usr/lib/jvm/java-${version}-amazon-corretto.aarch64"
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
		PLATFORM = "linux/amd64"
		MARKLOGIC_INSTALL_CONVERTERS = "true"
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
					./gradlew marklogic-client-api:test

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
			post {
				always {
					sh label: 'generate-javadoc', script: '''#!/bin/bash
						export JAVA_HOME=$JAVA_HOME_DIR
						export GRADLE_USER_HOME=$WORKSPACE/$GRADLE_DIR
						export PATH=$GRADLE_USER_HOME:$JAVA_HOME/bin:$PATH
						cd java-client-api
						./gradlew javadoc
						echo "Zipping javadocs for easy download..."
						cd marklogic-client-api/build/docs
						zip -r javadoc.zip javadoc/
						mv javadoc.zip $WORKSPACE/
	        '''
					archiveArtifacts artifacts: 'javadoc.zip', fingerprint: true
				}
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

		stage('provisionInfrastructure') {
			when {
				branch 'develop'
				expression { return params.regressions }
			}
			agent { label 'javaClientLinuxPool' }

			steps {
				script {
					withCredentials([
						string(credentialsId: 'aws-region-us-west', variable: 'AWS_REGION'),
						string(credentialsId: 'aws-role-headless-testing', variable: 'AWS_ROLE'),
						string(credentialsId: 'aws-role-account-headless', variable: 'AWS_ROLE_ACCOUNT')
					]) {
						def deploymentResult = deployAWSInstance([
							instanceName : "java-client-instance-${BUILD_NUMBER}",
							region       : env.AWS_REGION,
							credentialsId: 'headlessDbUserEC2',
							role         : env.AWS_ROLE,
							roleAccount  : env.AWS_ROLE_ACCOUNT,
							branch       : 'master'
						])

						echo "✅ Instance deployed: ${deploymentResult.privateIp}"
						echo "✅ Terraform directory: ${deploymentResult.terraformDir}"
						echo "✅ Workspace: ${deploymentResult.workspace}"
						echo "✅ Status: ${deploymentResult.status}"

						// Store deployment info for cleanup
						env.DEPLOYMENT_INSTANCE_NAME = deploymentResult.instanceName
						env.DEPLOYMENT_REGION = deploymentResult.region
						env.DEPLOYMENT_TERRAFORM_DIR = deploymentResult.terraformDir
						env.EC2_PRIVATE_IP = deploymentResult.privateIp

						def nodeName = "java-client-agent-${BUILD_NUMBER}"
						def remoteFS = "/space/jenkins_home"
						def labels = "java-client-agent-${BUILD_NUMBER}"
						def instanceIp = env.EC2_PRIVATE_IP

						// Attach volumes
						def volumeResult = attachInstanceVolumes([
							instanceIp: instanceIp,
							remoteFS  : remoteFS,
							branch    : 'master'
						])

						echo "✅ Volume attachment completed: ${volumeResult.volumeAttached}"
						echo "✅ Java installed: ${volumeResult.javaInstalled}"

						//Install dependencies AND run init scripts
						def depsResult = installDependenciesAndInitScripts([
							instanceIp     : instanceIp,
							packageFile    : 'Packagedependencies',
							packageDir     : 'terraform-templates/java-client-api',
							initScriptsDir : 'terraform-templates/java-client-api/scripts',
							initScriptsFile: 'terraform-templates/java-client-api/initscripts'
						])

						echo "✅ Dependencies installed: ${depsResult.dependenciesInstalled}"
						if (depsResult.initScriptsExecuted) {
							echo "✅ Init scripts executed: ${depsResult.initScriptsCount} scripts"
						} else {
							echo "ℹ️ No init scripts configured or executed"
						}

						// Use shared library to create Jenkins agent
						def agentResult = createJenkinsAgent([
							nodeName      : nodeName,
							instanceIp    : instanceIp,
							remoteFS      : remoteFS,
							labels        : labels,
							timeoutMinutes: 5,
							credentialsId : 'qa-builder-aws'
						])

						echo "✅ Jenkins agent created: ${agentResult.nodeName}"
						echo "✅ Agent status: ${agentResult.status}"
					}
				}
			}
		}

		stage('regressions-arm infrastructure') {
			when {
				beforeAgent true
				branch 'develop'
				expression { return params.regressions }
				expression { return env.EC2_PRIVATE_IP != null }
			}
			agent { label "java-client-agent-${BUILD_NUMBER}" }
			environment {
				JAVA_HOME_DIR = getJavaHomePathForARM()
				PLATFORM = "linux/arm64"
				MARKLOGIC_INSTALL_CONVERTERS = "false"
			}
			steps {
				checkout([$class                           : 'GitSCM',
									branches                         : scm.branches,
									doGenerateSubmoduleConfigurations: false,
									extensions                       : [[$class: 'RelativeTargetDirectory', relativeTargetDir: 'java-client-api']],
									submoduleCfg                     : [],
									userRemoteConfigs                : scm.userRemoteConfigs])

				script {
					def imagePrefix = 'ml-docker-db-dev-tierpoint.bed-artifactory.bedford.progress.com/marklogic/marklogic-server-ubi9-arm:'

					['latest-11', 'latest-12'].each { tag ->
						def fullImage = imagePrefix + tag
						stage("regressions-arm-${tag}") {
							try {
								runTests(fullImage)
							} finally {
								archiveArtifacts artifacts: 'java-client-api/**/build/reports/**/*.html'
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

	post {
		always {
			script {
				echo "🧹 Starting cleanup process..."

				try {
					// Cleanup Terraform infrastructure
					if (env.EC2_PRIVATE_IP) {
						echo "🗑️ Cleaning up Terraform resources..."
						node('javaClientLinuxPool') {
							try {
								//`sleep 60` allows AWS resources to stabilize before Terraform destroys them, preventing "resource in use" errors
								sleep 60
								unstash "terraform-${BUILD_NUMBER}"
								withCredentials([
									string(credentialsId: 'aws-region-us-west', variable: 'AWS_REGION'),
									string(credentialsId: 'aws-role-headless-testing', variable: 'AWS_ROLE'),
									string(credentialsId: 'aws-role-account-headless', variable: 'AWS_ROLE_ACCOUNT')
								]) {
									withAWS(credentials: 'headlessDbUserEC2', region: env.AWS_REGION, role: env.AWS_ROLE, roleAccount: env.AWS_ROLE_ACCOUNT, duration: 3600) {
										sh '''#!/bin/bash
											export PATH=/home/builder/terraform:$PATH
											cd ${WORKSPACE}/${DEPLOYMENT_TERRAFORM_DIR}
											terraform workspace select dev
											terraform destroy -auto-approve
										'''
									}
								}
								echo "✅ Terraform resources destroyed successfully."
								// Cleanup Jenkins agent using shared library function
								def nodeName = "java-client-agent-${BUILD_NUMBER}"
								echo "🗑️ Cleaning up Jenkins agent: ${nodeName}"
								try {
									def cleanupResult = cleanupJenkinsAgent(nodeName)
									echo "✅ Cleanup result: ${cleanupResult.status} for node: ${cleanupResult.nodeName}"
								} catch (Exception jenkinsCleanupException) {
									echo "⚠️ Warning: Jenkins agent cleanup failed: ${jenkinsCleanupException.message}"
								}
								echo "✅ Pipeline cleanup completed successfully."
							} catch (Exception terraformException) {
								echo "⚠️ Warning: Terraform cleanup failed: ${terraformException.message}"
							}
						}
					} else {
						echo "ℹ️ No EC2 instance IP found, skipping Terraform cleanup"
					}
				} catch (Exception cleanupException) {
					echo "⚠️ Warning: Cleanup encountered an error: ${cleanupException.message}"
					echo "📋 Continuing with pipeline completion despite cleanup issues..."
				}
			}
		}
	}
}

