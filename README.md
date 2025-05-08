# CSC 460 Database Project (Program 4)

### Authors

- **Zachary Astrowsky**:

  - Developed Query 1 (Member Ski Lessons) and all Lesson Purchase related database operations.
  - Designed database tables and constraints for Employees and Instructors.
  - Implemented some initial SQL data inserts for testing.

- **Steven George**:

  - Created and normalized the Entity-Relationship (E-R) Diagram for database schema.
  - Developed Query 4 (Equipment Change Log).
  - Designed and implemented Member and Ski Pass management queries and operations.
  - Set up initial SQL schema creation scripts.

- **Eduardo Hernandez**:

  - Developed Query 2 (Lift Rides and Equipment Rentals).
  - Designed, implemented, and optimized Equipment-related operations and logging functionalities.
  - Performed thorough testing, debugging, code optimization, and documentation.
  - Created comprehensive comments and the README documentation.

- **Vickram Sullhan**:

  - Developed Query 3 (Intermediate Trails and Lift Status).
  - Designed and implemented Rental database operations, including rental status updates and returns.
  - Designed database tables, relationships, and constraints for Trails and Lifts.

## Project Overview

This repository contains the implementation of a two-tier, database-driven information management system for a ski resort. The system uses an Oracle Database backend hosted on aloe.cs.arizona.edu and a Java-based JDBC frontend application providing users with a text-based interactive interface.

## Project Requirements

- **Backend**: Oracle DBMS (`aloe.cs.arizona.edu`)
- **Frontend**: Java (JDBC) running on Lectura servers
- **Database Design**: Relational schema with normalization (3NF/BCNF)

## Application Domain

This application supports a variety of operational functions for a ski resort, including:

- Member registration and management
- Ski pass management (creation, updating, archiving)
- Equipment inventory tracking, rental transactions, and change logging
- Lift ride logging and tracking
- Lesson scheduling and purchases
- Trail information and lift operations management

## Implemented Functionalities

The Java application implements these core operations:

### Database Editing Functions:

1. **Members**

   - Add, update, and delete member records.
   - Ensure integrity by checking active ski passes, rentals, and lesson sessions before deletion.

2. **Ski Passes**

   - Issue new ski passes.
   - Update pass usage counts and manage expirations.
   - Archive ski passes upon deletion (based on expiration or refund).

3. **Equipment**

   - Manage inventory records (insertion, updating, and archiving).
   - Maintain logs of all equipment changes.

4. **Equipment Rentals**

   - Record equipment rentals linked to ski passes.
   - Handle equipment returns and status updates.
   - Log rental updates and deletions.

5. **Lessons**
   - Record lesson purchases and track usage.
   - Allow updates on remaining lesson sessions.
   - Enable deletion if no sessions have been utilized.

### Database Queries Implemented:

- **Query 1**: List all ski lessons a member has purchased.
- **Query 2**: Display all lift rides and equipment rentals associated with a specific ski pass.
- **Query 3**: List open intermediate trails along with their categories and connected operational lifts.
- **Query 4** (custom): Provide detailed logs of changes for specific equipment items.

## Schema Structure Overview

The relational schema includes, but is not limited to, the following primary tables:

- **Member**: Basic personal and emergency contact information.
- **SkiPass**: Pass type, total uses, expiration, associated member ID.
- **Equipment**: Inventory of equipment items and their statuses.
- **Rental**: Rental records tied to ski passes and equipment.
- **LessonPurchase**: Purchases of ski lessons with session tracking.
- **LiftLog**: Lift usage entries linked to ski passes.
- **EquipmentChangeLog**: Logs of all equipment changes for auditing purposes.

## Technical Setup and Execution

### Database Connection

Ensure you have the Oracle JDBC driver on your classpath:

```bash
export CLASSPATH=/usr/lib/oracle/19.8/client64/lib/ojdbc8.jar:${CLASSPATH}
```

### Compilation and Execution

Compile the Java program:

```bash
javac Program4.java
```

Run the Java Program

```bash
java Program4
```

## Application Interaction

Upon execution, the program prompts the user to select between:

- **Editing the Database**: You can add/update/delete records for members, ski passes, equipment, rentals, and lesson purchases.
- **Running Sample Queries**: Execute pre-defined database queries to retrieve specific data.

Interactive menus guide the user through each function with clear prompts and validations.

## Project Constraints and Features

- Comprehensive validation checks (input formats, data integrity, operational constraints)
- Transaction management (commit/rollback on database operations)
- Detailed logging of changes and archival features for audit purposes
- Robust error handling and user-friendly console messaging

## Development and Collaboration

This project was developed collaboratively, emphasizing workload distribution and clear responsibility assignments. It follows industry best practices in teamwork and database application development.
