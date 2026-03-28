# Évora Research Framework

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
- `src/main/java/org/evora/core`: Building block and composition logic.
- `src/main/java/org/evora/registry`: Service Registry implementation.
- `pom.xml`: Configured with ODL repositories and `messaging4transport-impl` dependency.

# Citing Évora

If you use Évora in your research, please cite the below paper:

* Kathiravelu, P., Van Roy, P. and Veiga, L., 2018. **Composing network service chains at the edge: A Resilient and adaptive software‐defined approach.** Transactions on Emerging Telecommunications Technologies, 29(11), p.e3489.
