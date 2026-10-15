
# TCP vs IP (TCP/IP)


flowchart TD
    HTTP["HTTP/1.1 and HTTP/2"] --> TCP["TCP"]
    APP["Custom Socket Application"] --> TCP
    HTTP3["HTTP/3"] --> QUIC["QUIC"] --> UDP["UDP"]
    TCP --> IP["IP - IPv4 / IPv6"]
    UDP --> IP
    IP --> NETWORK["Network"]

    style TCP fill:#d1fae5,color:#065f46,stroke:#34d399
    style UDP fill:#fef3c7,color:#92400e,stroke:#f59e0b
    style IP fill:#f3e8ff,color:#6b21a8,stroke:#c084fc

TCP and IP are two different protocols that work together to enable communication between applications across a network.

## 1. IP vs TCP

| Feature | IP (Internet Protocol) | TCP (Transmission Control Protocol) |
|---|---|---|
| Layer | Network / Internet | Transport |
| Purpose | Addressing and routing packets | Reliable, ordered data delivery |
| Identifies | Source and destination IP addresses | Source and destination ports |
| Reliability | No delivery guarantee | Retransmits lost data |
| Connection | Connectionless | Connection-oriented |
| Example | `192.168.1.10` | Port `5000` |
| Java | `InetAddress` | `Socket`, `ServerSocket` |

## 2. How TCP/IP Works

```text
Client Application
       |
       v
TCP (Transport Layer)
  - Manages connection
  - Uses port numbers
  - Ensures reliable delivery
       |
       v
IP (Network Layer)
  - Uses IP addresses
  - Routes packets
       |
       v
      Network
       |
       v
Server (IP → TCP → Application)
```

**Key points:**

- TCP uses IP to transport data between machines.
- TCP data is carried inside IP packets.
- TCP handles reliability; IP handles addressing and routing.
- The operating system implements TCP/IP, so applications don't need to manually construct TCP or IP packets.
- IP can also carry other protocols, such as UDP.

## 3. Where Do We See TCP/IP in Java?

| Java Code | What it represents |
|---|---|
| `new ServerSocket(5000)` | Creates a TCP server listening on port 5000 |
| `new Socket("192.168.1.10", 5000)` | Connects to the specified IP address and TCP port |
| `socket.getInetAddress()` | Returns the remote machine's IP address |
| `socket.getLocalAddress()` | Returns the local IP address used by the connection |
| `socket.getInputStream()` | Reads bytes received through TCP |
| `socket.getOutputStream()` | Writes bytes to be sent through TCP |

**Remember:** `Socket` uses TCP, while the operating system handles IP underneath. The application generally doesn't need to implement either protocol.

## 4. Code Example

[Java TCP Client–Server Example](./tcp-client-server.md)

*Separate file containing server code, client code, connection setup, and IP/port demonstration.*
