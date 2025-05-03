import java.sql.*;
import java.util.*;

public class Program4 {
    private static final String QUERY2_STRING = "SELECT * FROM ( " +
            "SELECT 'LIFT RIDE' AS SECTION, ll.passId AS REF_ID, ll.liftName AS DETAIL1, TO_CHAR(ll.liftLotDate, 'YYYY-MM-DD') AS DETAIL2, NULL AS DETAIL3 FROM LiftLog ll "
            +
            "UNION ALL " +
            "SELECT 'RENTAL', r.passId, e.eType, e.eSize, r.returnStatus FROM Rental r JOIN Equipment e ON e.RID = r.RID "
            +
            "UNION ALL " +
            "SELECT 'SKIPASS', s.passId, s.type, TO_CHAR(s.purchaseDate, 'YYYY-MM-DD'), TO_CHAR(s.expirationDate, 'YYYY-MM-DD') FROM SkiPass s "
            +
            "UNION ALL " +
            "SELECT 'MEMBER', m.memberId, m.firstName || ' ' || m.lastName, m.phone, m.email FROM Member m " +
            "UNION ALL " +
            "SELECT 'LIFT', NULL, l.name, l.status, l.difficulty FROM Lift l " +
            ") WHERE REF_ID = ? OR REF_ID IS NULL";

    private static final String QUERY3_STRING = "SELECT t.name AS trail_name, " +
            "t.category, " +
            "l.name AS lift_name " +
            "FROM Trail t " +
            "JOIN Lift l ON t.name = l.name " +
            "WHERE t.difficulty = 'intermediate' " +
            "AND t.status = 'open' " +
            "AND l.status = 'open' " +
            "ORDER BY t.name";

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

    private static void exitProgram() {
        System.out.println("Exiting program. Goodbye!");
        System.exit(0);
    }

    private static void checkLoginInfo(String username, String password) {
        if (username == null || password == null) {
            System.out.println("""
                    Usage:  java Program4 <username> <password>
                    where <username> is your Oracle DBMS username,
                    and <password> is your Oracle password (not your system password).
                    """);
            System.exit(-1);
        }
    }

    // Placeholder
    private static void runQuery1(PreparedStatement stmt) throws SQLException {
        System.out.println("\n=== Query 1: ===");
    }

    private static void printQuery2Results(ResultSet rs) throws SQLException {
        System.out.println("\n==== Combined Report ====");
        while (rs.next()) {
            String section = rs.getString("SECTION");
            String refId = rs.getString("REF_ID");
            String detail1 = rs.getString("DETAIL1");
            String detail2 = rs.getString("DETAIL2");
            String detail3 = rs.getString("DETAIL3");

            System.out.printf("[%s] ID: %s | %s | %s | %s%n",
                    section,
                    (refId != null ? refId : "-"),
                    (detail1 != null ? detail1 : "-"),
                    (detail2 != null ? detail2 : "-"),
                    (detail3 != null ? detail3 : "-"));
        }
    }

    private static void printQuery3Results(ResultSet rs) throws SQLException {
        System.out.println("\n==== Open Intermediate Trails and Connected Lifts ====");
        System.out.printf("%-30s %-15s %-30s%n", "Trail Name", "Category", "Lift Name");
        System.out.println("-".repeat(77));

        while (rs.next()) {
            String trailName = rs.getString("trail_name");
            String category = rs.getString("category");
            String liftName = rs.getString("lift_name");

            System.out.printf("%-30s %-15s %-30s%n",
                    trailName,
                    category,
                    liftName);
        }
    }

    // Placeholder
    private static void runQuery4(PreparedStatement stmt) throws SQLException {
        System.out.println("\n=== Query 4: ===");
    }

    public static void main(String[] args) {
        String username = "eduardoh12";
        String password = "a3769";

        checkLoginInfo(username, password);

        System.out.println("Connecting " + username + " to Oracle DBMS...");
        try (Connection conn = getConnection(username, password);
                Scanner input = new Scanner(System.in)) {

            System.out.println("Connected to Oracle DBMS.\n");

            while (true) {
                System.out.println("\n==========================");
                System.out.println("SKI RESORT DATABASE SYSTEM");
                System.out.println("==========================");
                System.out.println("1. Query 1 - Member Ski Lessons");
                System.out.println("2. Query 2 - Ski Pass Details");
                System.out.println("3. Query 3 - Intermediate Trails and Lifts");
                System.out.println("4. Query 4 - Custom Query");
                System.out.println("5. Exit program");
                System.out.println("==========================");
                System.out.print("Enter your choice: ");

                String choice = input.nextLine();

                switch (choice) {
                    case "1":
                        // Placeholder
                        runQuery1(null);
                        break;

                    case "2":
                        System.out.print("Enter a passId (or type 'back' to return to menu): ");
                        String passId = input.nextLine();

                        if (passId.equalsIgnoreCase("back")) {
                            continue;
                        }

                        try (PreparedStatement stmt = conn.prepareStatement(QUERY2_STRING)) {
                            stmt.setString(1, passId);
                            try (ResultSet rs = stmt.executeQuery()) {
                                printQuery2Results(rs);
                            }
                        }
                        break;

                    case "3":
                        try (PreparedStatement stmt = conn.prepareStatement(QUERY3_STRING)) {
                            try (ResultSet rs = stmt.executeQuery()) {
                                printQuery3Results(rs);
                            }
                        }
                        break;

                    case "4":
                        // Placeholder
                        runQuery4(null);
                        break;

                    case "5":
                        exitProgram();
                        break;

                    default:
                        System.out.println("Invalid choice");
                }

                System.out.print("\nPress Enter to continue...");
                input.nextLine();
            }
        } catch (SQLException e) {
            System.err.println("*** SQLException:  " + e.getMessage());
            System.exit(-1);
        }
    }
}