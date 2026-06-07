# Smart Employee Management System

An enterprise-grade desktop application designed for secure, efficient, and scalable workforce management. This system utilizes a monolithic architecture with a strict Separation of Concerns (SoC), isolating database operations from the frontend rendering logic.

---
**Author:** Karandeep Singh
*B.Tech AI & ML, CGC University*
*GitHub:* [karan0dev](https://github.com/karan0dev)

## ⚙️ Core Architecture & Tech Stack

* **Frontend:** JavaFX (Modern Hub-and-Spoke UI layout, Custom CSS)
* **Backend Database:** MySQL (Relational Data Management)
* **Database Bridging:** JDBC API (Implementation of the DAO Pattern)
* **Build System:** Apache Maven (Dependency management and Fat JAR packaging)
* **Data Processing:** Java 8 Streams API (In-memory analytics aggregation)

## 🏗️ System Highlights

* **Data Access Object (DAO) Pattern:** SQL logic is strictly encapsulated within the `dao` package (`EmployeeDAO.java`), protecting the presentation layer from backend schema changes and preventing SQL Injection via `PreparedStatements`.
* **Real-Time Analytics:** The Dashboard Analytics module bypasses latency-heavy SQL `GROUP BY` operations. Instead, it leverages Java Streams to process `Employee` POJOs in-memory, dynamically rendering Workforce Distribution (Pie Chart) and Compensation insights (Bar Chart).
* **Dynamic Filtering:** The employee directory utilizes JavaFX `FilteredList` and `SortedList` wrappers over an `ObservableList` to provide real-time, zero-latency search queries without requiring database round-trips.
* **Environment Agnostic Deployment:** The `pom.xml` utilizes the Maven Shade Plugin to compile all external dependencies (MySQL Connector, JavaFX engines) into a single executable Fat JAR, ensuring the software runs on any host machine without manual driver configuration.

## 🚀 Local Deployment Protocol

### Prerequisites
* Java Development Kit (JDK) 21 or higher
* MySQL Server & MySQL Workbench
* Apache Maven

### Database Configuration
1. Open MySQL Workbench.
2. Execute the provided SQL schema script to generate the `employee_management` database, the `employees` table, and the default admin credentials.
3. Update the `DBConnection.java` utility file with your local MySQL root password.

### Compilation & Execution
To compile the raw code into a standalone executable, run the following Maven command in the project root:
```bash
mvn clean package
