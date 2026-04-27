#!/bin/bash

# Évora All-in-One Initializer
# Handles environment dependencies, dynamic port resolution, and framework patching.

echo "--- Initializing Évora Environment ---"

# --- Phase 0: System Dependencies ---

if [[ "$1" == "--clean" ]]; then
    echo "--- Performing Deep Clean ---"
    docker-compose -p evora down --remove-orphans
    docker rm -f evora-activemq evora-axis2 evora-cxf evora-odl 2>/dev/null
    echo "Clean complete."
    [ "$1" == "--clean" ] && exit 0
fi

if ! command -v mvn &> /dev/null; then
    echo "Maven (mvn) not found. Attempting to install..."
    sudo apt-get update && sudo apt-get install -y maven || { 
        echo "ERROR: Maven installation failed. Please run 'sudo apt install maven' manually."; 
        exit 1; 
    }
fi

# Check for Messaging4Transport dependency
M4T_REPO="$HOME/.m2/repository/org/opendaylight/messaging4transport/messaging4transport-impl"
if [ ! -d "$M4T_REPO" ]; then
    echo "--- Messaging4Transport dependency not found. Initiating auto-install... ---"
    if [ -f "./install_m4t.sh" ]; then
        chmod +x ./install_m4t.sh
        ./install_m4t.sh || { echo "ERROR: Failed to install Messaging4Transport."; exit 1; }
    else
        echo "ERROR: install_m4t.sh not found. Cannot proceed."
        exit 1
    fi
fi

# --- Phase 1: Dynamic Port Resolution ---

is_port_free() {
    ! nc -z localhost $1 &> /dev/null
}

is_port_assigned_locally() {
    local port=$1
    for p in $ASSIGNED_PORTS; do
        if [ "$p" == "$port" ]; then return 0; fi
    done
    return 1
}

is_port_available() {
    local port=$1
    if is_port_assigned_locally $port; then return 1; fi
    is_port_free $port
}

find_next_available_port() {
    local port=$1
    while ! is_port_available $port; do port=$((port + 1)); done
    echo $port
}

get_docker_image_on_port() {
    local port=$1
    docker ps --format '{{.Image}}' --filter "publish=$port" | head -n 1
}

resolve_service_port() {
    local target_port=$1
    local keyword=$2
    local resolved_val=""
    if is_port_available $target_port; then
        resolved_val=$target_port
        ASSIGNED_PORTS="$ASSIGNED_PORTS $resolved_val"
    else
        local existing_img=$(get_docker_image_on_port $target_port)
        if [ -n "$existing_img" ] && [[ "$existing_img" == *"$keyword"* ]]; then
            resolved_val="MATCH:$target_port"
        else
            resolved_val=$(find_next_available_port $((target_port + 1)))
            ASSIGNED_PORTS="$ASSIGNED_PORTS $resolved_val"
        fi
    fi
    echo "$resolved_val"
}

ASSIGNED_PORTS=""

# Resolve ActiveMQ
RES_AMQP=$(resolve_service_port 61616 "activemq")
ASSIGNED_PORTS="$ASSIGNED_PORTS $(echo $RES_AMQP | grep -oE '[0-9]+')"
[[ "$RES_AMQP" == MATCH:* ]] && { ACTIVEMQ_PORT=${RES_AMQP#MATCH:}; REUSE_AMQP=true; } || { ACTIVEMQ_PORT=$RES_AMQP; REUSE_AMQP=false; }

# Resolve ActiveMQ UI
RES_UI=$(resolve_service_port 8161 "activemq")
ASSIGNED_PORTS="$ASSIGNED_PORTS $(echo $RES_UI | grep -oE '[0-9]+')"
ACTIVEMQ_UI_PORT=${RES_UI#MATCH:*} 

# Resolve Axis2
RES_AXIS2=$(resolve_service_port 8080 "axis2")
ASSIGNED_PORTS="$ASSIGNED_PORTS $(echo $RES_AXIS2 | grep -oE '[0-9]+')"
[[ "$RES_AXIS2" == MATCH:* ]] && { AXIS2_PORT=${RES_AXIS2#MATCH:}; REUSE_AXIS2=true; } || { AXIS2_PORT=$RES_AXIS2; REUSE_AXIS2=false; }

# Resolve CXF
RES_CXF=$(resolve_service_port 8081 "tomcat")
ASSIGNED_PORTS="$ASSIGNED_PORTS $(echo $RES_CXF | grep -oE '[0-9]+')"
[[ "$RES_CXF" == MATCH:* ]] && { CXF_PORT=${RES_CXF#MATCH:}; REUSE_CXF=true; } || { CXF_PORT=$RES_CXF; REUSE_CXF=false; }

# Resolve OpenDaylight
RES_ODL_OF=$(resolve_service_port 6633 "opendaylight")
ASSIGNED_PORTS="$ASSIGNED_PORTS $(echo $RES_ODL_OF | grep -oE '[0-9]+')"
[[ "$RES_ODL_OF" == MATCH:* ]] && { ODL_OF_PORT=${RES_ODL_OF#MATCH:}; REUSE_ODL=true; } || { ODL_OF_PORT=$RES_ODL_OF; REUSE_ODL=false; }

RES_ODL_REST=$(resolve_service_port 8181 "opendaylight")
ASSIGNED_PORTS="$ASSIGNED_PORTS $(echo $RES_ODL_REST | grep -oE '[0-9]+')"
ODL_REST_PORT=${RES_ODL_REST#MATCH:*}

export ACTIVEMQ_PORT ACTIVEMQ_UI_PORT AXIS2_PORT CXF_PORT ODL_OF_PORT ODL_REST_PORT

echo "Active Environment Ports:"
echo " - AMQP: $ACTIVEMQ_PORT, Axis2: $AXIS2_PORT, CXF: $CXF_PORT"
echo " - ODL:  $ODL_OF_PORT (OF), $ODL_REST_PORT (REST)"

# --- Phase 2: Start Infrastructure (Docker) ---

COMPOSE_PROJECT="evora"
SERVICES=""
[ "$REUSE_AMQP" != true ] && SERVICES="$SERVICES activemq"
[ "$REUSE_AXIS2" != true ] && SERVICES="$SERVICES axis2"
[ "$REUSE_CXF" != true ] && SERVICES="$SERVICES cxf-host"
[ "$REUSE_ODL" != true ] && SERVICES="$SERVICES opendaylight"

if [ -n "$SERVICES" ]; then
    echo "Bringing up Docker services: $SERVICES"
    for s in $SERVICES; do
        container=$(docker-compose -p $COMPOSE_PROJECT ps -q $s)
        if [ -n "$container" ]; then
            echo "Workaround: Pre-removing $s container..."
            docker rm -f $container &>/dev/null
        fi
    done
    yes | docker-compose -p $COMPOSE_PROJECT up -d --force-recreate $SERVICES
fi

# --- Phase 3: Patch & Build ---
echo "--- Finalizing Application Configuration ---"

# Wait for ports to bind
echo "Waiting for services to be healthy..."
for p in $ACTIVEMQ_PORT $AXIS2_PORT $CXF_PORT $ODL_REST_PORT; do
    while ! nc -z localhost $p; do sleep 1; done
done
echo -e "\nServices are ready."

# Dynamic Patching
sed -i "s|tcp://localhost:[0-9]*|tcp://localhost:$ACTIVEMQ_PORT|g" src/main/java/org/opendaylight/messaging4transport/M4TRealService.java
sed -i "s|localhost:[0-9]*/axis2|localhost:$AXIS2_PORT/axis2|g" src/main/resources/services.conf
sed -i "s|localhost:[0-9]*/cxf|localhost:$CXF_PORT/cxf|g" src/main/resources/services.conf

echo "--- Compiling Évora ---"
mvn clean compile

echo "--- Évora Ecosystem Ready ---"
echo "SDN Controller (ODL) listening on port: $ODL_OF_PORT"
echo "Launch command: mvn exec:java -Dexec.mainClass=\"org.evora.core.EvoraMain\""
