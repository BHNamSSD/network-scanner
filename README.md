# Java Network Scanner

A lightweight LAN network scanner built with **Java 21** and **Swing**.

The application discovers active hosts on a local network and displays their IP address, hostname, MAC address, connection status, and response time through a simple desktop GUI.

## Features

* 🔎 Scan a custom IP range
* 🟢 Detect active (`UP`) hosts
* 💻 Resolve hostnames
* 🔗 Retrieve MAC addresses from the Windows ARP table
* ⏱️ Display response time
* 🔍 Filter results to show `UP` hosts only
* 📊 Real-time scan progress
* ⏹️ Stop an ongoing scan
* 🖥️ Lightweight Swing GUI
* ☕ Java 21 compatible
* 🪟 Designed for Windows

## Screenshot

*Add a screenshot of the application here.*

```text
Subnet: [192.168.1]  Start: [1]  End: [254]

Filter: [UP only]   [Scan]   [Stop]

IP Address     Host Name       MAC Address        Status   Response
192.168.1.1    router.local    AA-BB-CC-DD-EE-01  UP       3 ms
192.168.1.5    DESKTOP-PC      AA-BB-CC-DD-EE-05  UP       5 ms
192.168.1.10   server.local    AA-BB-CC-DD-EE-10  UP       2 ms
```

## Requirements

* Java 21 or later
* Windows
* IntelliJ IDEA or another Java IDE
* Maven

The project can also run on ARM64 systems with an ARM64 JDK.

## Project Structure

```text
java-network-scanner/
│
├── src/
│   └── main/
│       └── java/
│           └── com/
│               └── bhnam/
│                   ├── Main.java
│                   ├── NetworkScanner.java
│                   └── ScannerWindow.java
│
├── .gitignore
├── pom.xml
└── README.md
```

## How It Works

The scanner performs host discovery using Java's networking API.

```text
User
 │
 │ Enter subnet and IP range
 ▼
Scanner GUI
 │
 ▼
Network Scanner
 │
 ├── Host Reachability
 │
 ├── Hostname Resolution
 │
 └── ARP Table
       │
       └── MAC Address
 │
 ▼
Scan Results
```

### Host Discovery

The scanner uses:

```java
InetAddress.isReachable()
```

to determine whether a host responds to the reachability check.

### Hostname

For reachable hosts, the application attempts to resolve the hostname using:

```java
InetAddress.getCanonicalHostName()
```

### MAC Address

On Windows, the scanner retrieves MAC address information from the local ARP table using:

```text
arp -a
```

This means a MAC address may not always be available if Windows does not have an ARP entry for the target host.

## Usage

Start the application and enter a subnet.

For example:

```text
Subnet: 192.168.1
Start: 1
End: 254
```

Click:

```text
Scan
```

The scanner will check:

```text
192.168.1.1
192.168.1.2
192.168.1.3
...
192.168.1.254
```

### Filtering

Use the filter dropdown:

```text
All
```

to display all scanned hosts.

Select:

```text
UP only
```

to display only hosts that responded.

## Build

Clone the repository:

```bash
git clone https://github.com/<your-username>/java-network-scanner.git
```

Enter the project directory:

```bash
cd java-network-scanner
```

Build with Maven:

```bash
mvn clean package
```

Run the application:

```bash
java -cp target/network-scanner-1.0-SNAPSHOT.jar com.bhnam.Main
```

Alternatively, run `Main.java` directly from IntelliJ IDEA.

## Configuration

The scanner does not require an external configuration file.

The IP range can be configured directly from the GUI.

Example:

```text
Subnet: 192.168.0
Start: 1
End: 100
```

## Limitations

This is currently a lightweight host discovery tool rather than a full network reconnaissance framework.

### MAC address

MAC addresses are obtained from the Windows ARP table. Therefore:

* Some hosts may not have a MAC address available.
* MAC resolution depends on the local ARP cache.
* The scanner is primarily intended for local networks.

### Host discovery

`InetAddress.isReachable()` does not guarantee that a host is actually offline when it returns `false`. Firewalls or network configurations may prevent responses.

## Future Improvements

Planned features:

* [ ] TCP port scanner
* [ ] Common service detection
* [ ] Port status visualization
* [ ] MAC vendor lookup
* [ ] Export results to CSV
* [ ] Export results to JSON
* [ ] Scan multiple subnets
* [ ] Concurrent host scanning
* [ ] Configurable timeout
* [ ] Scan history
* [ ] Network interface selection
* [ ] Dark mode
* [ ] Executable JAR distribution

## Technologies

| Technology          | Purpose                                |
| ------------------- | -------------------------------------- |
| Java 21             | Application language/runtime           |
| Swing               | Desktop GUI                            |
| Java Networking API | Host discovery and hostname resolution |
| Windows ARP         | MAC address discovery                  |
| Maven               | Project/build management               |
| IntelliJ IDEA       | Development environment                |

## Security & Responsible Use

This tool is intended for **authorized network discovery and administration**.

Only scan networks and devices that you own or have explicit permission to assess.

## License

This project is provided for educational and authorized security testing purposes.

Add an appropriate open-source license if you plan to distribute the project publicly.

