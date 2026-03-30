# Évora 

Évora is a research framework for service composition and orchestration in SDN-enabled environments.

## Messaging4Transport Integration

The framework is configured to use the [Messaging4Transport](https://github.com/KathiraveluLab/messaging4transport) middleware for robust MD-SAL to AMQP bindings.

### Local Installation of Messaging4Transport

Since Messaging4Transport is a separate project, you must install it to your local Maven repository (`~/.m2/repository`) before building Évora.

#### Automated Installation
```bash
chmod +x install_m4t.sh
./install_m4t.sh
```

#### Manual Installation
1.  **Clone the repository**:
    ```bash
    git clone https://github.com/KathiraveluLab/messaging4transport.git
    ```
2.  **Install to local Maven repository**:
    ```bash
    cd messaging4transport
    mvn clean install -DskipTests
    ```

> [!NOTE]
> If you do not have Maven installed, you can install it on Ubuntu/Linux using:
> `sudo apt install maven`

## Project Structure
- `src/main/java/org/evora/core`: Building block, composition, and Orchestration logic.
- `src/main/java/org/evora/registry`: Service Registry implementation.
- `evora_topology.py`: 12-node Mininet edge topology script.
- `pom.xml`: Configured with ODL repositories and `messaging4transport-impl` dependency.

## Empirical Evaluation

To reproduce the performance results (Speedup and Complexity) from the ETT 2018 paper:
1.  **Build the project**: `mvn clean compile`
2.  **Run the Benchmarker**:
    ```bash
    java -cp target/classes org.evora.core.Benchmarker
    ```

## SDN Deployment (Mininet)

To spin up the 12-node edge topology in a Mininet environment managed by OpenDaylight:
```bash
sudo mn --custom evora_topology.py --topo evora --controller remote,ip=<odl_ip>
```

## Citing Évora

If you use Évora in your research, please cite the following papers:

* Kathiravelu, P., Van Roy, P. and Veiga, L., 2018. **Composing network service chains at the edge: A Resilient and adaptive software‐defined approach.** Transactions on Emerging Telecommunications Technologies, 29(11), p.e3489.

* Kathiravelu, P., Grbac, T.G. and Veiga, L., 2016, June. **Building blocks of Mayan: Componentizing the escience workflows through software-defined service composition.** In 2016 IEEE International Conference on Web Services (ICWS) (pp. 372-379). IEEE.
