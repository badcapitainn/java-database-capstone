Here is the requested content, combining the humanized paragraph and the numbered list, formatted in Markdown.

This Spring Boot application efficiently manages a medical system by separating its front-end presentation and back-end data needs. The user-facing Admin and Doctor dashboards are built using the classic MVC architecture and rendered with Thymeleaf templates, offering a traditional web experience. Meanwhile, data for all other system modules is exposed via modern REST APIs, making it easily accessible to other services or mobile apps. All requests flow through a central, unified service layer, which acts as the application's brain. This layer intelligently directs data operations to the appropriate database: core structured information like patients, doctors, appointments, and admin details lives in MySQL (managed by JPA entities), while flexible, unstructured prescription data is stored in MongoDB (using document models). This dual-database setup, orchestrated through a common service layer, ensures the application is both robust and specialized.

🗺️ Data Flow Steps
User Access: The user interacts with the application through two main interfaces: the Dashboards (Admin and Doctor) or the REST Modules (Appointments, Patient Dashboard, Patient Record).

Request Routing: Requests from the Dashboards are routed to the Thymeleaf Controllers (using MVC), while requests from the REST Modules are routed to the REST Controllers (using JSON API).

Core Logic Execution: Both sets of controllers delegate the request to the central Service Layer, which handles the application's business logic.

Data Access Delegation: The Service Layer determines which database is needed and calls the appropriate repository: the MySQL Repositories for structured data or the MongoDB Repository for prescription data.

Database Connection: The Repositories then send the request to access the corresponding database: the MySQL Database or the MongoDB Database.

MySQL Data Retrieval: The MySQL Database retrieves core data (Patient, Doctor, Appointment, Admin) using JPA Entities mapped to MySQL Models.

MongoDB Data Retrieval: Concurrently, the MongoDB Database retrieves flexible Prescription data using a Document Model mapped to a MongoDB Model. The retrieved data then flows back up through the layers to the user.

Would you like a more technical explanation of a specific component, like the Service Layer or the difference between JPA Entities and Document Models?
