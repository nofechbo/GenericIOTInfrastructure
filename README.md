# Generic IoT Infrastructure

⚠️ Note: This project is a learning project, developed as part of a course. Many implementation choices were made for educational purposes and may not follow best practices in real-world software development.

This project is a demonstration of an IoT management system designed for companies. It provides a scalable and concurrent solution for registering, managing, and collecting data from IoT devices efficiently. With this system, companies can onboard their devices and define flexible, device-specific APIs tailored to their operational needs.

The project is built with React for the frontend and Java (Servlets running on Tomcat) for the backend.

## User Requirements

1. **High Concurrency**: Efficiently handle a large volume of simultaneous requests.
2. **Modular Architecture**: Ensure components are modular for easy maintenance and scalability.
3. **Dynamic Command Addition**: Allow new commands to be added without server downtime.
4. **User Interface**: Provide a UI for seamless interaction with the server.
5. **Protocol Support**: Support multiple networking protocols for diverse IoT devices.
6. **Security**: Ensure that IoT data from different companies is kept separate.

## Core Functionality

The project includes four fundamental requests:
- **Register Company**: Register a new company (user).
- **Register Product**: Register a new product type for a company.
- **Register Device**: Register a specific device.
- **Register Update**: Register updates sent by devices.

* The IoT infrastructure allows the addition of new commands dynamically without requiring server downtime.

## Design Patterns Used

The project incorporates multiple software design patterns for scalability and maintainability:

- **Factory Pattern**: Used for request parsing and command creation.
- **Command Pattern**: Encapsulates requests as objects, allowing flexible processing.
- **Mediator Pattern**: Manages communication between components to reduce dependencies.
- **Observer Pattern**: Implements plug-and-play functionality by dynamically loading new commands.
- **Strategy Pattern**: Used for selecting different networking protocols dynamically.

## Technical Capabilities

1. **Thread Pool Management**: Implements a thread pool in the gateway server for efficient request handling.
2. **Plug & Play Components**: Supports dynamically adding new functionality without restarting the server.
3. **Protocol Handling**: Supports HTTP, TCP, and UDP for device communication.
4. **Web Interface**: Developed with React to interact with the server and execute primary commands.
5. **Component Independence**: Ensures each project component operates independently.

## Main Components

### 1. IOTWebsite
- Contains both frontend and backend components.
- **Frontend**:
  - Developed using **React**.
  - Communicates with the backend using REST APIs.
  - Provides an intuitive interface for managing IoT data.
- **Backend**:
  - Developed using **Java Servlets running on Tomcat**.
  - Handles API communication with the frontend.
  - Implements core business logic for processing IoT device registrations and updates.
  - Provides API access to the **administrative database**.

### 2. Gateway Server
- Contains all core server logic outside of the website.
- **Connection Service**: Supports HTTP, TCP, and UDP protocols.
- **Request Processing Service (RPS)**: Uses a thread pool for concurrent request handling.
- **Plug & Play Service**: Dynamically loads new commands from external `.JAR` files.

### 3. Databases
- **Administrative Database (GDBM)**: Stores company and product registration data, accessible via the IOTWebsite backend.
- **IoT DBMS**: Stores updates received from IoT devices, managed by the Gateway Server.

## Directory Structure

- `/IOTWebsite`
  - `/frontend` - React-based frontend files.
  - `/backend` - Java Servlets handling API requests, running on Tomcat, and managing administrative database interactions.

- `/GatewayServer`
  - `ConnectionService`
     - Manages connections using HTTP, TCP, and UDP protocols.
  - `RequestProcessingService`
     - Processes incoming requests with a thread pool.
  - `Plug & Play`
     - Dynamically loads new `.JAR` files for additional commands.

## Getting Started

1. **Clone the Repository**
    ```bash
    git clone <repository-url>
    ```

2. **Navigate to the Project Directory**
    ```bash
    cd GenericIOTInfrastructure
    ```

3. **Start the Gateway Server**
    ```bash
    cd GatewayServer
    java -jar gateway-server.jar
    ```

4. **Start the Backend Server on Tomcat**
    ```bash
    cd ../IOTWebsite/backend
    # Deploy the WAR file to Tomcat
    cp backend.war /path/to/tomcat/webapps/
    ```

5. **Install Dependencies for the Frontend**
    ```bash
    cd ../frontend
    npm install
    ```

6. **Run the Frontend**
    ```bash
    npm run dev
    ```

## Usage

- Access the website at `http://localhost:3000` to manage IoT data.
- The Gateway Server will handle incoming connections and process requests using the specified protocols.

## Project Overview

The Generic IoT Infrastructure project provides a robust platform for managing data from IoT devices. It is designed to be scalable and efficient, supporting multiple networking protocols and ensuring smooth data processing. The entire project was developed from scratch with a focus on modularity and extensibility.

## Future Enhancements

- **Enhanced Security Measures**: Implement additional authentication mechanisms.
- **Support for More Protocols**: Extend beyond HTTP, TCP, and UDP.
- **Advanced Analytics Dashboard**: Improve data visualization and monitoring capabilities.
- **Watchdog Component**: Implement a monitoring system to detect failures and recover automatically for improved fault tolerance.
- **Dynamic Load Balancer**: Introduce a mechanism to create and destroy new Gateway Servers based on the number of incoming requests, ensuring efficient scaling.

This project serves as an educational demonstration of scalable IoT management systems.


