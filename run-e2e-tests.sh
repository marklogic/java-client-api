#!/usr/bin/env bash
# ==============================================================================
# Turnkey MarkLogic E2E Automated Test Runner
# Repository: marklogic/java-client-api
# Branch: rsingh-testing
# ==============================================================================
set -euo pipefail

SCRIPT_DIR="$(cd "$(dirname "${BASH_SOURCE[0]}")" && pwd)"
cd "${SCRIPT_DIR}"

RED='\033[0;31m'
GREEN='\033[0;32m'
YELLOW='\033[1;33m'
BLUE='\033[0;34m'
CYAN='\033[0;36m'
BOLD='\033[1m'
NC='\033[0m' # No Color

MODE="e2e"
TARGET_CLASS=""
FORCE_DEPLOY=false
STOP_CONTAINER=false

usage() {
    cat << EOF
${BOLD}MarkLogic Java Client E2E Test Runner${NC}

Usage: ./run-e2e-tests.sh [OPTIONS]

Options:
  --e2e                (Default) Run the comprehensive E2E test suite
  --all                Run both E2E test suite AND full existing test suite
  --regression         Run only existing unit and functional tests
  --class <ClassName>  Run a specific E2E test class (e.g. DocumentOperationsE2ETest)
  --deploy             Force redeployment of test application, schemas, and data
  --down               Stop and tear down the MarkLogic container
  -h, --help           Show this help message

Test Suites Included in E2E:
  - DocumentOperationsE2ETest      (JSON, XML, Text, Binary, Metadata, Patch, OCC, Bulk)
  - SearchAndQueryE2ETest          (Structured, String, Delete-by-search, Page streaming)
  - OpticAndRowManagerE2ETest      (Literals, Joins, Aggregations, Views, JSON output)
  - TransactionsE2ETest            (Multi-statement ACID, Read-your-own-writes, Rollback)
  - ServerEvalE2ETest              (JavaScript, XQuery, Variable Bindings, JacksonHandle)
  - DataMovementE2ETest            (WriteBatcher, QueryBatcher, Multithreading, JobReport)
  - ConnectionAndSecurityE2ETest   (Builder, Direct/Gateway modes, RBAC security)
  - BitemporalE2ETest              (Bitemporal docs, System timestamping, Temporal delete)

Examples:
  ./run-e2e-tests.sh
  ./run-e2e-tests.sh --class DocumentOperationsE2ETest
  ./run-e2e-tests.sh --all
  ./run-e2e-tests.sh --deploy
  ./run-e2e-tests.sh --down
EOF
    exit 0
}

# Parse CLI options
while [[ $# -gt 0 ]]; do
    case "$1" in
        --e2e)
            MODE="e2e"
            shift
            ;;
        --all)
            MODE="all"
            shift
            ;;
        --regression)
            MODE="regression"
            shift
            ;;
        --class)
            MODE="single"
            TARGET_CLASS="$2"
            shift 2
            ;;
        --deploy)
            FORCE_DEPLOY=true
            shift
            ;;
        --down)
            STOP_CONTAINER=true
            shift
            ;;
        -h|--help)
            usage
            ;;
        *)
            echo -e "${RED}Unknown option: $1${NC}"
            usage
            ;;
    esac
done

if [ "${STOP_CONTAINER}" = true ]; then
    echo -e "${YELLOW}Stopping MarkLogic test container...${NC}"
    docker compose down
    echo -e "${GREEN}MarkLogic container stopped.${NC}"
    exit 0
fi

echo -e "${CYAN}${BOLD}======================================================${NC}"
echo -e "${CYAN}${BOLD}  MarkLogic Java Client E2E Automated Verification    ${NC}"
echo -e "${CYAN}${BOLD}======================================================${NC}"

# 1. Verify Docker daemon is running
echo -e "\n${BLUE}[1/4] Checking Docker daemon status...${NC}"
if ! docker info >/dev/null 2>&1; then
    echo -e "${RED}Error: Docker daemon is not running or socket is unreachable.${NC}"
    echo -e "Please ensure Docker / Rancher Desktop is running."
    exit 1
fi
echo -e "${GREEN}Docker daemon is responsive.${NC}"

# 2. Check or launch MarkLogic container
echo -e "\n${BLUE}[2/4] Verifying MarkLogic test container...${NC}"
CONTAINER_RUNNING=$(docker ps --filter "name=marklogic" --filter "status=running" -q | head -n 1 || true)

if [ -z "${CONTAINER_RUNNING}" ]; then
    echo -e "${YELLOW}MarkLogic container not running. Starting via docker compose...${NC}"
    docker compose up -d
    echo -e "${YELLOW}Waiting for MarkLogic to become ready (this may take up to a minute)...${NC}"
    ./gradlew mlWaitTillReady
else
    echo -e "${GREEN}MarkLogic container is running (ID: ${CONTAINER_RUNNING:0:12}).${NC}"
fi

# 3. Check App Server & Database Deployment
echo -e "\n${BLUE}[3/4] Checking test application deployment on port 8012...${NC}"
REST_STATUS=$(curl -s -o /dev/null -w "%{http_code}" --digest -u admin:admin "http://localhost:8012/v1/resources" 2>/dev/null || echo "000")

if [ "${REST_STATUS}" = "000" ] || [ "${REST_STATUS}" = "404" -a "${REST_STATUS}" = "000" ] || [ "${FORCE_DEPLOY}" = true ]; then
    echo -e "${YELLOW}Deploying test application databases, REST server, security roles, modules, and schemas...${NC}"
    ./gradlew :test-app:mlDeploySecurity
    ./gradlew :test-app:mlDeployDatabases
    ./gradlew :test-app:mlDeployRestApis
    ./gradlew :test-app:mlLoadModules :test-app:mlReloadSchemas
    ./gradlew :test-app:mlDeployTemporal
    ./gradlew :test-app:mlLoadData
    echo -e "${GREEN}Test application deployment complete.${NC}"
else
    echo -e "${GREEN}Test application REST server is active on port 8012.${NC}"
fi

# 4. Execute Tests
echo -e "\n${BLUE}[4/4] Executing tests (Mode: ${MODE})...${NC}"
START_TIME=$(date +%s)

case "${MODE}" in
    "e2e")
        echo -e "${CYAN}Running all 8 Comprehensive E2E Test Suites...${NC}"
        ./gradlew e2eTest
        ;;
    "single")
        echo -e "${CYAN}Running single test class: ${TARGET_CLASS}...${NC}"
        ./gradlew :marklogic-client-api:test --tests "*${TARGET_CLASS}*"
        ;;
    "regression")
        echo -e "${CYAN}Running existing client test suite...${NC}"
        ./gradlew :marklogic-client-api:test
        ;;
    "all")
        echo -e "${CYAN}Running both E2E test suite and existing test suite...${NC}"
        ./gradlew e2eTest :marklogic-client-api:test
        ;;
esac

END_TIME=$(date +%s)
DURATION=$((END_TIME - START_TIME))

echo -e "\n${GREEN}${BOLD}======================================================${NC}"
echo -e "${GREEN}${BOLD}  ALL TESTS PASSED SUCCESSFULLY! (${DURATION}s)              ${NC}"
echo -e "${GREEN}${BOLD}======================================================${NC}"
if [ -f "${SCRIPT_DIR}/marklogic-client-api/build/reports/tests/e2eTest/index.html" ]; then
    echo -e "E2E Report: file://${SCRIPT_DIR}/marklogic-client-api/build/reports/tests/e2eTest/index.html"
fi
if [ -f "${SCRIPT_DIR}/marklogic-client-api/build/reports/tests/test/index.html" ]; then
    echo -e "Test Report: file://${SCRIPT_DIR}/marklogic-client-api/build/reports/tests/test/index.html"
fi
echo ""

