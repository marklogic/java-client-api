@Library('shared-libraries@arminstances_aws_sharedlibraries') _

def getJavaHomePath() {
    if (params.arm_regressions) {
        def version = (env.JAVA_VERSION == "JAVA21") ? "21" : "17"
        def path = "/usr/lib/jvm/java-${version}-amazon-corretto.aarch64"
        return path
    } else {
        if (env.JAVA_VERSION == "JAVA21") {
            return "/home/builder/java/jdk-21.0.1"
        } else {
            return "/home/builder/java/jdk-17.0.2"
        }
    }
}

def getPlatform() {
	return params.arm_regressions ? "linux/arm64" : "linux/amd64"
}

def setConverters() {
	return params.arm_regressions ? "false" :"true"
}

def setupDockerMarkLogic(String image) {
    cleanupDocker()
    sh label: 'mlsetup', script: '''#!/bin/bash
        echo "Removing any running MarkLogic server and clean up MarkLogic data directory"
        sudo /usr/local/sbin/mladmin remove
        sudo /usr/local/sbin/mladmin cleandata
        cd java-client-api
		export PLATFORM=$PLATFORM
		export SET_CONVERTERS=$SET_CONVERTERS
        docker compose down -v || true
        docker volume prune -f

        echo "Using image: "''' + image + '''
        docker pull ''' + image + '''

        MARKLOGIC_IMAGE=''' + image + ''' MARKLOGIC_LOGS_VOLUME=marklogicLogs \
        docker compose up -d --build
		echo "Waiting for MarkLogic server to initialize."
        sleep 300
        export JAVA_HOME=$JAVA_HOME_DIR
        export GRADLE_USER_HOME=$WORKSPACE/$GRADLE_DIR
        export PATH=$GRADLE_USER_HOME:$JAVA_HOME/bin:$PATH
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
		booleanParam(name: 'arm_regressions', defaultValue: true, description: 'indicator if build is for ARM regressions')
		string(name: 'JAVA_VERSION', defaultValue: 'JAVA17', description: 'Either JAVA17 or JAVA21')
		string(name: 'packagefile', defaultValue: 'Packagedependencies', description: 'package dependency file')
		string(name: 'terraformBranch', defaultValue: 'create-packagedependencies-javaclientapi', description: 'Branch of terraform-templates repo to use')


	}

	environment {
		JAVA_HOME_DIR = getJavaHomePath()
		GRADLE_DIR = ".gradle"
		DMC_USER = credentials('MLBUILD_USER')
		DMC_PASSWORD = credentials('MLBUILD_PASSWORD')
		PLATFORM = getPlatform()
		SET_CONVERTERS = setConverters()
	}

	stages {

		stage('pull-request-tests') {
			when {
				expression { 
					return !params.regressions && !params.arm_regressions
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
					anyOf {
						expression { return params.regressions }
						expression { return params.arm_regressions }
					}
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

		stage('regressions-11') {
			when {
				allOf {
					branch 'develop'
					expression { return params.regressions }          
					expression { return !params.arm_regressions }     
				}
			}
			steps {
				runTests("ml-docker-db-dev-tierpoint.bed-artifactory.bedford.progress.com/marklogic/marklogic-server-ubi:latest-11")
			}
			post {
				always {
					junit '**/build/**/TEST*.xml'
					updateWorkspacePermissions()
					tearDownDocker()
				}
			}
		}

		// Latest run had 87 errors, which have been added to MLE-24523 for later research.
//		stage('regressions-12-reverseProxy') {
//			when {
//				allOf {
//					branch 'develop'
//					expression {return params.regressions}
//				}
//			}
//			steps {
//				runTestsWithReverseProxy("ml-docker-db-dev-tierpoint.bed-artifactory.bedford.progress.com/marklogic/marklogic-server-ubi:latest-12")
//			}
//			post {
//				always {
//					junit '**/build/**/TEST*.xml'
//					updateWorkspacePermissions()
//					tearDownDocker()
//				}
//			}
//		}

		stage('regressions-12') {
			when {
				allOf {
					branch 'develop'
					expression { return params.regressions }          
					expression { return !params.arm_regressions }     
				}
			}
			steps {
				runTests("ml-docker-db-dev-tierpoint.bed-artifactory.bedford.progress.com/marklogic/marklogic-server-ubi:latest-12")
			}
			post {
				always {
					junit '**/build/**/TEST*.xml'
					updateWorkspacePermissions()
					tearDownDocker()
				}
			}
		}

		stage('provisionInfrastructure'){
			when {
				allOf {
					//branch 'develop'
					branch 'arm-regressions-testbranch'
					expression { return params.arm_regressions }
					expression { return !params.regressions }          
				}
			}
            agent {label 'javaClientLinuxPool'}
			
            steps{
                script {
					def nodeName = "java-client-agent-${BUILD_NUMBER}"
                    def remoteFS = "/space/jenkins_home"
                    def labels = "java-client-agent-${BUILD_NUMBER}"
                    def instanceIp = env.EC2_PRIVATE_IP

                    def deploymentResult = deployAWSInstance([
                        instanceName: "java-client-instance-${BUILD_NUMBER}",
                        region: 'us-west-2',
                        credentialsId: 'headlessDbUserEC2',
                        role: 'role-headless-testing',
                        roleAccount: '343869654284',
						branch: params.terraformBranch 
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

					// Use shared library for volume attachment
                    def volumeResult = attachInstanceVolumes([
                        instanceIp: instanceIp,
                        remoteFS: remoteFS,
                        packageFile: params.packagefile,
                        setupScriptPath: 'terraform-templates/arm-server-build/setup_volume.sh',
                        packageDir: 'terraform-templates/java-client-api',
                        initScriptsDir: 'terraform-templates/java-client-api/scripts',
						initScriptsFile: 'terraform-templates/java-client-api/initscripts',
                        branch: params.terraformBranch

                    ])
                    
                    echo "✅ Volume attachment completed: ${volumeResult.volumeAttached}"
                    echo "✅ Java installed: ${volumeResult.javaInstalled}"
                    echo "✅ Dependencies installed: ${volumeResult.dependenciesInstalled}"

					// Use shared library to create Jenkins agent
                    def agentResult = createJenkinsAgent([
                        nodeName: nodeName,
                        instanceIp: instanceIp,
                        remoteFS: remoteFS,
                        labels: labels,
                        timeoutMinutes: 5,
						credentialsId: 'qa-builder-aws'
                    ])
                    
                    echo "✅ Jenkins agent created: ${agentResult.nodeName}"
                    echo "✅ Agent status: ${agentResult.status}"
                }
            }
        }

		stage('regressions-11 arm infrastructure') {
    		agent { label "java-client-agent-${BUILD_NUMBER}" }
			when {
				allOf {
					//branch 'develop'
					branch 'arm-regressions-testbranch'
					expression { return params.arm_regressions }
					expression { return !params.regressions }          
				}
			}
			steps {
				checkout([$class: 'GitSCM', 
						branches: scm.branches, 
						doGenerateSubmoduleConfigurations: false, 
						extensions: [[$class: 'RelativeTargetDirectory', relativeTargetDir: 'java-client-api']], 
						submoduleCfg: [], 
						userRemoteConfigs: scm.userRemoteConfigs])
				
				runTests("ml-docker-db-dev-tierpoint.bed-artifactory.bedford.progress.com/marklogic/marklogic-server-ubi9-arm:latest-11")
			}
			post {
				always {
					archiveArtifacts artifacts: 'java-client-api/**/build/reports/**/*.html'
					junit '**/build/**/TEST*.xml'
					updateWorkspacePermissions()
					tearDownDocker()
				}
			}
		}

	}

	post{
        always {
            script {
                echo "🧹 Starting cleanup process..."
                
                try {
                    // Cleanup Terraform infrastructure
                    if (env.EC2_PRIVATE_IP) {
                        echo "🗑️ Cleaning up Terraform resources..."
                        node('javaClientLinuxPool') {
                            try {
                                sleep 60
                                unstash "terraform-${BUILD_NUMBER}"
                                withAWS(credentials: 'headlessDbUserEC2', region: 'us-west-2', role: 'role-headless-testing', roleAccount: '343869654284', duration: 3600) {
                                    sh '''#!/bin/bash
                                        export PATH=/home/builder/terraform:$PATH
                                        cd ${WORKSPACE}/${DEPLOYMENT_TERRAFORM_DIR}
                                        terraform workspace select dev
                                        terraform destroy -auto-approve
                                    '''
                                }
                                echo "✅ Terraform resources destroyed successfully."
                            } catch (Exception terraformException) {
                                echo "⚠️ Warning: Terraform cleanup failed: ${terraformException.message}"
                            }
                        }
                    } else {
                        echo "ℹ️ No EC2 instance IP found, skipping Terraform cleanup"
                    }
                    
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
                    
                } catch (Exception cleanupException) {
                    echo "⚠️ Warning: Cleanup encountered an error: ${cleanupException.message}"
                    echo "📋 Continuing with pipeline completion despite cleanup issues..."
                }
            }
        }
    }
}