
/***
 * File: Program4.java
 * Authors: Zachary Astrowsky, Steven George, Eduardo Hernandez, Vickram Sullhan
 * Description: A JDBC-based Java client-server application managing ski resort operations. It allows CRUD operations
 * on members, ski passes, equipment rentals, and lesson purchases. The program supports detailed logging
 * of equipment changes and rental history. It also executes predefined queries such as retrieving member
 * lesson details, ski pass activities (lift rides and rentals), available intermediate trails, and
 * equipment change logs.
 */

import java.sql.*;
import java.util.*;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;

public class Program4 {
    // Queries
    private static final String QUERY1_STRING = "SELECT LP.orderId, LP.totalSessions, LP.remainingSessions, LP.pricePerSession, "
            + "L.lessonId, L.ageType, L.lessonType, L.durationType, L.startTime, "
            + "E.firstName AS instructorFirstName, E.lastName AS instructorLastName "
            + "FROM LessonPurchase LP JOIN Lesson L ON LP.lessonId = L.lessonId "
            + "JOIN Instructor I ON L.instructorId = I.instructorId "
            + "JOIN Employee E ON I.instructorId = E.employeeId " + "JOIN Member M ON LP.memberId = M.memberId "
            + "WHERE M.firstName = ? AND M.lastName = ?";

    private static final String QUERY2_STRING = "SELECT * FROM ( " +
            "SELECT DISTINCT 'LIFT RIDE' AS SECTION, ll.passId AS REF_ID, ll.liftName AS DETAIL1, " +
            "TO_CHAR(ll.liftLogDate,'YYYY-MM-DD HH24:MI:SS') AS DETAIL2, NULL AS DETAIL3 " +
            "FROM LiftLog ll " +
            "UNION ALL " +
            "SELECT 'EQUIPMENT RENTAL' AS SECTION, r.passId AS REF_ID, e.eType||' '||e.eSize AS DETAIL1, " +
            "TO_CHAR(r.rentalDate,'YYYY-MM-DD HH24:MI:SS') AS DETAIL2, r.returnStatus  AS DETAIL3 " +
            "FROM Rental r JOIN Equipment e ON e.RID = r.RID " +
            ") WHERE REF_ID = ? " +
            "ORDER BY DETAIL2";

    private static final String QUERY3_STRING = "SELECT t.name AS trail_name, t.category, l.name AS lift_name "
            + "FROM Trail t JOIN Lift l ON t.name = l.name "
            + "WHERE t.difficulty = 'intermediate' AND t.status = 'open' AND l.status = 'open' ORDER BY t.name";

    private static final String QUERY4_STRING = "SELECT eq.EID as EquipmentID, eq.ETYPE as EquipmentType, eq.ESIZE as EquipmentSize, eq.ESTATUS as CurrentStatus, "
            + " log.OLDTYPE,  log.NEWTYPE, log.OLDSIZE, log.NEWSIZE, log.OLDSTATUS, log.NEWSTATUS, TO_CHAR(log.changeDate, 'YYYY-MM-DD HH24:MI:SS') AS ChangeDate "
            + "FROM EquipmentChangeLog log JOIN Equipment eq ON log.EID = eq.EID WHERE eq.EID = ? ORDER BY log.changeDate DESC";

    /**
     * Attempts to connect to the database using the given username and password.
     * If unable to connect, prints the error message and exits the program.
     * 
     * @param username the desired username
     * @param password the desired password
     * @return the connection or null if unable to connect
     */
    private static Connection getConnection(String username, String password) {
        final String oracleURL = "jdbc:oracle:thin:@aloe.cs.arizona.edu:1521:oracle";
        try {
            return DriverManager.getConnection(oracleURL, username, password);
        } catch (SQLException e) {
            System.err.println("*** SQLException:  " + e.getMessage());
            System.exit(-1);
        }
        return null;
    }

    /**
     * Inserts a new equipment item into the database with the given parameters.
     * 
     * @param conn    the connection to the database
     * @param rid     the rental id associated with the equipment item
     * @param eTpe    the type of the equipment item
     * @param eSize   the size of the equipment item
     * @param eStatus the status of the equipment item
     * @throws SQLException if a database error occurs
     */
    private static void insertEquipmentItem(Connection conn, int rid, String eTpe, String eSize, String eStatus)
            throws SQLException {
        String insertSQL = "INSERT INTO Equipment (RID, eType, eSize, eStatus) VALUES (?, ?, ?, ?)";
        try (PreparedStatement pstmt = conn.prepareStatement(insertSQL, new String[] { "EID" })) {
            if (rid != 0) {
                pstmt.setInt(1, rid);
            } else {
                pstmt.setNull(1, Types.INTEGER);
            }
            pstmt.setString(2, eTpe);
            pstmt.setString(3, eSize);
            pstmt.setString(4, eStatus);

            int rowsAffected = pstmt.executeUpdate();
            if (rowsAffected > 0) {
                try (ResultSet rs = pstmt.getGeneratedKeys()) {
                    if (rs.next()) {
                        System.out.println("Successfully inserted equipment. Equipment id -> " + rs.getInt(1));
                    }
                }
            } else {
                System.out.println("Failed to insert Equipment");
            }
        }
    }

    /**
     * Logs an equipment change into the EquipmentChangeLog table.
     *
     * @param conn      the connection to the database
     * @param eid       the equipment id of the item that was changed
     * @param oldType   the old type of the equipment item
     * @param newType   the new type of the equipment item
     * @param oldSize   the old size of the equipment item
     * @param newSize   the new size of the equipment item
     * @param oldStatus the old status of the equipment item
     * @param newStatus the new status of the equipment item
     * @throws SQLException if a database error occurs
     */
    private static void logEquipmentChange(Connection conn, int eid, String oldType,
            String newType, String oldSize, String newSize, String oldStatus, String newStatus) throws SQLException {
        String sql = "INSERT INTO EquipmentChangeLog " +
                "  (EID, oldType, newType, oldSize, newSize, oldStatus, newStatus) " +
                "VALUES (?,    ?,       ?,       ?,       ?,       ?,         ?      )";

        try (PreparedStatement ps = conn.prepareStatement(sql, new String[] { "EChangeId" })) {
            ps.setInt(1, eid);
            ps.setString(2, oldType);
            ps.setString(3, newType);
            ps.setString(4, oldSize);
            ps.setString(5, newSize);
            ps.setString(6, oldStatus);
            ps.setString(7, newStatus);
            ps.executeUpdate();
            try (ResultSet rs = ps.getGeneratedKeys()) {
                if (rs.next()) {
                    int changeId = rs.getInt(1);
                    System.out.println("Logged equipment change: EChangeId=" + changeId);
                }
            }
        }
    }

    /**
     * Updates an equipment item in the Equipment table by allowing the user to
     * change one of its attributes.
     * Logs the change in the EquipmentChangeLog table if the update is successful.
     *
     * @param conn the connection to the database
     * @param eid  the equipment id of the item to update
     * @throws SQLException if a database error occurs
     */
    private static void updateEquipmentItem(Connection conn, int eid, Scanner sc) throws SQLException {
        // SQL to select current equipment details
        String selectSql = "SELECT eType, eSize, eStatus FROM Equipment WHERE EID = ?";
        String oldType, oldSize, oldStatus;

        // Retrieve current details of the equipment
        try (PreparedStatement sel = conn.prepareStatement(selectSql)) {
            sel.setInt(1, eid);
            try (ResultSet rs = sel.executeQuery()) {
                if (!rs.next()) {
                    System.out.println("No equipment item found with the provided EID.");
                    return;
                }
                oldType = rs.getString("eType");
                oldSize = rs.getString("eSize");
                oldStatus = rs.getString("eStatus");
            }
        }

        System.out.println("Select attribute to update:");
        System.out.println("1. eStatus");
        System.out.println("2. eType");
        System.out.println("3. eSize");
        int choice = sc.nextInt();
        sc.nextLine(); // consume newline

        String updateSQL;
        String updateValue;
        String newType = oldType;
        String newSize = oldSize;
        String newStatus = oldStatus;

        // Determine the update query based on the user's choice
        switch (choice) {
            case 1 -> {
                System.out.print("Enter new eStatus: ");
                updateValue = sc.nextLine().trim();
                newStatus = updateValue;
                updateSQL = "UPDATE Equipment SET eStatus = ? WHERE EID = ?";
            }
            case 2 -> {
                System.out.print("Enter new eType: ");
                updateValue = sc.nextLine().trim();
                newType = updateValue;
                updateSQL = "UPDATE Equipment SET eType = ? WHERE EID = ?";
            }
            case 3 -> {
                System.out.print("Enter new eSize: ");
                updateValue = sc.nextLine().trim();
                newSize = updateValue;
                updateSQL = "UPDATE Equipment SET eSize = ? WHERE EID = ?";
            }
            default -> {
                System.out.println("Invalid choice.");
                return;
            }
        }

        // Start transaction
        conn.setAutoCommit(false);
        int rowsAffected;

        // Execute the update statement
        try (PreparedStatement pstmt = conn.prepareStatement(updateSQL)) {
            pstmt.setString(1, updateValue);
            pstmt.setInt(2, eid);
            rowsAffected = pstmt.executeUpdate();
        }

        // If update is successful, log the change and commit the transaction
        if (rowsAffected > 0) {
            logEquipmentChange(conn,
                    eid,
                    oldType, newType,
                    oldSize, newSize,
                    oldStatus, newStatus);
            conn.commit();
            System.out.println("Equipment item updated successfully.");
        } else {
            // Rollback if no rows were updated
            conn.rollback();
            System.out.println("No equipment item found with the provided EID.");
        }
        conn.setAutoCommit(true); // Reset auto-commit to true
    }

    /**
     * Archives an equipment item by updating its status to 'Archived'.
     * Logs the change in the EquipmentChangeLog table.
     *
     * @param conn the connection to the database
     * @param eid  the equipment id of the item to archive
     * @throws SQLException if a database error occurs
     */
    private static void archiveEquipmentItem(Connection conn, int eid) throws SQLException {
        // SQL for selecting current equipment details
        String selectSql = "SELECT eType, eSize, eStatus FROM Equipment WHERE EID = ?";
        String oldType = null;
        String oldSize = null;
        String oldStatus = null;

        // Retrieve current equipment details
        try (PreparedStatement sel = conn.prepareStatement(selectSql)) {
            sel.setInt(1, eid);
            try (ResultSet rs = sel.executeQuery()) {
                if (!rs.next())
                    throw new SQLException("Equipment not found for EID=" + eid);
                oldType = rs.getString("eType");
                oldSize = rs.getString("eSize");
                oldStatus = rs.getString("eStatus");
            }
        }

        // Check equipment status
        String statusCheckSQL = "SELECT eStatus FROM Equipment WHERE EID = ?";
        String eStatus = null;
        conn.setAutoCommit(false);
        try (PreparedStatement statusStmt = conn.prepareStatement(statusCheckSQL)) {
            statusStmt.setInt(1, eid);
            ResultSet rs = statusStmt.executeQuery();
            if (rs.next()) {
                eStatus = rs.getString("eStatus");
            } else {
                System.out.println("No equipment item found with the provided EID.");
                return;
            }
        }

        // Ensure the equipment is either retired or lost before archiving
        if (!eStatus.equalsIgnoreCase("retired") && !eStatus.equalsIgnoreCase("lost")) {
            System.out.println("Deletion not allowed: Equipment must be retired or lost.");
            return;
        }

        // Check if the equipment is currently rented
        String rentalCheckSQL = "SELECT COUNT(*) AS count FROM Rental WHERE RID = ? AND returnStatus IN ('Rented')";
        int activeCount = 0;
        try (PreparedStatement rentalStmt = conn.prepareStatement(rentalCheckSQL)) {
            rentalStmt.setInt(1, eid);
            ResultSet rs = rentalStmt.executeQuery();
            if (rs.next()) {
                activeCount = rs.getInt("count");
            }
        }

        // If equipment is rented, prevent archiving
        if (activeCount > 0) {
            System.out.println("Deletion not allowed: Equipment is currently rented.");
            return;
        }

        // Archive the equipment by updating its status
        String archiveSQL = "UPDATE Equipment SET eStatus = 'Archived' WHERE EID = ?";
        try (PreparedStatement archiveStmt = conn.prepareStatement(archiveSQL)) {
            archiveStmt.setInt(1, eid);
            int rowsAffected = archiveStmt.executeUpdate();
            System.out.println(rowsAffected > 0 ? "Equipment item archived successfully."
                    : "Failed to archive equipment item.");
            conn.commit();
            conn.setAutoCommit(true);
        }

        // Log the change in the equipment status
        String newStatus = "Archived";
        logEquipmentChange(conn, eid, oldType, oldType, oldSize, oldSize, oldStatus, newStatus);
    }

    /**
     * Adds a lesson purchase record to the database.
     * 
     * @param conn              The database connection object.
     * @param orderId           The ID of the order.
     * @param memberId          The ID of the member.
     * @param lessonId          The ID of the lesson.
     * @param totalSessions     The total number of sessions purchased.
     * @param remainingSessions The remaining number of sessions.
     * @param pricePerSession   The price per session.
     * @throws SQLException if a database access error occurs.
     */
    private static void addLessonPurchase(Connection conn, int orderId, int memberId, int lessonId, int totalSessions,
            int remainingSessions, double pricePerSession) throws SQLException {
        String insertSQL = "INSERT INTO LessonPurchase (orderId, memberId, lessonId, totalSessions, remainingSessions, pricePerSession) VALUES (?, ?, ?, ?, ?, ?)";
        try (PreparedStatement pstmt = conn.prepareStatement(insertSQL)) {
            pstmt.setInt(1, orderId);
            pstmt.setInt(2, memberId);
            pstmt.setInt(3, lessonId);
            pstmt.setInt(4, totalSessions);
            pstmt.setInt(5, remainingSessions);
            pstmt.setDouble(6, pricePerSession);
            int rowsAffected = pstmt.executeUpdate();
            System.out.println(
                    rowsAffected > 0 ? "Lesson purchase added successfully." : "Failed to add lesson purchase.");
        }
    }

    /**
     * Updates a lesson purchase record in the database.
     * 
     * @param conn              The database connection object.
     * @param orderId           The ID of the order.
     * @param remainingSessions The new remaining number of sessions.
     * @throws SQLException if a database access error occurs.
     */
    private static void updateLessonPurchase(Connection conn, int orderId, int remainingSessions) throws SQLException {
        String updateSQL = "UPDATE LessonPurchase SET remainingSessions = ? WHERE orderId = ?";
        try (PreparedStatement pstmt = conn.prepareStatement(updateSQL)) {
            pstmt.setInt(1, remainingSessions);
            pstmt.setInt(2, orderId);
            int rowsAffected = pstmt.executeUpdate();
            System.out.println(rowsAffected > 0 ? "Lesson purchase updated successfully."
                    : "No lesson purchase found with the provided orderId.");
        }
    }

    /**
     * Deletes a lesson purchase record from the database if it meets the criteria.
     *
     * This method attempts to delete a lesson purchase identified by the given
     * orderId. The deletion is allowed only if the remaining sessions are equal
     * to the total sessions or if the remaining sessions are zero. If the deletion
     * criteria are not met, it prompts for an admin override to force deletion.
     *
     * @param conn    The database connection object.
     * @param orderId The ID of the order to be deleted.
     * @param input   The Scanner object for reading user input during admin
     *                override.
     * @throws SQLException if a database access error occurs.
     */
    private static void deleteLessonPurchase(Connection conn, int orderId, Scanner input) throws SQLException {
        String deleteSQL = "DELETE FROM LessonPurchase WHERE orderId = ? AND (remainingSessions = totalSessions OR remainingSessions = 0)";
        try (PreparedStatement pstmt = conn.prepareStatement(deleteSQL)) {
            pstmt.setInt(1, orderId);
            int rowsAffected = pstmt.executeUpdate();
            if (rowsAffected > 0) {
                System.out.println("Lesson purchases deleted successfully");
            } else {
                System.out.println("No eligible lesson purchase found to delete (must have zero sessions used)."
                        + "\nAccount cannot be deleted. Admin Override? y/n");
                String val = input.nextLine();
                if (val.compareTo("y") == 0) {
                    try (PreparedStatement stmt = conn
                            .prepareStatement("Delete from lessonpurchase where orderid = ?")) {
                        stmt.setInt(1, orderId);
                        rowsAffected = stmt.executeUpdate();
                        System.out.println(rowsAffected > 0 ? "Lesson purchase deleted successfully."
                                : "No lesson purchase found with the provided orderId.");
                    }
                }

            }

        }
    }

    /**
     * Executes a query to retrieve lesson details for a member based on their first
     * and last name.
     *
     * Prompts the user for the member's first and last name, executes a predefined
     * SQL query,
     * and prints the details of each lesson purchase associated with the member. If
     * no lessons
     * are found, it informs the user accordingly.
     *
     * @param conn  the database connection
     * @param input the scanner to read user input
     * @throws SQLException if a database error occurs
     */
    private static void runQuery1(Connection conn, Scanner input) throws SQLException {
        System.out.print("Enter member first name: ");
        String firstName = input.nextLine().trim();

        System.out.print("Enter member last name: ");
        String lastName = input.nextLine().trim();

        System.out.println("\n=== Query 1: ===");
        try (PreparedStatement stmt = conn.prepareStatement(QUERY1_STRING)) {
            stmt.setString(1, firstName);
            stmt.setString(2, lastName);

            try (ResultSet rs = stmt.executeQuery()) {
                boolean hasResults = false;
                while (rs.next()) {
                    hasResults = true;
                    System.out.printf(
                            "OrderID: %d, Sessions: %d/%d, Price: $%.2f, LessonID: %d, Age: %s, Type: %s, Duration: %s, Time: %s, Instructor: %s %s\n",
                            rs.getInt("orderId"), rs.getInt("totalSessions"), rs.getInt("remainingSessions"),
                            rs.getDouble("pricePerSession"), rs.getInt("lessonId"), rs.getString("ageType"),
                            rs.getString("lessonType"), rs.getString("durationType"), rs.getString("startTime"),
                            rs.getString("instructorFirstName"), rs.getString("instructorLastName"));
                }
                if (!hasResults) {
                    System.out.println("No lessons found for this member.");
                }
            }
        }
    }

    /**
     * Retrieves the equipment change log for a given equipment id.
     *
     * Prints the log of changes to the equipment's type, size, and status.
     * Each line of the log includes the date of the change,
     * the old and new values of the equipment type, size, and status.
     *
     * @param conn the connection to the database
     * @param eId  the equipment id to retrieve the change log for
     * @throws SQLException if a database error occurs
     */
    private static void runQuery4(Connection conn, String eId) throws SQLException {
        try (PreparedStatement stmt = conn.prepareStatement(QUERY4_STRING)) {
            stmt.setString(1, eId);
            try (ResultSet rs = stmt.executeQuery()) {
                System.out.printf("\nEquipment Change Log For Equipment ID %s\n", eId);
                while (rs.next()) {

                    System.out.printf("Changed on %-20s: %-10s, %-10s -> %-10s, %-6s->%-6s, %-10s ->%-10s\n",
                            rs.getString("CHANGEDATE"), rs.getString("EQUIPMENTTYPE"), rs.getString("OLDTYPE"),
                            rs.getString("NEWTYPE"), rs.getString("OLDSIZE"), rs.getString("NEWSIZE"),
                            rs.getString("OLDSTATUS"), rs.getString("NEWSTATUS")

                    );

                }
            }
        }
    }

    /**
     * Adds a new member to the database.
     * 
     * Prompts the user to enter details for the new member, including first name,
     * last name, phone number, email, date of birth, and emergency contact
     * information. Each field is validated for proper format and constraints.
     * 
     * If any validation fails or the user input is invalid, the operation is
     * aborted. The member details are inserted into the database and the new
     * member ID is displayed upon successful insertion.
     * 
     * @param conn  The database connection object.
     * @param input The Scanner object for reading user input.
     * @throws SQLException If a database access error occurs.
     */
    private static void addMember(Connection conn, Scanner input) throws SQLException {
        String addMemberString = "INSERT INTO member ( " + "FIRSTNAME, LASTNAME, PHONE, EMAIL, DOB, "
                + "EMGCONTACTFNAME, EMGCONTACTLNAME, EMGCONTACTPHONE ) "
                + " VALUES (?, ?, ?, ?, TO_DATE(?, 'YYYY-MM-DD'), ?, ?, ? )";

        String firstName = promptAndValidate(input, "First Name:");
        if (firstName == null)
            return;

        String lastName = promptAndValidate(input, "Last Name:");
        if (lastName == null)
            return;

        String phone = promptAndValidate(input, "Phone (XXX-XXX-XXXX):");
        if (phone == null)
            return;

        String email = promptAndValidate(input, "Email:");
        if (email == null)
            return;

        String dob = promptAndValidate(input, "Date Of Birth (YYYY-MM-DD):");
        if (dob == null)
            return;
        try {
            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");
            LocalDate birthday = LocalDate.parse(dob, formatter);
            LocalDate now = LocalDate.now();
            LocalDate oldest = now.minusYears(200);
            if (birthday.isAfter(now) || birthday.isBefore(oldest)) {
                System.out.println("Invalid birthday\nPress enter to continue");
                input.nextLine();
                return;
            }
        } catch (DateTimeParseException e) {
            System.out.println("Invalid birthday\nPress enter to continue");
            input.nextLine();
            return;
        }

        String emFirstName = promptAndValidate(input, "Emergency Contact First Name:");
        if (emFirstName == null)
            return;

        String emLastName = promptAndValidate(input, "Emergency Contact Last Name:");
        if (emLastName == null)
            return;

        String emPhone = promptAndValidate(input, "Emergency Contact Phone (XXX-XXX-XXXX):");
        if (emPhone == null)
            return;

        try (PreparedStatement stmt = conn.prepareStatement(addMemberString, new String[] { "memberId" })) {
            stmt.setString(1, firstName);
            stmt.setString(2, lastName);
            stmt.setString(3, phone);
            stmt.setString(4, email);
            stmt.setString(5, dob);
            stmt.setString(6, emFirstName);
            stmt.setString(7, emLastName);
            stmt.setString(8, emPhone);
            int rowsAffected = stmt.executeUpdate();
            if (rowsAffected > 0) {
                try (ResultSet rs = stmt.getGeneratedKeys()) {
                    if (rs.next()) {
                        System.out.println("Successfully generated new member. Member id -> " + rs.getInt(1));
                    }
                }
            } else {
                System.out.println("Failed to add member");
            }
        }
        System.out.println("Press enter to continue");
        input.nextLine();

    }

    /**
     * Prompts the user to enter a value and validates the input.
     *
     * Prints the given prompt to the console and reads the user's input.
     * If the input is null or empty, prints an error message and returns null.
     * Otherwise, returns the input.
     *
     * @param input  the scanner object to read input from
     * @param prompt the prompt to display to the user
     * @return the validated input, or null if the input is invalid
     */
    private static String promptAndValidate(Scanner input, String prompt) {
        System.out.println(prompt);
        String val = input.nextLine();
        if (val == null || val.trim().isEmpty()) {
            System.out.println("Input is null or empty.");
            System.out.println("Press enter to continue");
            input.nextLine();
            return null;
        }
        return val;
    }

    /**
     * Checks if a member exists in the database using the given member ID.
     *
     * Executes a query to determine if a record with the specified member ID
     * exists in the Member table.
     *
     * @param conn     the database connection object.
     * @param memberId the member ID to check for existence.
     * @return true if the member exists, false otherwise.
     * @throws SQLException if a database access error occurs.
     */
    private static boolean memberExists(Connection conn, int memberId) throws SQLException {
        String memberCheckSql = "SELECT 1 FROM Member WHERE MemberId = ?";
        try (PreparedStatement stmt = conn.prepareStatement(memberCheckSql)) {
            stmt.setInt(1, memberId);
            try (ResultSet rs = stmt.executeQuery()) {
                return rs.next();
            }
        }

    }

    /**
     * Checks if a ski pass exists in the database.
     * 
     * @param conn   The database connection object.
     * @param passId The pass ID to check.
     * @return true if the pass exists, false otherwise.
     * @throws SQLException if a database access error occurs.
     */
    private static boolean passExists(Connection conn, int passId) throws SQLException {
        String passCheckSql = "SELECT 1 FROM skipass WHERE passid = ?";
        try (PreparedStatement stmt = conn.prepareStatement(passCheckSql)) {
            stmt.setInt(1, passId);
            try (ResultSet rs = stmt.executeQuery()) {
                return rs.next();
            }
        }

    }

    /**
     * Updates a member record in the database. The user is prompted to enter
     * the member ID, and if the member exists, the user is asked to choose a
     * field to update. The user is then prompted to enter the new value. If the
     * field is date of birth, the new value must be in the format "YYYY-MM-DD"
     * and must be between 200 years ago and today. If the field is phone, the
     * new value must be in the format "XXX-XXX-XXXX". If the field is email,
     * the new value must be a valid email address. If the field is emergency
     * contact name, the user is asked to enter both the first and last name.
     * 
     * @param conn  The database connection object.
     * @param input The Scanner object for reading user input.
     * @throws SQLException if a database access error occurs.
     */
    private static void updateMember(Connection conn, Scanner input) throws SQLException {
        System.out.println("Enter member id to update");
        String memberIdString = input.nextLine();
        int memberId;
        try {
            memberId = Integer.parseInt(memberIdString);
        } catch (NumberFormatException e) {
            System.out.println("Invalid member id. Must enter a number");
            return;
        }

        if (!memberExists(conn, memberId)) {
            System.out.println("Invalid member id. Member does not exist");
        } else {

            System.out.println("Which field would you like to edit?");
            System.out.println(
                    "1. First Name \n2. Last Name \n3. Phone Number \n4. Email \n5. Date of Birth \n6. Emergency Contact Name \n7. Emergency Contact Phone \n8. Exit");
            System.out.println("Enter a number (1-8)");
            String choice = input.nextLine();
            String toPrint = "Hope this is initialized";
            String field = null;
            String newVal1;
            String newVal2;
            int inputCase;
            switch (choice) {
                case "1":
                    toPrint = "Enter updated first name: ";
                    inputCase = 1;
                    field = "FIRSTNAME";
                    break;
                case "2":
                    toPrint = "Enter updated last name: ";
                    inputCase = 1;
                    field = "LASTNAME";
                    break;
                case "3":
                    toPrint = "Enter updated phone number (XXX-XXX-XXXX): ";
                    inputCase = 1;
                    field = "PHONE";
                    break;
                case "4":
                    toPrint = "Enter updated email: ";
                    inputCase = 1;
                    field = "EMAIL";
                    break;
                case "5":
                    inputCase = 2;
                    break;
                case "6":
                    inputCase = 3;
                    break;
                case "7":
                    toPrint = "Enter updated phone: ";
                    inputCase = 1;
                    field = "EMGCONTACTPHONE";
                    break;
                case "8":
                    return;
                default:
                    System.out.println("Invalid choice. Press enter to continue");
                    input.nextLine();
                    return;
            }

            String editMemberSql;

            if (inputCase == 1) {
                System.out.println(toPrint);
                newVal1 = input.nextLine();
                editMemberSql = "UPDATE Member SET " + field + "= ? WHERE MemberId = ?";
                try (PreparedStatement stmt = conn.prepareStatement(editMemberSql)) {
                    stmt.setString(1, newVal1);
                    stmt.setInt(2, memberId);
                    int rows = stmt.executeUpdate();
                    System.out.println(rows > 0 ? "Member updated successfully." : "Update failed.");

                }
            } else if (inputCase == 2) {
                System.out.println("Enter the updated date of birth (YYYY-MM-DD): ");
                newVal1 = input.nextLine();
                if (newVal1 == null)
                    return;
                try {
                    DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");
                    LocalDate birthday = LocalDate.parse(newVal1, formatter);
                    LocalDate now = LocalDate.now();
                    LocalDate oldest = now.minusYears(200);
                    if (birthday.isAfter(now) || birthday.isBefore(oldest)) {
                        System.out.println("Invalid birthday\nPress enter to continue");
                        input.nextLine();
                        return;
                    }
                } catch (DateTimeParseException e) {
                    System.out.println("Invalid birthday\nPress enter to continue");
                    input.nextLine();
                    return;
                }
                editMemberSql = "UPDATE Member SET dob = TO_DATE(?, 'YYYY-MM-DD') WHERE MemberId = ?";
                try (PreparedStatement stmt = conn.prepareStatement(editMemberSql)) {
                    stmt.setString(1, newVal1);
                    stmt.setInt(2, memberId);
                    int rows = stmt.executeUpdate();
                    System.out.println(rows > 0 ? "Member updated successfully." : "Update failed.");

                }
            } else if (inputCase == 3) {
                System.out.println("Enter the updated emergency contact first name ");
                newVal1 = input.nextLine();
                System.out.println("Enter the updated emergency contact last name ");
                newVal2 = input.nextLine();
                editMemberSql = "UPDATE Member SET emgContactFName = ?, emgContactLName = ? WHERE MemberId = ?";
                try (PreparedStatement stmt = conn.prepareStatement(editMemberSql)) {
                    stmt.setString(1, newVal1);
                    stmt.setString(2, newVal2);
                    stmt.setInt(3, memberId);
                    int rows = stmt.executeUpdate();
                    System.out.println(rows > 0 ? "Member updated successfully." : "Update failed.");

                }
            }
        }
    }

    /**
     * Deletes a member from the database.
     *
     * The user is first prompted to enter the member id of the member to delete.
     * If the member does not exist, an error message is displayed.
     * If the member exists, the program checks if the member has any active ski
     * passes, active lesson purchases, or
     * active rentals. If any of these are found, an error message is displayed and
     * the deletion is not performed.
     * If no active ski passes, active lesson purchases, or active rentals are
     * found, the program asks the user to
     * confirm that they want to delete the account. If the user enters 'y', the
     * account is deleted. If the user
     * enters 'n', the deletion is cancelled.
     *
     * @param conn  the database connection
     * @param input the Scanner object for reading user input
     * @throws SQLException if a database error occurs
     */
    private static void deleteMember(Connection conn, Scanner input) throws SQLException {
        System.out.println("Enter member id to delete");
        String memberIdString = input.nextLine();
        int memberId;
        try {
            memberId = Integer.parseInt(memberIdString);
        } catch (NumberFormatException e) {
            System.out.println("Invalid member id. Must enter a number");
            return;
        }
        if (!memberExists(conn, memberId)) {
            System.out.println("Invalid member id. Member does not exist");
        } else {

            boolean unableToReturn = false;

            if (hasAny(conn, "SELECT passid FROM skipass WHERE memberId = " + memberId, "PASSID",
                    "Active ski pass(es) present. Please delete or use them before deleting account\n. Warning, you may not delete your account if you have used a lesson")) {
                unableToReturn = true;
            }

            if (hasAny(conn,
                    "SELECT orderid FROM lessonpurchase WHERE memberId = " + memberId + " AND remainingSessions > 0",
                    "ORDERID",
                    "Active lesson purchase(s) present. Please delete or use them before deleting account")) {
                unableToReturn = true;
            }

            if (hasAny(conn,
                    "SELECT rid FROM Rental r JOIN SKIPASS s ON r.passid = s.passid WHERE s.memberId = " + memberId
                            + " AND r.returnStatus = 'Rented'",
                    "RID", "Active rental(s) present. Please delete or use them before deleting account")) {
                unableToReturn = true;
            }

            if (unableToReturn) {
                System.out.println("Press enter to continue");
                input.nextLine();
            } else {
                System.out.println("Are you sure you want to delete the account. This action is final");
                System.out.println("Press 'y' to permanently delete the account");
                System.out.println("Press 'n' to cancel");
                String selection = input.nextLine();
                if (selection.compareTo("y") == 0) {
                    String deleteMemberSql = "delete from member where memberid = ?";
                    try (PreparedStatement stmt = conn.prepareStatement(deleteMemberSql)) {
                        stmt.setInt(1, memberId);
                        int rows = stmt.executeUpdate();
                        System.out.println(rows > 0 ? "Membership successfully deleted" : "Deletion failed.");
                    }
                }
            }
        }

    }

    /**
     * Adds a new ski pass for a member into the SkiPass table.
     * 
     * Prompts the user to enter the member ID, expiration date, and select a pass
     * type.
     * Validates the member ID and pass type. Inserts the new ski pass record into
     * the database.
     * 
     * @param conn  the database connection to use
     * @param input the scanner to read user input
     * @throws SQLException if a database error occurs
     */
    private static void addSkiPass(Connection conn, Scanner input) throws SQLException {

        System.out.println("Member ID:");
        String memberIdString = input.nextLine();

        int memberId;
        try {
            memberId = Integer.parseInt(memberIdString);
        } catch (NumberFormatException e) {
            System.out.println("Invalid member id. Must enter a number");
            return;
        }

        if (!memberExists(conn, memberId)) {
            System.out.println("Invalid member id. Member does not exist");
            return;
        }

        System.out.println("Enter expiration date (YYYY-MM-DD):");
        String expDate = input.nextLine();

        System.out.print("Select pass type (A. 1 day, B. 2 day, C. 4 day, D. season)");
        System.out.println("Enter A-D:");
        String passType = input.nextLine();
        switch (passType) {
            case "A":
                passType = "1 day";
                break;
            case "B":
                passType = "2 day";
                break;
            case "C":
                passType = "4 day";
                break;
            case "D":
                passType = "season";
                break;
            default:
                System.out.println("Invalid pass type. Press enter to continue");
                input.nextLine();
                return;
        }

        String sql = "INSERT INTO SkiPass (memberId, purchaseDate, expirationDate, totalUses, type) "
                + " VALUES (?, SYSDATE, TO_DATE(?, 'YYYY-MM-DD'), ?, ?)";
        try (PreparedStatement stmt = conn.prepareStatement(sql, new String[] { "passId" })) {
            stmt.setInt(1, memberId);
            stmt.setString(2, expDate);
            stmt.setInt(3, 0);
            stmt.setString(4, passType);
            int rowsAffected = stmt.executeUpdate();
            if (rowsAffected > 0) {
                try (ResultSet rs = stmt.getGeneratedKeys()) {
                    if (rs.next()) {
                        System.out.println("Successfully generated new pass. Pass id -> " + rs.getInt(1));
                    }
                }
            } else {
                System.out.println("Failed to add new ski pass");
            }
        }
    }

    /**
     * Updates the total uses of a ski pass identified by the given pass ID.
     * Prompts the user to enter the updated number of total uses.
     * Validates the pass ID and ensures it exists in the database.
     * Prints a message indicating the success or failure of the update operation.
     *
     * @param conn  the connection to the database
     * @param input the scanner used to read the pass ID and new total uses from the
     *              user
     * @throws SQLException if a database error occurs
     */
    private static void updateSkiPass(Connection conn, Scanner input) throws SQLException {

        System.out.println("Ski Pass Id:");
        String passIdString = input.nextLine();

        int passId;
        try {
            passId = Integer.parseInt(passIdString);
        } catch (NumberFormatException e) {
            System.out.println("Invalid pass id. Must enter a number");
            return;
        }

        if (!passExists(conn, passId)) {
            System.out.println("Invalid pass id. Pass does not exist");
            return;
        }

        String getTotalUses = "SELECT totalUses FROM SkiPass WHERE passId = ?";
        try (PreparedStatement stmt = conn.prepareStatement(getTotalUses)) {
            stmt.setInt(1, passId);
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    int uses = rs.getInt("totalUses");
                    System.out.println("Total uses for pass " + passId + ": " + uses);
                }
            }
        }

        System.out.println("Enter the updated number of total uses:");
        String newTotalString = input.nextLine();

        int newTotal;
        try {
            newTotal = Integer.parseInt(newTotalString);
        } catch (NumberFormatException e) {
            System.out.println("Invalid total. Must enter a number >= 0");
            return;
        }

        String updateTotalUses = "UPDATE skipass SET totaluses = ? WHERE passId = ?";
        try (PreparedStatement stmt = conn.prepareStatement(updateTotalUses)) {
            stmt.setInt(1, newTotal);
            stmt.setInt(2, passId);

            int rows = stmt.executeUpdate();
            if (rows > 0) {
                System.out.println("Total uses updated successfully.");
            } else {
                System.out.println("Could not update total uses.");
            }
        }
    }

    /**
     * Deletes a ski pass by its pass id. Will only delete the pass if it has
     * expired or has no remaining uses. If the pass has remaining uses,
     * the user will be prompted to confirm deletion. If the pass has expired,
     * the user will be prompted to confirm deletion. If the pass has expired
     * and has remaining uses, the user will be prompted to confirm deletion.
     * The user will also be prompted to confirm if the pass has been refunded.
     * If the pass has been refunded, the user will be prompted to confirm
     * deletion. If the pass has not been refunded, the user will not be
     * prompted to confirm deletion.
     * 
     * @param conn  the database connection
     * @param input the user's input
     * @throws SQLException if a database error occurs
     */
    private static void deleteSkiPass(Connection conn, Scanner input) throws SQLException {

        System.out.println("Ski Pass Id:");
        String passIdString = input.nextLine();

        int passId;
        try {
            passId = Integer.parseInt(passIdString);
        } catch (NumberFormatException e) {
            System.out.println("Invalid pass id. Must enter a number");
            return;
        }

        if (!passExists(conn, passId)) {
            System.out.println("Invalid pass id. Pass does not exist");
            return;
        }

        String getPasses = "SELECT * FROM SkiPass WHERE passId = ?";
        try (PreparedStatement stmt = conn.prepareStatement(getPasses)) {
            stmt.setInt(1, passId);
            try (ResultSet rs = stmt.executeQuery()) {
                if (!rs.next()) {
                    System.out.println("Ski pass cannot be deleted. Total uses not zero");
                }
                // rs.next();
                int memberId = rs.getInt("memberId");
                java.sql.Date purchaseDate = rs.getDate("purchaseDate");
                java.sql.Date expirationDate = rs.getDate("expirationDate");
                System.out.println(expirationDate);
                int totalUses = rs.getInt("totalUses");
                String type = rs.getString("type");
                int remainingUsesInt = 0;
                String remainingUses = null;
                switch (type) {
                    case "1 day":
                        remainingUsesInt = 1 - totalUses;
                        if (remainingUsesInt < 0) {
                            remainingUsesInt = 0;
                        }
                        remainingUses = Integer.toString(remainingUsesInt);

                        break;
                    case "2 day":
                        remainingUsesInt = 2 - totalUses;
                        if (remainingUsesInt < 0) {
                            remainingUsesInt = 0;
                        }
                        remainingUses = Integer.toString(remainingUsesInt);
                        break;
                    case "4 day":
                        remainingUsesInt = 3 - totalUses;
                        if (remainingUsesInt < 0) {
                            remainingUsesInt = 0;
                        }
                        remainingUses = Integer.toString(remainingUsesInt);
                        break;
                    case "season":
                        remainingUses = "inf";
                        break;
                }

                String archiveSql = "INSERT INTO ArchivedPass ( passId, memberId, purchaseDate, expirationDate, "
                        + "totalUses, type, archiveDate, archiveReason) VALUES (?, ?, ?, ?, ?, ?, SYSDATE, ?)";
                if (expirationDate.before(java.sql.Date.valueOf(LocalDate.now()))) {
                    System.out.println("Pass is expired");
                    remainingUses = "0";
                }
                // System.out.println("Remaining uses is " + remainingUses);
                if (remainingUses.compareTo("0") == 0 || remainingUses.compareTo("inf") == 0) {
                    if (!expirationDate.before(java.sql.Date.valueOf(LocalDate.now()))) {
                        System.out.println("Pass has not expired and cannot be deleted");
                        // return;
                    } else {
                        System.out.println("Pass can be deleted. Are you sure you want to delete the pass?");
                        if (remainingUses.compareTo("inf") == 0) {
                            System.out.println("This is a season pass and can be used until the end of the season");
                            System.out.println("Deletion is permanent");
                        }
                        System.out.println("Enter 'y' to delete the pass, else enter anything");
                        String selection = input.nextLine();
                        if (selection.compareTo("y") == 0) {

                            try (PreparedStatement archiveStmt = conn.prepareStatement(archiveSql)) {
                                archiveStmt.setInt(1, passId);
                                archiveStmt.setInt(2, memberId);
                                archiveStmt.setDate(3, purchaseDate);
                                archiveStmt.setDate(4, expirationDate);
                                archiveStmt.setInt(5, totalUses);
                                archiveStmt.setString(6, type);
                                archiveStmt.setString(7, "Deleted");
                                archiveStmt.executeUpdate();
                                System.out.println("Ski pass archived.");
                            }

                            String deleteSql = " Delete from skipass where passid = ? ";
                            try (PreparedStatement deleteStmt = conn.prepareStatement(deleteSql)) {
                                deleteStmt.setInt(1, passId);
                                int rows = deleteStmt.executeUpdate();

                                if (rows > 0) {
                                    System.out.println("Ski pass deleted successfully.");
                                } else {
                                    System.out.println("Ski pass deletion failed.");
                                }
                            }

                            return;
                        } else {
                            System.out.println("Pass not deleted");
                            return;
                        }

                    }
                } else {
                    System.out.println("Pass has days remaining and cannot be deleted");
                }

                System.out.println("Has the pass been refunded? y/n");
                String refundInput = input.nextLine();
                if (refundInput.compareTo("y") == 0) {
                    System.out.println("Press 'y' again to delete the pass");
                    System.out.println("Press 'n' to cancel");
                    refundInput = input.nextLine();
                    if (refundInput.compareTo("y") == 0) {

                        try (PreparedStatement archiveStmt = conn.prepareStatement(archiveSql)) {
                            archiveStmt.setInt(1, passId);
                            archiveStmt.setInt(2, memberId);
                            archiveStmt.setDate(3, purchaseDate);
                            archiveStmt.setDate(4, expirationDate);
                            archiveStmt.setInt(5, totalUses);
                            archiveStmt.setString(6, type);
                            archiveStmt.setString(7, "Expired");
                            archiveStmt.executeUpdate();
                            System.out.println("Ski pass archived.");
                        }

                        String deleteSql = " Delete from skipass where passid = ? ";
                        try (PreparedStatement deleteStmt = conn.prepareStatement(deleteSql)) {
                            deleteStmt.setInt(1, passId);
                            int rows = deleteStmt.executeUpdate();

                            if (rows > 0) {
                                System.out.println("Ski pass deleted successfully.");
                            } else {
                                System.out.println("Ski pass deletion failed.");
                            }
                        }
                    } else {
                        System.out.println("Pass not deleted");
                    }
                }
            }
        }
    }

    /**
     * Runs a query, and if there are any results, prints out the message and the
     * values of the given fieldName
     * for each row of the results, and returns true. If there are no results,
     * returns false.
     * 
     * @param conn      the database connection to use
     * @param sql       the query to run
     * @param fieldName the name of the field to print out
     * @param message   the message to print out if there are any results
     * @return true if there were any results, false if there were no results
     * @throws SQLException if there is a problem with the database
     */
    private static boolean hasAny(Connection conn, String sql, String fieldName, String message) throws SQLException {
        try (PreparedStatement stmt = conn.prepareStatement(sql)) {
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    System.out.println("\n\n" + message);
                    do {
                        System.out.println(fieldName + ": " + rs.getString(fieldName));
                    } while (rs.next());
                    return true;
                } else {
                    return false;
                }
            }
        }

    }

    /**
     * Creates a new rental record for the given ski pass ID and equipment ID. If
     * the pass ID is invalid, the pass has expired, or the equipment ID is
     * invalid, the rental record is not created and an appropriate error message
     * is displayed. If the equipment is not available for rental, an error
     * message is displayed. If the rental record is created successfully, the
     * rental ID is displayed. The equipment record is updated to link it to this
     * rental, and the change is logged. If any database operation fails, the
     * transaction is rolled back and an error message is displayed.
     * 
     * @param conn  The database connection object.
     * @param input The Scanner object for reading user input.
     * @throws SQLException If a database access error occurs.
     */
    private static void addEquipmentRental(Connection conn, Scanner input) throws SQLException {
        // SQL for selecting current equipment details
        String selectSql = "SELECT eType, eSize, eStatus FROM Equipment WHERE EID = ?";
        String oldType = null;
        String oldSize = null;
        String oldStatus = null;

        System.out.println("Enter Ski Pass ID:");
        String passIdString = input.nextLine();

        int passId;
        try {
            passId = Integer.parseInt(passIdString);
        } catch (NumberFormatException e) {
            System.out.println("Invalid pass ID. Must enter a number.");
            return;
        }

        // Validate that the pass exists and is valid
        String checkPassSQL = "SELECT 1 FROM SkiPass WHERE passId = ? AND expirationDate >= SYSDATE";
        try (PreparedStatement checkStmt = conn.prepareStatement(checkPassSQL)) {
            checkStmt.setInt(1, passId);
            ResultSet rs = checkStmt.executeQuery();
            if (!rs.next()) {
                System.out.println("Error: The ski pass is not valid, has expired, or does not exist.");
                return;
            }
        }

        System.out.println("Enter Equipment ID:");
        String equipmentIdString = input.nextLine();

        int equipmentId;
        try {
            equipmentId = Integer.parseInt(equipmentIdString);
        } catch (NumberFormatException e) {
            System.out.println("Invalid equipment ID. Must enter a number.");
            return;
        }

        // Check if the equipment is available
        String checkEquipmentSQL = "SELECT eStatus FROM Equipment WHERE EID = ?";
        try (PreparedStatement checkStmt = conn.prepareStatement(checkEquipmentSQL)) {
            checkStmt.setInt(1, equipmentId);
            ResultSet rs = checkStmt.executeQuery();
            if (!rs.next()) {
                System.out.println("Error: Equipment not found.");
                return;
            }

            String status = rs.getString("eStatus");
            if (!status.equalsIgnoreCase("Available")) {
                System.out.println("Error: The equipment is not available for rental. Current status: " + status);
                return;
            }
        }

        // Retrieve current equipment details
        try (PreparedStatement sel = conn.prepareStatement(selectSql)) {
            sel.setInt(1, equipmentId);
            try (ResultSet rs = sel.executeQuery()) {
                if (!rs.next())
                    throw new SQLException("Equipment not found for EID=" + equipmentId);
                oldType = rs.getString("eType");
                oldSize = rs.getString("eSize");
                oldStatus = rs.getString("eStatus");
            }
        }

        conn.setAutoCommit(false);
        try {
            // Create the rental record
            String insertRentalSQL = "INSERT INTO Rental (passId, rentalDate, returnStatus) VALUES (?, SYSDATE, 'Rented')";

            PreparedStatement pstmt = conn.prepareStatement(insertRentalSQL, new String[] { "RID" });
            pstmt.setInt(1, passId);
            pstmt.executeUpdate();

            // Get the generated rental ID
            ResultSet rs = pstmt.getGeneratedKeys();
            int rentalId = -1;
            if (rs.next()) {
                rentalId = rs.getInt(1);

                // Update the equipment record to link it to this rental
                String updateEquipmentSQL = "UPDATE Equipment SET RID = ?, eStatus = 'Rented' WHERE EID = ?";
                PreparedStatement equipPstmt = conn.prepareStatement(updateEquipmentSQL);
                equipPstmt.setInt(1, rentalId);
                equipPstmt.setInt(2, equipmentId);
                int updated = equipPstmt.executeUpdate();

                if (updated == 0) {
                    System.out.println("Failed to update equipment status.");
                    conn.rollback();
                    return;
                }

                equipPstmt.close();

                System.out.println("Rental record created successfully. Rental ID: " + rentalId);
            } else {
                System.out.println("Failed to create rental record - no ID was generated.");
                conn.rollback();
                return;
            }

            rs.close();
            pstmt.close();
            conn.commit();

        } catch (SQLException e) {
            conn.rollback();
            System.err.println("Error creating rental record: " + e.getMessage());
            throw e;
        } finally {
            conn.setAutoCommit(true);
        }
        // Log the change in the equipment status
        String newStatus = "Rented";
        logEquipmentChange(conn, equipmentId, oldType, oldType, oldSize, oldSize, oldStatus, newStatus);
    }

    /**
     * Updates a rental record with the given rental ID to indicate that the
     * equipment has been returned.
     * Updates the associated equipment record to indicate that it is available for
     * rental again.
     * Logs the change in the RentalChangeLog table.
     *
     * @param conn  the connection to the database
     * @param input the scanner used to read the rental ID from the user
     * @throws SQLException if a database error occurs
     */
    private static void updateRentalReturn(Connection conn, Scanner input) throws SQLException {
        // SQL for selecting current equipment details
        String selectSql = "SELECT eType, eSize, eStatus FROM Equipment WHERE RID = ?";
        String oldType = null;
        String oldSize = null;
        String oldStatus = null;
        int equipmentId = 0;

        System.out.println("Enter Rental ID to update:");
        String rentalIdString = input.nextLine();

        int rentalId;
        try {
            rentalId = Integer.parseInt(rentalIdString);
        } catch (NumberFormatException e) {
            System.out.println("Invalid rental ID. Must enter a number.");
            return;
        }

        // Check if rental exists and get current status
        String checkRentalSQL = "SELECT returnStatus FROM Rental WHERE RID = ?";
        String currentStatus = null;

        try (PreparedStatement checkStmt = conn.prepareStatement(checkRentalSQL)) {
            checkStmt.setInt(1, rentalId);
            ResultSet rs = checkStmt.executeQuery();

            if (!rs.next()) {
                System.out.println("Error: Rental record not found.");
                return;
            }

            currentStatus = rs.getString("returnStatus");
            if ("Returned".equalsIgnoreCase(currentStatus)) {
                System.out.println("This equipment has already been returned.");
                return;
            }
        }

        conn.setAutoCommit(false);
        try {
            // Find the equipment associated with this rental first
            String findEquipmentSQL = "SELECT EID FROM Equipment WHERE RID = ?";
            PreparedStatement findPstmt = conn.prepareStatement(findEquipmentSQL);
            findPstmt.setInt(1, rentalId);
            ResultSet rs = findPstmt.executeQuery();

            if (rs.next()) {
                equipmentId = rs.getInt("EID");

                // Retrieve current equipment details
                PreparedStatement sel = conn.prepareStatement(selectSql);
                sel.setInt(1, rentalId); // Use rental ID to find equipment
                ResultSet ers = sel.executeQuery();
                if (ers.next()) {
                    oldType = ers.getString("eType");
                    oldSize = ers.getString("eSize");
                    oldStatus = ers.getString("eStatus");

                    // Update equipment status first
                    String updateEquipmentSQL = "UPDATE Equipment SET RID = NULL, eStatus = 'Available' WHERE RID = ?";
                    PreparedStatement equipPstmt = conn.prepareStatement(updateEquipmentSQL);
                    equipPstmt.setInt(1, rentalId);
                    int equipUpdated = equipPstmt.executeUpdate();

                    if (equipUpdated == 0) {
                        System.out.println("Failed to update equipment status.");
                        conn.rollback();
                        return;
                    }

                    equipPstmt.close();
                }
                ers.close();
                sel.close();
            }
            rs.close();
            findPstmt.close();

            // Now update the rental status
            String updateRentalSQL = "UPDATE Rental SET returnStatus = 'Available' WHERE RID = ?";
            PreparedStatement pstmt = conn.prepareStatement(updateRentalSQL);
            pstmt.setInt(1, rentalId);
            int rowsAffected = pstmt.executeUpdate();

            if (rowsAffected > 0) {
                // Log the update
                String logUpdateSQL = "INSERT INTO RentalChangeLog (RID, action, rentalChangeDate) VALUES (?, 'Update', SYSDATE)";
                PreparedStatement logPstmt = conn.prepareStatement(logUpdateSQL);
                logPstmt.setInt(1, rentalId);
                logPstmt.executeUpdate();
                logPstmt.close();

                System.out.println("Rental record updated successfully. Equipment returned.");
                conn.commit();
            } else {
                System.out.println("Failed to update rental record.");
                conn.rollback();
                return;
            }

            pstmt.close();

        } catch (SQLException e) {
            conn.rollback();
            System.err.println("Error updating rental record: " + e.getMessage());
            throw e;
        } finally {
            conn.setAutoCommit(true);
        }

        if (equipmentId != 0 && oldStatus != null && oldType != null && oldSize != null) {
            String newStatus = "Available";
            logEquipmentChange(conn, equipmentId, oldType, oldType, oldSize, oldSize, oldStatus, newStatus);
        }
    }

    /**
     * Deletes a rental record identified by the given rental ID.
     * Ensures the rental record can only be deleted if the associated equipment
     * has not been returned or used. Confirms the deletion action with the user.
     * Updates the associated equipment record to indicate it is available for
     * rental again. Logs the deletion in the RentalChangeLog and updates the
     * equipment status change in the EquipmentChangeLog.
     *
     * @param conn  The database connection object.
     * @param input The Scanner object for reading user input.
     * @throws SQLException If a database access error occurs.
     */
    private static void deleteRentalRecord(Connection conn, Scanner input) throws SQLException {
        // SQL for selecting current equipment details
        String selectSql = "SELECT eType, eSize, eStatus FROM Equipment WHERE EID = ?";
        String oldType = null;
        String oldSize = null;
        String oldStatus = null;
        int equipmentId = 0;

        System.out.println("Enter Rental ID to delete:");
        String rentalIdString = input.nextLine();

        int rentalId;
        try {
            rentalId = Integer.parseInt(rentalIdString);
        } catch (NumberFormatException e) {
            System.out.println("Invalid rental ID. Must enter a number.");
            return;
        }

        // Check if rental exists and verify it can be deleted
        String checkRentalSQL = "SELECT returnStatus FROM Rental WHERE RID = ?";
        String currentStatus = null;

        try (PreparedStatement checkStmt = conn.prepareStatement(checkRentalSQL)) {
            checkStmt.setInt(1, rentalId);
            ResultSet rs = checkStmt.executeQuery();

            if (!rs.next()) {
                System.out.println("Error: Rental record not found.");
                return;
            }

            currentStatus = rs.getString("returnStatus");
            if (!"Rented".equalsIgnoreCase(currentStatus)) {
                System.out.println(
                        "Error: Cannot delete rental record as the equipment has already been used or returned.");
                return;
            }
        }

        System.out.println("This will delete a rental record. Are you sure? (y/n)");
        String confirmation = input.nextLine().trim().toLowerCase();
        if (!confirmation.equals("y")) {
            System.out.println("Deletion cancelled.");
            return;
        }

        conn.setAutoCommit(false);
        try {
            // Find the equipment associated with this rental
            String findEquipmentSQL = "SELECT EID FROM Equipment WHERE RID = ?";
            PreparedStatement findPstmt = conn.prepareStatement(findEquipmentSQL);
            findPstmt.setInt(1, rentalId);
            ResultSet rs = findPstmt.executeQuery();

            if (rs.next()) {
                equipmentId = rs.getInt("EID");

                // Retrieve current equipment details
                try (PreparedStatement sel = conn.prepareStatement(selectSql)) {
                    sel.setInt(1, equipmentId);
                    try (ResultSet ers = sel.executeQuery()) {
                        if (!ers.next())
                            throw new SQLException("Equipment not found for EID=" + equipmentId);
                        oldType = ers.getString("eType");
                        oldSize = ers.getString("eSize");
                        oldStatus = ers.getString("eStatus");
                    }
                }

                // Update equipment status to make it available again
                String updateEquipmentSQL = "UPDATE Equipment SET RID = NULL, eStatus = 'Available' WHERE EID = ?";
                PreparedStatement equipPstmt = conn.prepareStatement(updateEquipmentSQL);
                equipPstmt.setInt(1, equipmentId);
                int equipUpdated = equipPstmt.executeUpdate();

                if (equipUpdated == 0) {
                    System.out.println("Failed to update equipment status.");
                    conn.rollback();
                    return;
                }

                equipPstmt.close();
            }
            rs.close();
            findPstmt.close();

            // Log the deletion
            String logDeleteSQL = "INSERT INTO RentalChangeLog (RID, action, rentalChangeDate) VALUES (?, 'Delete', SYSDATE)";
            PreparedStatement logPstmt = conn.prepareStatement(logDeleteSQL);
            logPstmt.setInt(1, rentalId);
            logPstmt.executeUpdate();
            logPstmt.close();

            // Delete the rental record
            String deleteRentalSQL = "DELETE FROM Rental WHERE RID = ?";
            PreparedStatement pstmt = conn.prepareStatement(deleteRentalSQL);
            pstmt.setInt(1, rentalId);
            int rowsAffected = pstmt.executeUpdate();

            if (rowsAffected > 0) {
                System.out.println("Rental record deleted successfully.");
            } else {
                System.out.println("Failed to delete rental record.");
                conn.rollback();
                return;
            }

            pstmt.close();
            conn.commit();

        } catch (SQLException e) {
            conn.rollback();
            System.err.println("Error deleting rental record: " + e.getMessage());
            throw e;
        } finally {
            conn.setAutoCommit(true);
        }
        if (equipmentId != 0 && oldStatus != null && oldType != null && oldSize != null) {
            // Log the change in the equipment status
            String newStatus = "Available";
            logEquipmentChange(conn, equipmentId, oldType, oldType, oldSize, oldSize, oldStatus, newStatus);
        }
    }

    /**
     * This is the main method of the Program4 class.
     * It provides a menu to the user to either edit the database or run sample
     * queries.
     * If the user chooses to edit the database, they will be presented with a menu
     * with 16 options.
     * If the user chooses to run sample queries, they will be presented with a menu
     * with 5 options.
     * If the user enters an invalid option, they will be prompted to enter a valid
     * option.
     * The program will continue to run until the user chooses to exit.
     */
    public static void main(String[] args) {
        String username = "eduardoh12";
        String password = "a3769";

        try {
            Class.forName("oracle.jdbc.OracleDriver");
        } catch (ClassNotFoundException e) {
            System.err.println("""
                    *** ClassNotFoundException:  Error loading Oracle JDBC driver.
                    \tPerhaps the driver is not on the Classpath?
                    Try this:
                    export CLASSPATH=/usr/lib/oracle/19.8/client64/lib/ojdbc8.jar:${CLASSPATH}
                    """);
            System.exit(-1);
        }
        try (Connection conn = getConnection(username, password); Scanner input = new Scanner(System.in)) {
            System.out.print("Do you want to edit the database? (y/n): ");
            String editChoice = input.nextLine().trim().toLowerCase();
            if (editChoice.equals("y")) {
                boolean exit = false;
                while (!exit) {
                    displayDBMenu();
                    String choice = input.nextLine();
                    if (choice.equals("16")) {
                        System.out.println("Goodbye!");
                        exit = true;
                    } else {
                        handleDBQueries(conn, input, choice);
                    }
                }
            } else if (editChoice.equals("n")) {
                boolean exit = false;
                while (!exit) {
                    displaySampleQueries();
                    String queryChoice = input.nextLine();
                    if (queryChoice.equals("5")) {
                        System.out.println("Goodbye!");
                        exit = true;
                    } else {
                        handleSampleQueries(conn, input, queryChoice);
                    }
                }
            } else {
                System.out.println("Invalid input. Exiting program.");
            }
        } catch (SQLException e) {
            System.err.println("SQL Error: " + e.getMessage());
        }
    }

    // Main helpers

    /**
     * Displays the database management menu to the user.
     * Provides options for managing lesson purchases, members, ski passes,
     * equipment, and rentals.
     */
    private static void displayDBMenu() {
        // Display menu header
        System.out.println("\nLesson Purchase Management");

        // Display options for lesson purchase management
        System.out.println("1. Add Lesson Purchase");
        System.out.println("2. Update Lesson Purchase");
        System.out.println("3. Delete Lesson Purchase");

        // Display options for member management
        System.out.println("4. Add Member");
        System.out.println("5. Update Member");
        System.out.println("6. Delete Member");

        // Display options for ski pass management
        System.out.println("7. Add Ski Pass");
        System.out.println("8. Update Ski Pass");
        System.out.println("9. Delete Ski Pass");

        // Display options for equipment management
        System.out.println("10. Add Equipment");
        System.out.println("11. Update Equipment");
        System.out.println("12. Delete Equipment");

        // Display options for equipment rental management
        System.out.println("13. Add Equipment Rental");
        System.out.println("14. Update Equipment Rental");
        System.out.println("15. Delete Equipment Rental");

        // Display exit option
        System.out.println("16. Exit");

        // Prompt user to select an option
        System.out.print("Select an option: ");
    }

    /**
     * Handles various database queries based on user input.
     *
     * This method provides an interface to manage lesson purchases, members, ski
     * passes,
     * equipment, and rentals within the database. It processes user choices and
     * executes
     * the corresponding database operations.
     *
     * @param conn   The database connection object.
     * @param input  The Scanner object for reading user input.
     * @param choice The user-selected option determining which operation to
     *               perform.
     * @throws SQLException if a database access error occurs.
     */
    private static void handleDBQueries(Connection conn, Scanner input, String choice) throws SQLException {
        switch (choice) {
            case "1":
                System.out.print("Enter Order ID: ");
                int orderId = Integer.parseInt(input.nextLine());
                System.out.print("Enter Member ID: ");
                int memberId = Integer.parseInt(input.nextLine());
                System.out.print("Enter Lesson ID: ");
                int lessonId = Integer.parseInt(input.nextLine());
                System.out.print("Enter Total Sessions: ");
                int totalSessions = Integer.parseInt(input.nextLine());
                System.out.print("Enter Remaining Sessions: ");
                int remainingSessions = Integer.parseInt(input.nextLine());
                System.out.print("Enter Price per Session: ");
                double pricePerSession = Double.parseDouble(input.nextLine());
                addLessonPurchase(conn, orderId, memberId, lessonId, totalSessions, remainingSessions,
                        pricePerSession);
                break;
            case "2":
                System.out.print("Enter Order ID to update: ");
                int updateOrderId = Integer.parseInt(input.nextLine());
                System.out.print("Enter new Remaining Sessions: ");
                int newRemainingSessions = Integer.parseInt(input.nextLine());
                updateLessonPurchase(conn, updateOrderId, newRemainingSessions);
                break;
            case "3":
                System.out.print("Enter Order ID to delete: ");
                int deleteOrderId = Integer.parseInt(input.nextLine());
                deleteLessonPurchase(conn, deleteOrderId, input);
                break;
            case "4":
                addMember(conn, input);
                break;
            case "5":
                updateMember(conn, input);
                break;
            case "6":
                deleteMember(conn, input);
                break;
            case "7":
                addSkiPass(conn, input);
                break;
            case "8":
                updateSkiPass(conn, input);
                break;
            case "9":
                deleteSkiPass(conn, input);
                break;
            case "10":
                System.out.println("Please enter the following information:");
                System.out.println("RID, eType, eSize, eStatus [enter 0 for null RID]");
                String equipment = input.nextLine();
                String[] parts = equipment.split(",");
                int rid = Integer.parseInt(parts[0].trim());
                String etype = parts[1].trim();
                String esize = parts[2].trim();
                String estatus = parts[3].trim();
                insertEquipmentItem(conn, rid, etype, esize, estatus);
                break;
            case "11":
                System.out.println("Enter Equipment ID to update:");
                int eidUpdate = Integer.parseInt(input.nextLine().trim());
                updateEquipmentItem(conn, eidUpdate, input);
                break;
            case "12":
                System.out.println("Please enter the following information:");
                System.out.println("EID");
                String equipmentDelete = input.nextLine();
                String[] partsDelete = equipmentDelete.split(",");
                int eidDelete = Integer.parseInt(partsDelete[0].trim());
                archiveEquipmentItem(conn, eidDelete);
                break;
            case "13":
                addEquipmentRental(conn, input);
                break;
            case "14":
                updateRentalReturn(conn, input);
                break;
            case "15":
                deleteRentalRecord(conn, input);
                break;
            default:
                System.out.println("Invalid choice. Try again.");
        }
    }

    /**
     * Display the query menu and prompt the user to enter their choice.
     */
    private static void displaySampleQueries() {
        System.out.println("\nQuery Menu");
        System.out.println("1. Query 1 - Member Ski Lessons");
        System.out.println("2. Query 2 - Ski Pass Details");
        System.out.println("3. Query 3 - Intermediate Trails and Lifts");
        System.out.println("3. Query 4 - Equipment Change Log");
        System.out.println("5. Exit");
        System.out.print("Enter your choice: ");
    }

    /**
     * Handles the sample queries in the menu by executing the corresponding
     * SQL statement and displaying the results.
     *
     * @param conn   the database connection
     * @param input  the scanner to read user input
     * @param choice the choice number of the query to execute
     * @throws SQLException if a database error occurs
     */
    private static void handleSampleQueries(Connection conn, Scanner input, String choice) throws SQLException {
        switch (choice) {
            case "1":
                runQuery1(conn, input);
                break;
            case "2":
                System.out.print("Enter passId: ");
                String passId = input.nextLine();
                try (PreparedStatement stmt = conn.prepareStatement(QUERY2_STRING)) {
                    stmt.setString(1, passId);
                    try (ResultSet rs = stmt.executeQuery()) {
                        while (rs.next()) {
                            System.out.printf("[%s] ID: %s | %s | %s | %s%n",
                                    rs.getString("SECTION"),
                                    rs.getString("REF_ID"),
                                    rs.getString("DETAIL1"),
                                    rs.getString("DETAIL2"),
                                    rs.getString("DETAIL3"));
                        }
                    }
                }
                break;
            case "3":
                try (PreparedStatement stmt = conn.prepareStatement(QUERY3_STRING);
                        ResultSet rs = stmt.executeQuery()) {
                    while (rs.next()) {
                        System.out.printf("Trail: %s, Category: %s, Lift: %s%n",
                                rs.getString("trail_name"), rs.getString("category"),
                                rs.getString("lift_name"));
                    }
                }
                break;
            case "4":
                System.out.print("Enter equipment id: ");
                String eId = input.nextLine();
                try {
                    Integer.parseInt(eId);
                } catch (NumberFormatException e) {
                    System.out.println("Invalid equipment id");
                    return;
                }
                runQuery4(conn, eId);
                break;
            default:
                System.out.println("Invalid choice.");
        }
    }
}
