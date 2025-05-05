import java.lang.reflect.Member;
import java.sql.*;
import java.util.*;

public class Program4 {
    private static final String QUERY1_STRING = "SELECT LP.orderId, LP.totalSessions, LP.remainingSessions, LP.pricePerSession, "
            +
            "L.lessonId, L.ageType, L.lessonType, L.durationType, L.startTime, " +
            "E.firstName AS instructorFirstName, E.lastName AS instructorLastName " +
            "FROM LessonPurchase LP JOIN Lesson L ON LP.lessonId = L.lessonId " +
            "JOIN Instructor I ON L.instructorId = I.instructorId " +
            "JOIN Employee E ON I.instructorId = E.employeeId " +
            "JOIN Member M ON LP.memberId = M.memberId " +
            "WHERE M.firstName = 'Olivia' AND M.lastName = 'Robinson';";

    private static final String QUERY2_STRING = "SELECT * FROM ( " +
            "SELECT 'LIFT RIDE' AS SECTION, ll.passId AS REF_ID, ll.liftName AS DETAIL1, TO_CHAR(ll.liftLotDate, 'YYYY-MM-DD') AS DETAIL2, NULL AS DETAIL3 FROM LiftLog ll "
            +
            "UNION ALL SELECT 'RENTAL', r.passId, e.eType, e.eSize, r.returnStatus FROM Rental r JOIN Equipment e ON e.RID = r.RID "
            +
            "UNION ALL SELECT 'SKIPASS', s.passId, s.type, TO_CHAR(s.purchaseDate, 'YYYY-MM-DD'), TO_CHAR(s.expirationDate, 'YYYY-MM-DD') FROM SkiPass s "
            +
            "UNION ALL SELECT 'MEMBER', m.memberId, m.firstName || ' ' || m.lastName, m.phone, m.email FROM Member m " +
            "UNION ALL SELECT 'LIFT', NULL, l.name, l.status, l.difficulty FROM Lift l ) WHERE REF_ID = ? OR REF_ID IS NULL";

    private static final String QUERY3_STRING = "SELECT t.name AS trail_name, t.category, l.name AS lift_name " +
            "FROM Trail t JOIN Lift l ON t.name = l.name " +
            "WHERE t.difficulty = 'intermediate' AND t.status = 'open' AND l.status = 'open' ORDER BY t.name";

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

    private static void insertEquipmentItem(Connection conn, int EID, int RID, String eTpe, String eSize,
            String eStatus) throws SQLException {
        String insertSQL = "INSERT INTO Equipment (EID, RID, eType, eSize, eStatus) VALUES (?, ?, ?, ?, ?)";
        try (PreparedStatement pstmt = conn.prepareStatement(insertSQL)) {
            pstmt.setInt(1, EID);
            pstmt.setInt(2, RID);
            pstmt.setString(3, eTpe);
            pstmt.setString(4, eSize);
            pstmt.setString(5, eStatus);
            int rowsAffected = pstmt.executeUpdate();
            System.out
                    .println(rowsAffected > 0 ? "Equipment item added successfully." : "Failed to add equipment item.");
        }
    }

    private static void updateEquipmentItem(Connection conn, int EID, String eStatus, int RID, String eType,
            String eSize) throws SQLException {
        String updateSQL = "UPDATE Equipment SET eStatus = ? AND eType = ? AND eSize = ? WHERE EID = ?";
        try (PreparedStatement pstmt = conn.prepareStatement(updateSQL)) {
            pstmt.setString(1, eStatus);
            pstmt.setString(2, eType);
            pstmt.setString(3, eSize);
            pstmt.setInt(4, EID);
            int rowsAffected = pstmt.executeUpdate();
            System.out.println(rowsAffected > 0 ? "Equipment item updated successfully."
                    : "No equipment item found with the provided EID.");
        }
    }

    private static void deleteEquipmentItem(Connection conn, int EID) throws SQLException {
        String deleteSQL = "DELETE FROM Equipment WHERE EID = ?";
        try (PreparedStatement pstmt = conn.prepareStatement(deleteSQL)) {
            pstmt.setInt(1, EID);
            int rowsAffected = pstmt.executeUpdate();
            System.out.println(rowsAffected > 0 ? "Equipment item deleted successfully."
                    : "No equipment item found with the provided EID.");
        }
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

    private static void runQuery1(Connection conn) throws SQLException {
        System.out.println("\n=== Query 1: ===");
        try (PreparedStatement stmt = conn.prepareStatement(QUERY1_STRING); ResultSet rs = stmt.executeQuery()) {
            while (rs.next()) {
                System.out.printf(
                        "OrderID: %d, Sessions: %d/%d, Price: $%.2f, LessonID: %d, Age: %s, Type: %s, Duration: %s, Time: %s, Instructor: %s %s\n",
                        rs.getInt("orderId"), rs.getInt("totalSessions"), rs.getInt("remainingSessions"),
                        rs.getDouble("pricePerSession"), rs.getInt("lessonId"), rs.getString("ageType"),
                        rs.getString("lessonType"), rs.getString("durationType"), rs.getString("startTime"),
                        rs.getString("instructorFirstName"), rs.getString("instructorLastName"));
            }
        }
    }

    public static void main(String[] args) {
        String username = "eduardoh12";
        String password = "a3769";

        try (Connection conn = getConnection(username, password); Scanner input = new Scanner(System.in)) {
            System.out.print("Do you want to edit the database? (y/n): ");
            String editChoice = input.nextLine().trim().toLowerCase();

            if (editChoice.equals("y")) {
                while (true) {
                    System.out.println("\nLesson Purchase Management");
                    System.out.println("1. Add Lesson Purchase");
                    System.out.println("2. Update Lesson Purchase");
                    System.out.println("3. Delete Lesson Purchase");
                    System.out.println("4. Exit");
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
                            deleteLessonPurchase(conn, deleteOrderId);
                            break;
                        case "4":
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
                    System.out.println("4. Exit");
                    System.out.print("Enter your choice: ");
                    String queryChoice = input.nextLine();

                    switch (queryChoice) {
                        case "1":
                            runQuery1(conn);
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
                                                rs.getString("DETAIL1"), rs.getString("DETAIL2"),
                                                rs.getString("DETAIL3"));
                                    }
                                }
                            }
                            break;
                        case "3":
                            try (PreparedStatement stmt = conn.prepareStatement(QUERY3_STRING);
                                    ResultSet rs = stmt.executeQuery()) {
                                while (rs.next()) {
                                    System.out.printf("Trail: %s, Category: %s, Lift: %s\n",
                                            rs.getString("trail_name"), rs.getString("category"),
                                            rs.getString("lift_name"));
                                }
                            }
                            break;
                        case "4":
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
