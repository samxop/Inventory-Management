# Inventory Management System (Java Swing + JDBC)

## Overview
A desktop Inventory Management System built with Java Swing for the GUI and JDBC connecting to MySQL. The project demonstrates CRUD operations on products and suppliers, searching, low-stock alerts, total inventory value calculation, and CSV export.

## Prerequisites
- Java 8 or later (JDK installed)
- MySQL server
- MySQL Connector/J (add the JDBC driver jar to project classpath)
- An IDE such as IntelliJ IDEA or Eclipse (optional)

## Setup
1. Run the SQL script `sql/create_inventory_db.sql` in your MySQL server (via MySQL Workbench or `mysql` CLI):
   ```bash
   mysql -u root -p < sql/create_inventory_db.sql
   ```
2. Modify `src/db/DBConnection.java` constants `USER` and `PASSWORD` to match your MySQL credentials.
3. Add MySQL Connector/J to your project classpath. In Maven add:
   ```xml
   <dependency>
     <groupId>mysql</groupId>
     <artifactId>mysql-connector-java</artifactId>
     <version>8.0.33</version>
   </dependency>
   ```
   Or download the jar and add it to the classpath.

## Running
### From IDE
- Import or create a new Java project using the `src` folder.
- Ensure the JDBC driver is on the classpath.
- Run `app.InventoryApp`.

### From Terminal
- Compile:
  ```bash
  javac -d out -cp "path/to/mysql-connector-java.jar" src/db/DBConnection.java src/model/*.java src/dao/*.java src/ui/*.java src/app/InventoryApp.java
  ```
- Run:
  ```bash
  java -cp "out:path/to/mysql-connector-java.jar" app.InventoryApp
  ```

## Notes & Tips
- The DB URL in `DBConnection` uses `serverTimezone=UTC` and `allowPublicKeyRetrieval=true`. Adjust as needed.
- Deleting a supplier that is referenced by a product will set the product's supplier_id to NULL (due to `ON DELETE SET NULL`).
- The UI runs on the Event Dispatch Thread (EDT) using `SwingUtilities.invokeLater`.

---
