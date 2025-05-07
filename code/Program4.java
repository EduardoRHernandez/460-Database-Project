import java.sql.*;
import java.util.*;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;

public class Program4 {
    private static final String QUERY1_STRING = "SELECT LP.orderId, LP.totalSessions, LP.remainingSessions, LP.pricePerSession, "
            +
            "L.lessonId, L.ageType, L.lessonType, L.durationType, L.startTime, " +
            "E.firstName AS instructorFirstName, E.lastName AS instructorLastName " +
            "FROM LessonPurchase LP JOIN Lesson L ON LP.lessonId = L.lessonId " +
            "JOIN Instructor I ON L.instructorId = I.instructorId " +
            "JOIN Employee E ON I.instructorId = E.employeeId " +
            "JOIN Member M ON LP.memberId = M.memberId " +
            "WHERE M.firstName = ? AND M.lastName = ?";

    private static final String QUERY2_STRING = "SELECT * FROM ( " +
            "SELECT DISTINCT 'LIFT RIDE'      AS SECTION, ll.passId AS REF_ID, ll.liftName      AS DETAIL1, " +
            "TO_CHAR(ll.liftLogDate,'YYYY-MM-DD HH24:MI:SS') AS DETAIL2, NULL            AS DETAIL3 " +
            "FROM LiftLog ll " +
            "UNION ALL " +
            "SELECT 'EQUIPMENT RENTAL' AS SECTION, r.passId     AS REF_ID, e.eType||' '||e.eSize AS DETAIL1, " +
            "TO_CHAR(r.rentalDate,'YYYY-MM-DD HH24:MI:SS')        AS DETAIL2, r.returnStatus  AS DETAIL3 " +
            "FROM Rental r JOIN Equipment e ON e.RID = r.RID " +
            ") WHERE REF_ID = ? " +
            "ORDER BY DETAIL2";

    private static final String QUERY3_STRING = "SELECT t.name AS trail_name, t.category, l.name AS lift_name " +
            "FROM Trail t JOIN Lift l ON t.name = l.name " +
            "WHERE t.difficulty = 'intermediate' AND t.status = 'open' AND l.status = 'open' ORDER BY t.name";

    private static final String QUERY4_STRING = "SELECT eq.EID as EquipmentID, eq.ETYPE as EquipmentType, eq.ESIZE as EquipmentSize, eq.ESTATUS as CurrentStatus, "
            +
            " log.OLDTYPE,  log.NEWTYPE, log.OLDSIZE, log.NEWSIZE, log.OLDSTATUS, log.NEWSTATUS, TO_CHAR(log.changeDate, 'YYYY-MM-DD HH24:MI:SS') AS ChangeDate "
            +
            "FROM EquipmentChangeLog log JOIN Equipment eq ON log.EID = eq.EID WHERE eq.EID = ? ORDER BY log.changeDate DESC";

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

    private static void updateEquipmentItem(Connection conn, int eid) throws SQLException {
        String selectSql = "SELECT eType, eSize, eStatus FROM Equipment WHERE EID = ?";
        String oldType, oldSize, oldStatus;
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

        Scanner sc = new Scanner(System.in);
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

        conn.setAutoCommit(false);
        int rowsAffected;
        try (PreparedStatement pstmt = conn.prepareStatement(updateSQL)) {
            pstmt.setString(1, updateValue);
            pstmt.setInt(2, eid);
            rowsAffected = pstmt.executeUpdate();
        }

        if (rowsAffected > 0) {
            logEquipmentChange(conn,
                    eid,
                    oldType, newType,
                    oldSize, newSize,
                    oldStatus, newStatus);
            conn.commit();
            System.out.println("Equipment item updated successfully.");
        } else {
            conn.rollback();
            System.out.println("No equipment item found with the provided EID.");
        }
        conn.setAutoCommit(true);
    }

    private static void archiveEquipmentItem(Connection conn, int eid) throws SQLException {
        String selectSql = "SELECT eType, eSize, eStatus FROM Equipment WHERE EID = ?";
        String oldType, oldSize, oldStatus;
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

        if (!eStatus.equalsIgnoreCase("retired") && !eStatus.equalsIgnoreCase("lost")) {
            System.out.println("Deletion not allowed: Equipment must be retired or lost.");
            return;
        }

        String rentalCheckSQL = "SELECT COUNT(*) AS count FROM Rental WHERE RID = ? AND returnStatus IN ('Rented')";
        int activeCount = 0;

        try (PreparedStatement rentalStmt = conn.prepareStatement(rentalCheckSQL)) {
            rentalStmt.setInt(1, eid);
            ResultSet rs = rentalStmt.executeQuery();
            if (rs.next()) {
                activeCount = rs.getInt("count");
            }
        }

        if (activeCount > 0) {
            System.out.println("Deletion not allowed: Equipment is currently rented.");
            return;
        }

        String archiveSQL = "UPDATE Equipment SET eStatus = 'Archived' WHERE EID = ?";

        try (PreparedStatement archiveStmt = conn.prepareStatement(archiveSQL)) {
            archiveStmt.setInt(1, eid);
            int rowsAffected = archiveStmt.executeUpdate();
            System.out.println(rowsAffected > 0 ? "Equipment item archived successfully."
                    : "Failed to archive equipment item.");
            conn.commit();
            conn.setAutoCommit(true);
        }
        String newStatus = "Archived";
        logEquipmentChange(conn, eid, oldType, oldType, oldSize, oldSize, oldStatus, newStatus);
    }

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

    private static void deleteLessonPurchase(Connection conn, int orderId) throws SQLException {
        String deleteSQL = "DELETE FROM LessonPurchase WHERE orderId = ? AND remainingSessions = totalSessions";
        try (PreparedStatement pstmt = conn.prepareStatement(deleteSQL)) {
            pstmt.setInt(1, orderId);
            int rowsAffected = pstmt.executeUpdate();
            System.out.println(rowsAffected > 0 ? "Lesson purchase deleted successfully."
                    : "No eligible lesson purchase found to delete (must have zero sessions used).");
        }
    }

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

    private static void runQuery4(Connection conn, String eId) throws SQLException {
        try (PreparedStatement stmt = conn.prepareStatement(QUERY4_STRING)) {
            stmt.setString(1, eId);
            try (ResultSet rs = stmt.executeQuery()) {
                System.out.printf("\nEquipment Change Log For Equipment ID %s\n", eId);
                while (rs.next()) {

                    System.out.printf("Changed on %-20s: %-10s, %-10s -> %-10s, %-6s->%-6s, %-10s ->%-10s\n",
                            rs.getString("CHANGEDATE"),
                            rs.getString("EQUIPMENTTYPE"),
                            rs.getString("OLDTYPE"),
                            rs.getString("NEWTYPE"),
                            rs.getString("OLDSIZE"),
                            rs.getString("NEWSIZE"),
                            rs.getString("OLDSTATUS"),
                            rs.getString("NEWSTATUS")

                    );

                }
            }
        }
    }

        private static void addMember(Connection conn, Scanner input) throws SQLException {
        String add_member_string = "INSERT INTO member ( " +
                "FIRSTNAME, LASTNAME, PHONE, EMAIL, DOB, " +
                "EMGCONTACTFNAME, EMGCONTACTLNAME, EMGCONTACTPHONE ) " +
                " VALUES (?, ?, ?, ?, TO_DATE(?, 'YYYY-MM-DD'), ?, ?, ? )";

        String firstName = promptAndValidate(input, "First Name:");
        if (firstName == null) return;

        String lastName = promptAndValidate(input, "Last Name:");
        if (lastName == null) return;

        String phone = promptAndValidate(input, "Phone (XXX-XXX-XXXX):");
        if (phone == null) return;

        String email = promptAndValidate(input, "Email:");
        if (email == null) return;

        String dob = promptAndValidate(input, "Date Of Birth (YYYY-MM-DD):");
        if (dob == null) return;
        try{
            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");
            LocalDate birthday = LocalDate.parse(dob, formatter);
            LocalDate now = LocalDate.now();
            LocalDate oldest = now.minusYears(200);
            if (birthday.isAfter(now) || birthday.isBefore(oldest)){
                System.out.println("Invalid birthday\nPress enter to continue");
                input.nextLine();
                return;
            }
        } catch (DateTimeParseException e){
            System.out.println("Invalid birthday\nPress enter to continue");
            input.nextLine();
            return;
        }

        String emFirstName = promptAndValidate(input, "Emergency Contact First Name:");
        if (emFirstName == null) return;

        String emLastName = promptAndValidate(input, "Emergency Contact Last Name:");
        if (emLastName == null) return;

        String emPhone = promptAndValidate(input, "Emergency Contact Phone (XXX-XXX-XXXX):");
        if (emPhone == null) return;


        try (PreparedStatement stmt = conn.prepareStatement(add_member_string, new String[] { "memberId" })) {
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
            // System.out.println(rowsAffected > 0 ? "Member added successfully." : "Failed
            // to add member.");
        }
        System.out.println("Press enter to continue");
        input.nextLine();

    }

    private static String promptAndValidate(Scanner input, String prompt){
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

    private static boolean memberExists(Connection conn, int memberId) throws SQLException {
        String memberCheckSql = "SELECT 1 FROM Member WHERE MemberId = ?";
        try (PreparedStatement stmt = conn.prepareStatement(memberCheckSql)) {
            stmt.setInt(1, memberId);
            try (ResultSet rs = stmt.executeQuery()) {
                return rs.next();
            }
        }

    }

    private static boolean passExists(Connection conn, int passId) throws SQLException {
        String passCheckSql = "SELECT 1 FROM skipass WHERE passid = ?";
        try (PreparedStatement stmt = conn.prepareStatement(passCheckSql)) {
            stmt.setInt(1, passId);
            try (ResultSet rs = stmt.executeQuery()) {
                return rs.next();
            }
        }

    }

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
            return;
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

            String editMemberSql; // = "UPDATE Member SET " + field + " = ? WHERE MemberId = ?";

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
                if (newVal1 == null) return;
                try{
                    DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");
                    LocalDate birthday = LocalDate.parse(newVal1, formatter);
                    LocalDate now = LocalDate.now();
                    LocalDate oldest = now.minusYears(200);
                    if (birthday.isAfter(now) || birthday.isBefore(oldest)){
                        System.out.println("Invalid birthday\nPress enter to continue");
                        input.nextLine();
                        return;
                    }
                } catch (DateTimeParseException e){
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
            return;
        } else {

            boolean unableToReturn = false;

            if (hasAny(conn,
                    "SELECT passid FROM skipass WHERE memberId = " + memberId , //+ " AND EXPIRATIONDATE >= SYSDATE",
                    "PASSID",
                    "Active ski pass(es) present. Please delete or use them before deleting account")) {
                // System.out.println("Active ski pass(es) present. Please delete or use them before deleting account");
                unableToReturn = true;
            }

            if (hasAny(conn,
                    "SELECT orderid FROM lessonpurchase WHERE memberId = " + memberId + " AND remainingSessions > 0",
                    "ORDERID",
                    "Active lesson purchase(s) present. Please delete or use them before deleting account")) {
                // System.out.println(
                        // "Active lesson purchase(s) present. Please delete or use them before deleting account");
                unableToReturn = true;
            }

            if (hasAny(conn,
                    "SELECT rid FROM Rental r JOIN SKIPASS s ON r.passid = s.passid WHERE s.memberId = " + memberId
                            + " AND r.returnStatus = 'Rented'",
                    "RID",
                    "Active rental(s) present. Please delete or use them before deleting account")) {
                // System.out.println("Active rental(s) present. Please delete or use them before deleting account");
                unableToReturn = true;
            }

            if (unableToReturn) {
                System.out.println("Press enter to continue");
                input.nextLine();
                return;

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
                } else {
                    return;
                }
            }

            // TEST CODE ONLY. DELETE THIS
            // String query = "SELECT * FROM Member";
            // try (Statement stmt = conn.createStatement(); ResultSet rs =
            // stmt.executeQuery(query)) {
            // ResultSetMetaData meta = rs.getMetaData();
            // int columnCount = meta.getColumnCount();

            // System.out.println("\n=== Member Table ===");

            // while (rs.next()) {
            // System.out.println("-------------------------------");
            // for (int i = 1; i <= columnCount; i++) {
            // String colName = meta.getColumnName(i);
            // String value = rs.getString(i);
            // System.out.printf("%-20s: %s%n", colName, value);
            // }
            // }

            // System.out.println("-------------------------------");
            // }
        }

    }

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

        String sql = "INSERT INTO SkiPass (memberId, purchaseDate, expirationDate, totalUses, type) " +
                " VALUES (?, SYSDATE, TO_DATE(?, 'YYYY-MM-DD'), ?, ?)";
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
            // System.out.println(rowsAffected > 0 ? "Member added successfully." : "Failed
            // to add member.");
        }

    }

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

        // TEST CODE ONLY. DELETE THIS
        // String query = "SELECT * FROM skipass";
        // try (Statement stmt = conn.createStatement(); ResultSet rs = stmt.executeQuery(query)) {
        //     ResultSetMetaData meta = rs.getMetaData();
        //     int columnCount = meta.getColumnCount();

        //     System.out.println("\n=== SkiPass Table ===");

        //     while (rs.next()) {
        //         System.out.println("-------------------------------");
        //         for (int i = 1; i <= columnCount; i++) {
        //             String colName = meta.getColumnName(i);
        //             String value = rs.getString(i);
        //             System.out.printf("%-20s: %s%n", colName, value);
        //         }
        //     }

        //     System.out.println("-------------------------------");
        // }

    }

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
                        if (remainingUsesInt < 0){
                            remainingUsesInt = 0;
                        }
                        remainingUses = Integer.toString(remainingUsesInt);

                        break;
                    case "2 day":
                        remainingUsesInt = 2 - totalUses;
                        if (remainingUsesInt < 0){
                            remainingUsesInt = 0;
                        }
                        remainingUses = Integer.toString(remainingUsesInt);
                        break;
                    case "4 day":
                        remainingUsesInt = 3 - totalUses;
                        if (remainingUsesInt < 0){
                            remainingUsesInt = 0;
                        }
                        remainingUses = Integer.toString(remainingUsesInt);
                        break;
                    case "season":
                        remainingUses = "inf";
                        break;
                }

                String archiveSql = "INSERT INTO ArchivedPass ( passId, memberId, purchaseDate, expirationDate, "
                                    +
                                    "totalUses, type, archiveDate, archiveReason) VALUES (?, ?, ?, ?, ?, ?, SYSDATE, ?)";
                if (expirationDate.before(java.sql.Date.valueOf(LocalDate.now()))){
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
                        if (remainingUses.compareTo("inf") == 0){
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

                            return;
                        } else {
                            System.out.println("Pass not deleted");
                            return;
                        }

                    }
                } else{
                    System.out.println("Pass has days remaining and cannot be deleted");
                }
                
                    
                    System.out.println("Has the pass been refunded? y/n");
                    String refundInput = input.nextLine();
                    if (refundInput.compareTo("y") == 0){
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
                    return;
                

                // if (totalUses == 0){
                // if (!expirationDate.before(java.sql.Date.valueOf(LocalDate.now()))){
                // System.out.println("Pass has not expired and cannot be deleted");
                // return;
                // }
                // else{
                // System.out.println("Pass can be deleted. Are you sure you want to delete the
                // pass?");

                // }

                // } else{
                // System.out.println("Pass has days remaining and cannot be deleted");
                // return;
                // }

            }

        }

        // String getDate = "SELECT totalUses FROM SkiPass WHERE passId = ?";
        // try (PreparedStatement stmt = conn.prepareStatement(getTotalUses)) {
        // stmt.setInt(1, passId);
        // try (ResultSet rs = stmt.executeQuery()) {
        // if (rs.next()) {
        // int uses = rs.getInt("totalUses");
        // System.out.println("Total uses for pass " + passId + ": " + uses);
        // }
        // }
        // }

        // System.out.println("Enter the updated number of total uses:");
        // String newTotalString = input.nextLine();

        // int newTotal;
        // try {
        // newTotal = Integer.parseInt(newTotalString);
        // } catch (NumberFormatException e){
        // System.out.println("Invalid total. Must enter a number >= 0");
        // return;
        // }

        // String updateTotalUses = "UPDATE skipass SET totaluses = ? WHERE passId = ?";
        // try (PreparedStatement stmt = conn.prepareStatement(updateTotalUses)) {
        // stmt.setInt(1, newTotal);
        // stmt.setInt(2, passId);

        // int rows = stmt.executeUpdate();
        // if (rows > 0) {
        // System.out.println("Total uses updated successfully.");
        // } else {
        // System.out.println("Could not update total uses.");
        // }
        // }

        // // TEST CODE ONLY. DELETE THIS
        // String query = "SELECT * FROM skipass";
        // try (Statement stmt = conn.createStatement(); ResultSet rs =
        // stmt.executeQuery(query)) {
        // ResultSetMetaData meta = rs.getMetaData();
        // int columnCount = meta.getColumnCount();

        // System.out.println("\n=== SkiPass Table ===");

        // while (rs.next()) {
        // System.out.println("-------------------------------");
        // for (int i = 1; i <= columnCount; i++) {
        // String colName = meta.getColumnName(i);
        // String value = rs.getString(i);
        // System.out.printf("%-20s: %s%n", colName, value);
        // }
        // }

        // System.out.println("-------------------------------");
        // }

    }

    private static boolean hasAny(Connection conn, String sql, String fieldName, String message) throws SQLException {
        try (PreparedStatement stmt = conn.prepareStatement(sql)) {
            try (ResultSet rs = stmt.executeQuery()) {
                // return rs.next();
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



    private static void addEquipmentRental(Connection conn, Scanner input) throws SQLException {
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
    }

    private static void updateRentalReturn(Connection conn, Scanner input) throws SQLException {
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
            // Update the rental status
            String updateRentalSQL = "UPDATE Rental SET returnStatus = 'Returned' WHERE RID = ?";
            PreparedStatement pstmt = conn.prepareStatement(updateRentalSQL);
            pstmt.setInt(1, rentalId);
            int rowsAffected = pstmt.executeUpdate();

            if (rowsAffected > 0) {
                // Find the equipment associated with this rental
                String findEquipmentSQL = "SELECT EID FROM Equipment WHERE RID = ?";
                PreparedStatement findPstmt = conn.prepareStatement(findEquipmentSQL);
                findPstmt.setInt(1, rentalId);
                ResultSet rs = findPstmt.executeQuery();

                if (rs.next()) {
                    int equipmentId = rs.getInt("EID");

                    // Update equipment status
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

                // Log the update
                String logUpdateSQL = "INSERT INTO RentalChangeLog (RID, action, date) VALUES (?, 'Update', SYSDATE)";
                PreparedStatement logPstmt = conn.prepareStatement(logUpdateSQL);
                logPstmt.setInt(1, rentalId);
                logPstmt.executeUpdate();
                logPstmt.close();

                System.out.println("Rental record updated successfully. Equipment returned.");
            } else {
                System.out.println("Failed to update rental record.");
                conn.rollback();
                return;
            }

            pstmt.close();
            conn.commit();

        } catch (SQLException e) {
            conn.rollback();
            System.err.println("Error updating rental record: " + e.getMessage());
            throw e;
        } finally {
            conn.setAutoCommit(true);
        }
    }

    private static void deleteRentalRecord(Connection conn, Scanner input) throws SQLException {
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
                int equipmentId = rs.getInt("EID");

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
    }

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
                while (true) {
                    System.out.println("\nLesson Purchase Management");
                    System.out.println("1. Add Lesson Purchase");
                    System.out.println("2. Update Lesson Purchase");
                    System.out.println("3. Delete Lesson Purchase");
                    System.out.println("4. Add Member");
                    System.out.println("5. Update Member");
                    System.out.println("6. Delete Member");
                    System.out.println("7. Add Ski Pass");
                    System.out.println("8. Update Ski Pass");
                    System.out.println("9. Delete Ski Pass");
                    System.out.println("10. Add Equipment");
                    System.out.println("11. Update Equipment");
                    System.out.println("12. Delete Equipment");
                    System.out.println("13. Add Equipment Rental");
                    System.out.println("14. Update Equipment Rental (Return)");
                    System.out.println("15. Delete Equipment Rental");
                    System.out.println("16. Exit");
                    System.out.print("Select an option: ");

                    String choice = input.nextLine();
                    switch (choice) {
                        case "1":
                            try{
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
                            } catch (NumberFormatException e) {
                                System.out.println("Invalid input");
                                return;
                            }
                            break;
                        case "2":
                            try{
                            System.out.print("Enter Order ID to update: ");
                            int updateOrderId = Integer.parseInt(input.nextLine());
                            System.out.print("Enter new Remaining Sessions: ");
                            int newRemainingSessions = Integer.parseInt(input.nextLine());
                            updateLessonPurchase(conn, updateOrderId, newRemainingSessions);
                            } catch (NumberFormatException e) {
                                System.out.println("Invalid input");
                                return;
                            }
                            break;
                        case "3":
                            try{
                            System.out.print("Enter Order ID to delete: ");
                            int deleteOrderId = Integer.parseInt(input.nextLine());
                            deleteLessonPurchase(conn, deleteOrderId);
                            } catch (NumberFormatException e) {
                                System.out.println("Invalid input");
                                return;
                            }
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
                            updateEquipmentItem(conn, eidUpdate);
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
                        case "16":
                            System.out.println("Goodbye!");
                            return;
                        default:
                            System.out.println("Invalid choice. Try again.");
                    }
                }
            } else if (editChoice.equals("n")) {
                while (true) {
                    System.out.println("\nQuery Menu");
                    System.out.println("1. Query 1 - Member Ski Lessons");
                    System.out.println("2. Query 2 - Ski Pass Details");
                    System.out.println("3. Query 3 - Intermediate Trails and Lifts");
                    System.out.println("3. Query 4 - Equipment Change Log");
                    System.out.println("5. Exit");
                    System.out.print("Enter your choice: ");
                    String queryChoice = input.nextLine();

                    switch (queryChoice) {
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
                        case "5":
                            System.out.println("Goodbye!");
                            return;
                        default:
                            System.out.println("Invalid choice.");
                    }
                }
            } else {
                System.out.println("Invalid input. Exiting program.");
            }
        } catch (SQLException e) {
            System.err.println("SQL Error: " + e.getMessage());
        }
    }
}