# BSS-OSS Backend Microservices Architecture & Core Java Technical Master Guide 🚀

---

## 📌 1. Project Overview & Usage (Ye Project Kaha Kaha Used Hota Hai)

**BSS-OSS (Business Support System & Operations Support System)** ek enterprise-level Telecom & ISP Software Suite hai jise Telecom Operators, Internet Service Providers (ISPs), FTTH (Fiber to the Home) Providers, and Mobility Operators rely karte hain.

### 🏢 Key Domains & Real-World Use Cases:
1. **FTTH & Broadband Management**: Customer onboarding, ONU/ONT provisioning, IP address management (IPAM), bandwidth capping, and plan renewal.
2. **Mobility & Mobile Network Services**: Prepaid/Postpaid SIM CAF (Customer Application Form) verification, recharge processing, and network subscriber tracking.
3. **AAA & Radius Server Integration**: User authentication, authorization, and session accounting via RADIUS protocol for WiFi/Broadband sessions.
4. **OCS (Online Charging System) & CPM**: Real-time charging, usage tracking, policy control, balance deduction, and automated notification triggers.
5. **CRM & Field Staff Management**: Lead capture, customer ticket management, SLA tracking, field engineer task assignment, and follow-ups.
6. **Billing, Revenue & Invoicing**: Automated invoice generation, payment gateway integration, taxation, partner commission calculation, and KRA accounting.

---

## 🏗️ 2. 14 Backend Microservices Hierarchy & Architecture

BSS-OSS backend Spring Boot Microservices architecture par based hai jahan Har service multi-tenant, scalable aur modular component ke tarah run karti hai.

```
                                  +-----------------------+
                                  |   Client / Frontend   |
                                  |  (admin.ui-dira / UI) |
                                  +-----------+-----------+
                                              |
                                              v
                              +---------------+---------------+
                              |    common.gateway-dira        |
                              |  (Spring Cloud Zuul / Gateway)|
                              +---------------+---------------+
                                              |
      +---------------------------------------+---------------------------------------+
      |                                       |                                       |
      v                                       v                                       v
+-----+-----------------+           +---------+---------+                   +---------+---------+
| service.registry-dira |           |   cpm.service     |                   | integration.service|
|  (Eureka Discovery)   |           | (Central Policy)  |                   | (Third-Party APIs)|
+-----------------------+           +---------+---------+                   +---------+---------+
                                              |
      +---------------------------------------+---------------------------------------+
      |                   |                   |                   |                   |
      v                   v                   v                   v                   v
+-----+-------+   +-------+-------+   +-------+-------+   +-------+-------+   +-------+-------+
|  inventory  |   |    revenue    |   |   salescrm    |   |    ticket     |   |task.management|
|  .service   |   |   .service    |   |   .service    |   |   .service    |   |     -dira     |
+-------------+   +---------------+   +---------------+   +---------------+   +---------------+
      |                   |                   |                   |                   |
      v                   v                   v                   v                   v
+-----+-------+   +-------+-------+   +-------+-------+   +-------+-------+   +-------+-------+
|   radius    |   |    partner    |   | nofitication  |   | captiveportal |   |dira.migration |
|  .service   |   |   .service    |   |   .service    |   |   .service    |   |    utility    |
+-------------+   +---------------+   +---------------+   +---------------+   +---------------+
```

### 📋 Microservices Roles & Internal Working:

1. **`common.gateway-dira`**: Central entry point (Zuul Gateway). Rate-limiting, authentication token verification (JWT), and API request routing perform karta hai.
2. **`service.registry-dira`**: Netflix Eureka Discovery Server. All microservices start hone par yahan execute hokar register hoti hain (`@EnableDiscoveryClient`).
3. **`cpm.service` (Central Policy Management & Charging)**: Real-time charging policy, customer quota rules, schedule actions, and Kafka event publishing handles karta hai.
4. **`integration.service`**: External Payment Gateways (Razorpay/PayTM), SMS Gateways, WhatsApp API, and Network Hardware (OLT/Router) APIs ke sath integration handle karta hai.
5. **`inventory.service`**: Routers, ONTs, SIM cards, IP Pools, Fiber cables, aur stock transfers manage karta hai.
6. **`revenue.service`**: Invoices, Receipts, Tax splits (GST/KRA), customer wallet balances, and auto-recurring bill generation control karta hai.
7. **`salescrm.service`**: Customer Onboarding (CAF), Lead Management, Package & Plan Management, Subscriber Profile Data maintaining system hai.
8. **`ticket.service`**: Customer complaints, Network outages, Helpdesk ticketing, and Resolution workflow tracking system.
9. **`task.management-dira`**: Field engineer assignment, SLA matrix, Task scheduling, and Team hierarchy enforcement.
10. **`radius.service`**: High-performance AAA engine integration. Radius sessions, Bandwidth throttling, NAS communication handle karta hai.
11. **`partner.service`**: LCO (Local Cable Operator), Franchisee management, Revenue sharing commission calculations.
12. **`nofitication.service`**: Asynchronous Email/SMS/Push notifications processing using Kafka topics.
13. **`captiveportal.service`**: Public WiFi Hotspot authentication, Guest login verification, and session timeout management.
14. **`dira.migrationutility`**: Batch data import/export, Excel sheet migration scripts, legacy system data transformations.

---

## 🔌 3. Microservice APIs Breakdown & JSON Demo Records

Har microservice RESTful API contract publish karti hai. Below detailed endpoints and representative JSON request/response payloads match standard project architecture:

### A. CPM Service (`cpm.service`)
* **Endpoint**: `POST /api/v1/cpm/policy/evaluate`
* **Description**: Subscriber ke active plan quota aur rules check karke policy response calculate karta hai.
* **JSON Payload (Request)**:
```json
{
  "subscriberId": "SUB-998844",
  "serviceType": "FTTH",
  "currentUsageMb": 1048576,
  "allocatedQuotaMb": 2097152,
  "nasIpAddress": "10.200.1.1"
}
```
* **JSON Payload (Response)**:
```json
{
  "status": "SUCCESS",
  "statusCode": 200,
  "data": {
    "subscriberId": "SUB-998844",
    "policyAction": "ALLOW_TRAFFIC",
    "downloadSpeedKbps": 102400,
    "uploadSpeedKbps": 51200,
    "remainingQuotaMb": 1048576
  }
}
```

### B. Sales CRM Service (`salescrm.service`)
* **Endpoint**: `POST /api/v1/SavbillSalesCrmsBss/customer/create`
* **Description**: Naya customer (CAF) onboarding record create karta hai.
* **JSON Payload**:
```json
{
  "firstName": "Sadique",
  "lastName": "Amin",
  "email": "sadique@example.com",
  "mobileNumber": "9876543210",
  "documentType": "AADHAAR",
  "documentNumber": "1234-5678-9012",
  "planId": 504,
  "installationAddress": {
    "city": "Mumbai",
    "pincode": "400001",
    "subAreaId": 12
  }
}
```

### C. Inventory Service (`inventory.service`)
* **Endpoint**: `PUT /api/v1/SavbillInventoryManagement/device/assign`
* **Description**: Customer ko ONT/Router device allocate karta hai.
* **JSON Payload**:
```json
{
  "subscriberId": "SUB-998844",
  "serialNumber": "HW-ONT-2026-9901",
  "macAddress": "AA:BB:CC:DD:EE:FF",
  "deviceType": "ONT",
  "assignedByStaffId": 102
}
```

### D. Revenue & Billing Service (`revenue.service`)
* **Endpoint**: `POST /api/v1/revenue/invoice/generate`
* **Description**: Plan renewal invoice calculate aur save karta hai.
* **JSON Payload**:
```json
{
  "subscriberId": "SUB-998844",
  "planPrice": 999.00,
  "taxAmount": 179.82,
  "totalAmount": 1178.82,
  "paymentStatus": "PAID",
  "paymentMethod": "UPI"
}
```

### E. Ticket & Task Management (`ticket.service` & `task.management-dira`)
* **Endpoint**: `POST /api/v1/TicketManagement/ticket/create`
* **Description**: No Internet speed fault complaint raise karta hai.
* **JSON Payload**:
```json
{
  "subscriberId": "SUB-998844",
  "category": "FAULT",
  "subCategory": "NO_BROWSING",
  "priority": "HIGH",
  "description": "Red Light optical LOS flashing on ONT router"
}
```

---

## 💻 4. Core Java & Spring Boot Technical Deep-Dive

Project code implementation ke basis par niche diye gaye sabhi core concepts mapped hain:

### ⚡ Concurrency & Multi-Threading

#### 1. MultiThreading
* **Concept**: Simultaneously multiple tasks execute karne ke liye threads create aur execute kiye jaate hain.
* **Project Context**: `cpm.service` me Kafka messages asynchronously processing ke liye thread execution pattern follow kiya gaya hai.
```java
Thread kafkaThread = new Thread(kafkaMessageReceiver);
kafkaThread.start();
```

#### 2. ExecutorFramework
* **Concept**: Thread creation manual karne ke bajaye `ExecutorService` thread pool manage karta hai context switching reduce karne ke liye.
* **Project Usage** (`CPMApplication.java`):
```java
@PostConstruct
public void init() {
    ExecutorService executorService = Executors.newSingleThreadExecutor();
    executorService.submit(kafkaMessageReceiver);
}
```

#### 3. Volatile
* **Concept**: CPU Cache sync issue eliminate karta hai. Variable state direct main memory me read/write hoti hai.
* **Project Context**: Background polling tasks (jaise Radius ping check ya Kafka status listener) me shared flag ke liye use hota hai.
```java
private volatile boolean isWorkerRunning = true;
```

#### 4. synchronized
* **Concept**: Thread safety issue (Race Condition) avoid karne ke liye critical section par lock lagata hai.
* **Project Context**: Wallet Balance deduction ya Inventory Stock count updates.
```java
public synchronized void deductWalletBalance(Long subscriberId, Double amount) {
    // Critical Section: Double debit fix
}
```

#### 5. Atomic (AtomicInteger / AtomicLong)
* **Concept**: Lock-free thread-safe operations perform karta hai CAS (Compare-And-Swap) hardware instruction use karke.
* **Project Context**: API Hit Count tracking ya Sequence Number Generation.
```java
private final AtomicInteger requestCounter = new AtomicInteger(0);
public int getNextRequestId() {
    return requestCounter.incrementAndGet();
}
```

---

### 📦 Generics, Collections & Streams

#### 6. Generics (`<T>`)
* **Concept**: Type safety at compile time.
* **Project Context**: Standard `ApiResponse<T>` wrapper for all microservices.
```java
public class ApiResponse<T> {
    private String status;
    private int code;
    private T data;
}
```

#### 7. Collection Framework & Map Interface
* **Concept**: Data storage & manipulation structures (`List`, `Set`, `Map`, `ConcurrentHashMap`).
* **Project Context**: In-memory session store & fast key-value lookup.
```java
Map<String, CustomerDTO> activeSessionMap = new ConcurrentHashMap<>();
```

#### 8. Stream API & Collectors
* **Concept**: Functional style sequence pipeline filtering, mapping, and aggregating operations.
* **Project Context**: Invoices array me se overall total revenue calculate karna.
```java
Double totalRevenue = invoiceList.stream()
    .filter(inv -> "PAID".equalsIgnoreCase(inv.getPaymentStatus()))
    .mapToDouble(Invoice::getGrandTotal)
    .sum();
```

---

### ⚙️ Java 8 Functional Interfaces & References

1. **Lambda (`() -> {}`)**: Anonymous functions to pass behavior as argument.
2. **Predicate (`Predicate<T>`)**: Takes `T`, returns `boolean`. (e.g. `Predicate<Customer> isActive = c -> c.getStatus().equals("ACTIVE");`)
3. **Function (`Function<T, R>`)**: Takes `T`, returns `R`. (e.g. `Function<CustomerEntity, CustomerDTO> mapper = e -> modelMapper.map(e, CustomerDTO.class);`)
4. **Supplier (`Supplier<T>`)**: Takes no input, returns `T`. Used in lazy evaluation (e.g. `repo.findById(id).orElseThrow(() -> new ResourceNotFoundException("Subscriber not found"));`)
5. **Consumer (`Consumer<T>`)**: Takes `T`, returns `void` (e.g. `.forEach(ticket -> notifyStaff(ticket))`).
6. **BiPredicate (`BiPredicate<T, U>`)**: Takes 2 inputs, returns `boolean`. (e.g. Check if subscriber plan matches current area code).
7. **BiFunction (`BiFunction<T, U, R>`)**: Takes 2 inputs, calculates and returns output `R` (e.g. Base Price + Tax calculation).
8. **BiConsumer (`BiConsumer<T, U>`)**: Takes 2 inputs, returns `void` (e.g. `map.forEach((key, val) -> log.info("{} : {}", key, val))`).
9. **UnaryOperator (`UnaryOperator<T>`)**: Special case of Function where input and output type are same.
10. **BinaryOperator (`BinaryOperator<T>`)**: Special case of BiFunction where both inputs and output are same type (e.g. `BinaryOperator<Double> sum = (a, b) -> a + b;`).
11. **Method Reference (`Class::method`)**: Shorthand notation of lambda (e.g. `List<String> names = customers.stream().map(Customer::getFirstName).collect(Collectors.toList());`).
12. **Constructor Reference (`Class::new`)**: Instantiating object using method reference (e.g. `Supplier<List<String>> listSupplier = ArrayList::new;`).
13. **Iterator**: Sequential traversal over collection elements (`iterator.hasNext()`, `iterator.next()`).

---

### ☁️ Cloud, Framework & Messaging Components

#### 1. Kafka
* **Concept**: High-throughput distributed event-streaming platform.
* **Project Context**: `cpm.service` usage notification raise karke `nofitication.service` ko asynchronous topic event deliver karta hai.

#### 2. FeignClient
* **Concept**: Declarative REST client for inter-service communication.
* **Project Context**: `salescrm.service` calling `inventory.service` directly:
```java
@FeignClient(name = "inventory-service")
public interface InventoryClient {
    @GetMapping("/api/v1/inventory/device/{serialNo}")
    DeviceDTO getDeviceBySerial(@PathVariable("serialNo") String serialNo);
}
```

#### 3. Eureka (Service Discovery)
* **Concept**: Microservice dynamic IP/Port address resolution. All services register with `service.registry-dira`.

#### 4. Docker
* **Concept**: Containerization platform. Har microservice ke andar `Dockerfile` dynamic container deployment manage karti hai.

#### 5. Spring Boot
* **Concept**: Rapid Java application development framework auto-configuration & embedded Tomcat server ke saath.

---

## 🪵 5. Logging Strategy (Logs Add & Check Kaise Karein)

### A. Code me Log Add Karne Ka Tarika:

#### Method 1: Using `@Slf4j` (Lombok Annotation)
```java
@Service
@Slf4j
public class CustomerServiceImpl implements CustomerService {

    public CustomerDTO createCustomer(CustomerDTO dto) {
        log.info("Creating new customer with email: {}", dto.getEmail());
        try {
            // Save logic
            log.debug("Successfully saved customer record into database");
        } catch (Exception ex) {
            log.error("Error occurred while creating customer: {}", ex.getMessage(), ex);
        }
        return dto;
    }
}
```

#### Method 2: Using LoggerFactory (Classic SLF4J)
```java
private static final Logger log = LoggerFactory.getLogger(CPMApplication.class);
log.info("************* Application Started Successfully ****************");
```

---

### B. Logs Check Karne Ke Command-Line Steps:

#### 1. Real-Time Docker Container Logs Check Karna:
```bash
# General Container Log Tail
docker logs -f <container_name_or_id>

# Example: CPM Service Log Check
docker logs --tail 200 -f bss-cpm-service
```

#### 2. Log File Direct Server File-System me Check Karna:
Standard log paths in BSS-OSS:
`d:\KTPL\BSS-OSS\logs\` ya container filesystem `var/log/savbill/`

```bash
# Windows PowerShell Log Tail
Get-Content -Path "d:\KTPL\BSS-OSS\logs\cpm.log" -Wait -Tail 50

# Linux / Terminal Tail Command
tail -f /var/log/savbill/cpm.service.log
```

---

## 🔄 6. Repository Synchronization & Latest Changes Status

* **Git Pull Execution Status**: `git pull origin main` executed successfully (`Already up to date`).
* **Latest Commit Alignment**: `8ecf8cba` (*Initial commit of complete BSS-OSS project structure*).
* **Branch Verification**: Active branch `main` is completely in sync with `origin/main`.
* **Services & Modules Verified**:
  * 14 Microservices (`common.gateway-dira`, `service.registry-dira`, `cpm.service`, `captiveportal.service`, `integration.service`, `inventory.service`, `nofitication.service`, `partner.service`, `radius.service`, `revenue.service`, `salescrm.service`, `task.management-dira`, `ticket.service`, `dira.migrationutility`).
  * 4 Frontend Modules (`admin.ui-dira`, `savanna.customerapp`, `savanna.cwsc`, `savanna.fieldapp`).
  * Diameter AAA Engine (`diameter-protocol` - Gy/Ro/Gx specs & AVPs).
* **Documentation Verification**: All `.md` architecture & technical guides fully aligned with codebase state.

---
*Created & Verified for BSS-OSS Architecture Master Documentation.* 🎯

