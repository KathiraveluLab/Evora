# Évora 

Évora is a research framework for service composition and orchestration in SDN-enabled environments.

## Docker Environment

For research reproducibility, Évora provides a Dockerized environment for its core dependencies (ActiveMQ, Axis2, CXF).

### Automated Startup
```bash
chmod +x evora.sh
./evora.sh
```

> [!TIP]
> If you encounter Docker errors like `KeyError: 'ContainerConfig'`, run the script with the clean flag:
> `./evora.sh --clean`

The services will be available at:
- **ActiveMQ (AMQP)**: `localhost:61616` (or next free port)
- **Axis2 (Legacy)**: `localhost:8080` (or next free port)
- **CXF (Modern)**: `localhost:8081` (or next free port)

> [!NOTE]
> **Dynamic Port Resolution**: The `evora.sh` script automatically detects port conflicts.
> 1. If the required port is occupied by the *same* service (e.g., from another project), it will reuse it.
> 2. If occupied by a *different* service, it assigns the next available port and automatically patches the Java source code and configuration files before building.

## Framework Setup

Évora requires the [Messaging4Transport](https://github.com/KathiraveluLab/messaging4transport) middleware to be installed in your local Maven repository.

The `evora.sh` script handles everything automatically:
1. Verifies/installs Maven.
2. Auto-installs Messaging4Transport dependency if missing.
3. Manages Docker dependencies with dynamic port resolution.
4. Patches the framework and compiles the project.

```bash
chmod +x evora.sh
./evora.sh
```

## Project Structure
- `src/main/java/org/evora/core`: Building block, composition, and Orchestration logic.
- `src/main/java/org/evora/registry`: Service Registry implementation.
- `evora_topology.py`: 12-node Mininet edge topology script.
- `pom.xml`: Configured with ODL repositories and `messaging4transport-impl` dependency.

**Run the Benchmarker** (after running `./evora.sh`):
```bash
mvn exec:java -Dexec.mainClass="org.evora.core.util.Benchmarker"
```

**Run Sample Orchestration**:
```bash
mvn exec:java -Dexec.mainClass="org.evora.core.EvoraMain"
```

## SDN Deployment (Mininet)

To spin up the 12-node edge topology in Mininet and connect it to the containerized OpenDaylight controller:
```bash
# Connect Mininet to the Dockerized ODL (listening on host port 6633)
sudo mn --custom evora_topology.py --topo evora --controller remote,ip=127.0.0.1,port=6633
```

## Custom Simulation Inputs

To use your own topology and service chains, edit the configuration file:
- `src/main/resources/input.json`

The framework will automatically detect this file and load your custom nodes, link latencies, VNF placements, and orchestration policies.

**Example `input.json` Schema:**
```json
{
  "topology": [{"id": "node1", "neighbors": ["node2"], "services": ["s1"], "cost": 10.0, "latency": 2.0, "throughput": 100.0}],
  "links": [{"from": "node1", "to": "node2", "latency": 5.0}],
  "serviceChain": ["s1", "s2"],
  "policy": {"costWeight": 1.0, "latencyWeight": 10.0, "throughputWeight": 1.0}
}
```

## Citing Évora

If you use Évora in your research, please cite the following papers:

* Kathiravelu, P., Van Roy, P. and Veiga, L., 2018. **Composing network service chains at the edge: A Resilient and adaptive software‐defined approach.** Transactions on Emerging Telecommunications Technologies, 29(11), p.e3489.

* Kathiravelu, P., Grbac, T.G. and Veiga, L., 2016, June. **Building blocks of Mayan: Componentizing the escience workflows through software-defined service composition.** In 2016 IEEE International Conference on Web Services (ICWS) (pp. 372-379). IEEE.
