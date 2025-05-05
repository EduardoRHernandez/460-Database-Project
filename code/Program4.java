import java.lang.reflect.Member;
import java.sql.*;
import java.util.*;

public class Program4 {
    private static final String QUERY1_STRING =
        "SELECT LP.orderId, LP.totalSessions, LP.remainingSessions, LP.pricePerSession, " +
        "L.lessonId, L.ageType, L.lessonType, L.durationType, L.startTime, " +
        "E.firstName AS instructorFirstName, E.lastName AS instructorLastName " +
        "FROM LessonPurchase LP JOIN Lesson L ON LP.lessonId = L.lessonId " +
        "JOIN Instructor I ON L.instructorId = I.instructorId " +
        "JOIN Employee E ON I.instructorId = E.employeeId " +
        "JOIN Member M ON LP.memberId = M.memberId " +
        "WHERE M.firstName = ? AND M.lastName = ?";

    private static final String QUERY2_STRING = "SELECT * FROM ( " +
            "SELECT 'LIFT RIDE' AS SECTION, ll.passId AS REF_ID, ll.liftName AS DETAIL1, TO_CHAR(ll.liftLotDate, 'YYYY-MM-DD') AS DETAIL2, NULL AS DETAIL3 FROM LiftLog ll " +
            "UNION ALL SELECT 'RENTAL', r.passId, e.eType, e.eSize, r.returnStatus FROM Rental r JOIN Equipment e ON e.RID = r.RID " +
            "UNION ALL SELECT 'SKIPASS', s.passId, s.type, TO_CHAR(s.purchaseDate, 'YYYY-MM-DD'), TO_CHAR(s.expirationDate, 'YYYY-MM-DD') FROM SkiPass s " +
            "UNION ALL SELECT 'MEMBER', m.memberId, m.firstName || ' ' || m.lastName, m.phone, m.email FROM Member m " +
            "UNION ALL SELECT 'LIFT', NULL, l.name, l.status, l.difficulty FROM Lift l ) WHERE REF_ID = ? OR REF_ID IS NULL";

    private static final String QUERY3_STRING = "SELECT t.name AS trail_name, t.category, l.name AS lift_name " +
            "FROM Trail t JOIN Lift l ON t.name = l.name " +
            "WHERE t.difficulty = 'intermediate' AND t.status = 'open' AND l.status = 'open' ORDER BY t.name";

    private static final String QUERY4_STRING = "SELECT eq.EID as EquipmentID, eq.ETYPE as EquipmentType, eq.ESIZE as EquipmentSize, eq.ESTATUS as CurrentStatus, " +
            " log.OLDTYPE,  log.NEWTYPE, log.OLDSIZE, log.NEWSIZE, log.OLDSTATUS, log.NEWSTATUS, TO_CHAR(log.changeDate, 'YYYY-MM-DD HH24:MI:SS') AS ChangeDate " +
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

    private static void addLessonPurchase(Connection conn, int orderId, int memberId, int lessonId, int totalSessions, int remainingSessions, double pricePerSession) throws SQLException {
        String insertSQL = "INSERT INTO LessonPurchase (orderId, memberId, lessonId, totalSessions, remainingSessions, pricePerSession) VALUES (?, ?, ?, ?, ?, ?)";
        try (PreparedStatement pstmt = conn.prepareStatement(insertSQL)) {
            pstmt.setInt(1, orderId);
            pstmt.setInt(2, memberId);
            pstmt.setInt(3, lessonId);
            pstmt.setInt(4, totalSessions);
            pstmt.setInt(5, remainingSessions);
            pstmt.setDouble(6, pricePerSession);
            int rowsAffected = pstmt.executeUpdate();
            System.out.println(rowsAffected > 0 ? "Lesson purchase added successfully." : "Failed to add lesson purchase.");
        }
    }

    private static void updateLessonPurchase(Connection conn, int orderId, int remainingSessions) throws SQLException {
        String updateSQL = "UPDATE LessonPurchase SET remainingSessions = ? WHERE orderId = ?";
        try (PreparedStatement pstmt = conn.prepareStatement(updateSQL)) {
            pstmt.setInt(1, remainingSessions);
            pstmt.setInt(2, orderId);
            int rowsAffected = pstmt.executeUpdate();
            System.out.println(rowsAffected > 0 ? "Lesson purchase updated successfully." : "No lesson purchase found with the provided orderId.");
        }
    }

    private static void deleteLessonPurchase(Connection conn, int orderId) throws SQLException {
        String deleteSQL = "DELETE FROM LessonPurchase WHERE orderId = ? AND remainingSessions = totalSessions";
        try (PreparedStatement pstmt = conn.prepareStatement(deleteSQL)) {
            pstmt.setInt(1, orderId);
            int rowsAffected = pstmt.executeUpdate();
            System.out.println(rowsAffected > 0 ? "Lesson purchase deleted successfully." : "No eligible lesson purchase found to delete (must have zero sessions used).");
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
                    System.out.printf("OrderID: %d, Sessions: %d/%d, Price: $%.2f, LessonID: %d, Age: %s, Type: %s, Duration: %s, Time: %s, Instructor: %s %s\n",
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
        String add_member_string = 
        "INSERT INTO member ( " + 
        "FIRSTNAME, LASTNAME, PHONE, EMAIL, DOB, " + 
        "EMGCONTACTFNAME, EMGCONTACTLNAME, EMGCONTACTPHONE ) " + 
        " VALUES (?, ?, ?, ?, TO_DATE(?, 'YYYY-MM-DD'), ?, ?, ? )";

        System.out.println("First Name:");
        String firstName = input.nextLine();
        System.out.println("Last Name:");
        String lastName = input.nextLine();
        System.out.println("Phone (XXX-XXX-XXXX):");
        String phone = input.nextLine();
        System.out.println("Email:");
        String email = input.nextLine();
        System.out.println("Date Of Birth (YYYY-MM-DD):");
        String dob = input.nextLine();
        System.out.println("Emergency Contact First Name:");
        String emFirstName = input.nextLine();
        System.out.println("Emergency Contact Last Name:");
        String emLastName = input.nextLine();
        System.out.println("Emergency Contact Phone (XXX-XXX-XXXX):");
        String emPhone = input.nextLine();

        try (PreparedStatement stmt = conn.prepareStatement(add_member_string,new String[] { "memberId" })) {
            stmt.setString(1, firstName);
            stmt.setString(2, lastName);
            stmt.setString(3, phone);
            stmt.setString(4, email);
            stmt.setString(5, dob);
            stmt.setString(6, emFirstName);
            stmt.setString(7, emLastName);
            stmt.setString(8, emPhone);
            int rowsAffected = stmt.executeUpdate();
            if (rowsAffected > 0){
                try(ResultSet rs = stmt.getGeneratedKeys()){
                    if (rs.next()){
                        System.out.println("Successfully generated new member. Member id -> " + rs.getInt(1));
                    }
                }
            } else{
                System.out.println("Failed to add member");
            }
            // System.out.println(rowsAffected > 0 ? "Member added successfully." : "Failed to add member.");
        }

    }

    private static boolean memberExists(Connection conn, int memberId) throws SQLException {
        String memberCheckSql = "SELECT 1 FROM Member WHERE MemberId = ?";
        try (PreparedStatement stmt = conn.prepareStatement(memberCheckSql)) {
            stmt.setInt(1, memberId);
            try (ResultSet rs = stmt.executeQuery()){
                return rs.next();
            }
        }

    }

    private static boolean passExists(Connection conn, int passId) throws SQLException {
        String passCheckSql = "SELECT 1 FROM skipass WHERE passid = ?";
        try (PreparedStatement stmt = conn.prepareStatement(passCheckSql)) {
            stmt.setInt(1, passId);
            try (ResultSet rs = stmt.executeQuery()){
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
        } catch (NumberFormatException e){
            System.out.println("Invalid member id. Must enter a number");
            return;
        }
        

        if (!memberExists(conn, memberId)){
            System.out.println("Invalid member id. Member does not exist");
            return;
        } else{


            System.out.println("Which field would you like to edit?");
            System.out.println("1. First Name \n2. Last Name \n3. Phone Number \n4. Email \n5. Date of Birth \n6. Emergency Contact Name \n7. Emergency Contact Phone \n8. Exit");
            System.out.println("Enter a number (1-8)");
            String choice = input.nextLine();
            String toPrint = "Hope this is initialized";
            String field = null;
            String newVal1;
            String newVal2;
            int inputCase;
            switch (choice){
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
                    break ;
                case "8":
                    return;
                default:
                    System.out.println("Invalid choice. Press enter to continue");
                    input.nextLine();
                    return;
            }
            
            String editMemberSql; // = "UPDATE Member SET " + field + " = ? WHERE MemberId = ?";

            if (inputCase == 1){
                System.out.println(toPrint);
                newVal1 = input.nextLine();
                editMemberSql = "UPDATE Member SET " + field + "= ? WHERE MemberId = ?";
                try (PreparedStatement stmt = conn.prepareStatement(editMemberSql)) {
                    stmt.setString(1, newVal1);
                    stmt.setInt(2, memberId);
                    int rows = stmt.executeUpdate();
                    System.out.println(rows > 0 ? "Member updated successfully." : "Update failed.");
   
                    
                }
            } else if (inputCase == 2){
                System.out.println("Enter the updated date of birth (YYYY-MM-DD): ");
                newVal1 = input.nextLine();
                editMemberSql = "UPDATE Member SET dob = TO_DATE(?, 'YYYY-MM-DD') WHERE MemberId = ?";
                try (PreparedStatement stmt = conn.prepareStatement(editMemberSql)) {
                    stmt.setString(1, newVal1);
                    stmt.setInt(2, memberId);
                    int rows = stmt.executeUpdate();
                    System.out.println(rows > 0 ? "Member updated successfully." : "Update failed.");
                    
                }
            } else if (inputCase == 3){
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

            // TEST CODE ONLY. DELETE THIS
            String query = "SELECT * FROM Member";
            try (Statement stmt = conn.createStatement(); ResultSet rs = stmt.executeQuery(query)) {
                ResultSetMetaData meta = rs.getMetaData();
                int columnCount = meta.getColumnCount();

                System.out.println("\n=== Member Table ===");

                while (rs.next()) {
                    System.out.println("-------------------------------");
                    for (int i = 1; i <= columnCount; i++) {
                        String colName = meta.getColumnName(i);
                        String value = rs.getString(i);
                        System.out.printf("%-20s: %s%n", colName, value);
                    }
                }

                System.out.println("-------------------------------");
            }
        }



    }

    private static void deleteMember(Connection conn, Scanner input) throws SQLException {
        System.out.println("Enter member id to delete");
        String memberIdString = input.nextLine();
        int memberId;
        try {
            memberId = Integer.parseInt(memberIdString);
        } catch (NumberFormatException e){
            System.out.println("Invalid member id. Must enter a number");
            return;
        }
        

        if (!memberExists(conn, memberId)){
            System.out.println("Invalid member id. Member does not exist");
            return;
        } else{

            boolean unableToReturn = false;

            if (hasAny(conn,
                "SELECT 1 FROM skipass WHERE memberId = " + memberId + " AND EXPIRATIONDATE >= SYSDATE")){
                System.out.println("Active ski pass(es) present. Please delete or use them before deleting account");
                unableToReturn = true;
            }

            if (hasAny(conn,
                "SELECT 1 FROM lessonpurchase WHERE memberId = " + memberId + " AND remainingSessions > 0")){
                System.out.println("Active lesson purchase(s) present. Please delete or use them before deleting account");
                unableToReturn = true;
            }

            if (hasAny(conn,
                "SELECT 1 FROM Rental r JOIN SKIPASS s ON r.passid = s.passid WHERE s.memberId = " + memberId + " AND r.returnStatus = 'Rented'")){
                System.out.println("Active rental(s) present. Please delete or use them before deleting account");
                unableToReturn = true;
            }

            if (unableToReturn){
                System.out.println("Unable to delete account yet. Press enter to continue");
                input.nextLine();
                return;

            } else{
                System.out.println("Are you sure you want to delete your account. This action is final");
                System.out.println("Press 'y' to permanently delete your account");
                System.out.println("Press 'n' to cancel");
                String selection = input.nextLine();
                if (selection.compareTo("y") == 0){
                    String deleteMemberSql = "delete from member where memberid = ?";
                    try (PreparedStatement stmt = conn.prepareStatement(deleteMemberSql)) {
                        stmt.setInt(1, memberId);
                        int rows = stmt.executeUpdate();
                        System.out.println(rows > 0 ? "Membership successfully deleted" : "Deletion failed.");
                }
                } else{
                    return;
                }
            }

            // TEST CODE ONLY. DELETE THIS
            // String query = "SELECT * FROM Member";
            // try (Statement stmt = conn.createStatement(); ResultSet rs = stmt.executeQuery(query)) {
            //     ResultSetMetaData meta = rs.getMetaData();
            //     int columnCount = meta.getColumnCount();

            //     System.out.println("\n=== Member Table ===");

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



    }

    private static void addSkiPass(Connection conn, Scanner input) throws SQLException {

        System.out.println("Member ID:");
        String memberIdString = input.nextLine();

        int memberId;
        try {
            memberId = Integer.parseInt(memberIdString);
        } catch (NumberFormatException e){
            System.out.println("Invalid member id. Must enter a number");
            return;
        }
        
        if (!memberExists(conn, memberId)){
            System.out.println("Invalid member id. Member does not exist");
            return;
        }

        System.out.println("Enter expiration date (YYYY-MM-DD):");
        String expDate = input.nextLine();

        System.out.print("Select pass type (A. 1 day, B. 2 day, C. 4 day, D. season)");
        System.out.println("Enter A-D:");
        String passType = input.nextLine();
        switch (passType){
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
        try (PreparedStatement stmt = conn.prepareStatement(sql,new String[] { "passId" })) {
            stmt.setInt(1, memberId);
            stmt.setString(2, expDate);
            stmt.setInt(3, 0);
            stmt.setString(4, passType);
            int rowsAffected = stmt.executeUpdate();
            if (rowsAffected > 0){
                try(ResultSet rs = stmt.getGeneratedKeys()){
                    if (rs.next()){
                        System.out.println("Successfully generated new pass. Pass id -> " + rs.getInt(1));
                    }
                }
            } else{
                System.out.println("Failed to add new ski pass");
            }
            // System.out.println(rowsAffected > 0 ? "Member added successfully." : "Failed to add member.");
        }

    }

    private static void updateSkiPass(Connection conn, Scanner input) throws SQLException {

        System.out.println("Ski Pass Id:");
        String passIdString = input.nextLine();

        int passId;
        try {
            passId = Integer.parseInt(passIdString);
        } catch (NumberFormatException e){
            System.out.println("Invalid pass id. Must enter a number");
            return;
        }
        
        if (!passExists(conn, passId)){
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
        } catch (NumberFormatException e){
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
            String query = "SELECT * FROM skipass";
            try (Statement stmt = conn.createStatement(); ResultSet rs = stmt.executeQuery(query)) {
                ResultSetMetaData meta = rs.getMetaData();
                int columnCount = meta.getColumnCount();

                System.out.println("\n=== SkiPass Table ===");

                while (rs.next()) {
                    System.out.println("-------------------------------");
                    for (int i = 1; i <= columnCount; i++) {
                        String colName = meta.getColumnName(i);
                        String value = rs.getString(i);
                        System.out.printf("%-20s: %s%n", colName, value);
                    }
                }

                System.out.println("-------------------------------");
            }

    }

    private static boolean hasAny(Connection conn, String sql) throws SQLException {
        try (PreparedStatement stmt = conn.prepareStatement(sql)){
            try (ResultSet rs = stmt.executeQuery()) {
                return rs.next();
        }
        }

    }

    


        


    

   

    public static void main(String[] args) {
        String username = "stevengeorge";
        String password = "a9666";

        try {
			Class.forName("oracle.jdbc.OracleDriver");
		} catch (ClassNotFoundException e) {
			System.err.println("*** ClassNotFoundException:  " + "Error loading Oracle JDBC driver.  \n"
					+ "\tPerhaps the driver is not on the Classpath? \n" + "Try this: \n"
					+ "export CLASSPATH=/usr/lib/oracle/19.8/client64/lib/ojdbc8.jar:${CLASSPATH}");

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
                    System.out.println("10. Exit");
                    System.out.print("Select an option: ");

                    String choice = input.nextLine();
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
                            addLessonPurchase(conn, orderId, memberId, lessonId, totalSessions, remainingSessions, pricePerSession);
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
                            deleteLessonPurchase(conn, deleteOrderId);
                            break;
                        case "4":
                            addMember(conn, input);
                            break;
                        case "5":
                            updateMember(conn, input);
                        case "6":
                            deleteMember(conn, input);
                        case "7":
                            addSkiPass(conn, input);
                        case "8":
                            updateSkiPass(conn, input);
                        case "10":
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
                                        System.out.printf("[%s] ID: %s | %s | %s | %s\n",
                                                rs.getString("SECTION"), rs.getString("REF_ID"),
                                                rs.getString("DETAIL1"), rs.getString("DETAIL2"), rs.getString("DETAIL3"));
                                    }
                                }
                            }
                            break;
                        case "3":
                            try (PreparedStatement stmt = conn.prepareStatement(QUERY3_STRING); ResultSet rs = stmt.executeQuery()) {
                                while (rs.next()) {
                                    System.out.printf("Trail: %s, Category: %s, Lift: %s\n",
                                            rs.getString("trail_name"), rs.getString("category"), rs.getString("lift_name"));
                                }
                            }
                            break;
                            case "4":
                            System.out.print("Enter equipment id: ");
                            String eId = input.nextLine();
                            try{
                                Integer.parseInt(eId);
                            } catch (NumberFormatException e){
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