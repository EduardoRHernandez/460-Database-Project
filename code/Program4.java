
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

    public static void main(String[] args) {
        String username = "eduardoh12";
        String password = "a3769";

        checkLoginInfo(username, password);

        System.out.println("Connecting " + username + " to Oracle DBMS...");
        try (Connection conn = getConnection(username, password);
                Scanner input = new Scanner(System.in)) {

            System.out.println("Connected to Oracle DBMS.\n");

            while (true) {
                System.out.print("Enter a passId (or type 'exit' to quit): ");
                String passId = input.nextLine();

                if (passId.equalsIgnoreCase("exit")) {
                    exitProgram();
                }

                try (PreparedStatement stmt = conn.prepareStatement(QUERY2_STRING)) {
                    stmt.setString(1, passId);
                    try (ResultSet rs = stmt.executeQuery()) {
                        printQuery2Results(rs);
                    }
                }
            }
        } catch (SQLException e) {
            System.err.println("*** SQLException:  " + e.getMessage());
            System.exit(-1);
        }
    }
}