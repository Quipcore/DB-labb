import java.sql.*;
import java.util.Scanner;

public class DB {
    public static void main(String[] args) throws SQLException {
        String url = "jdbc:ucanaccess://resources/SQLDatabas.accdb";
        Connection con = DriverManager.getConnection(url);
        queryA(con);
        queryB(con);
        queryC(con);
        con.close();
    }

    private static void queryA(Connection con) throws SQLException {
        System.out.println("Query A");

        String distinctCourseTeacher = "SELECT DISTINCT kurs, lärare " +
                "FROM Kurstillfälle KT " +
                "GROUP BY kurs, lärare";

        String teacherCountPerCourse = "SELECT P.kurs, COUNT(P.lärare) AS antal " +
                "FROM (" + distinctCourseTeacher + ") AS P " +
                "GROUP BY P.kurs";

        String query = "SELECT K.kurskod, K.benämning " +
                "FROM Kurs K,(" + teacherCountPerCourse + ")AS L " +
                "WHERE K.kurskod = L.kurs " +
                "AND L.antal >= 2";

        ResultSet rs = con.createStatement().executeQuery(query);
        while (rs.next()) {
            String kurskod = rs.getString("kurskod");
            String ben = rs.getString("benämning");
            System.out.printf("%s\t\t%s\n", kurskod, ben);
        }
        System.out.println();
    }

    private static void queryB(Connection con) throws SQLException {
        System.out.println("Query B");
        String query = "SELECT rum,telefon,lnamn FROM Lärare";
        ResultSet rs = con.createStatement().executeQuery(query);
        while (rs.next()) {
            String rum = rs.getString("rum");
            String telefon = rs.getString("telefon");
            String lnamn = rs.getString("lnamn");
            System.out.printf("%s\t\t%s\t\t%s\n", rum, telefon, lnamn);
        }
        System.out.println();
    }

    private static void queryC(Connection con) throws SQLException {
        PreparedStatement pstmt;

        System.out.println("Query C");

        String userAnswer = getUserString("Select Teacher:");

        String lid = "SELECT lid FROM lärare WHERE lnamn = ?";

        String query = "SELECT DISTINCT K.kurskod, K.benämning, K.längd, K.pris " +
                "FROM Kurs K, Kurstillfälle KT " +
                "WHERE KT.kurs = K.kurskod " +
                "AND KT.lärare IN (" + lid + ")";

        pstmt = con.prepareStatement(query);
        pstmt.setString(1, userAnswer);

        ResultSet rs = pstmt.executeQuery();

        while (rs.next()) {
            String kurskod = rs.getString("kurskod");
            String ben = rs.getString("benämning");
            String len = rs.getString("längd");
            String pris = rs.getString("pris");
            System.out.printf("%s\t\t%s\t\t%s\t\t%s\n", kurskod, ben, len, pris);
        }
        System.out.println();
    }

    private static String getUserString(String prompt) {
        Scanner scan = new Scanner(System.in);
        System.out.print(prompt);
        String usrAns = scan.nextLine();
        scan.close();
        return usrAns.trim();
    }
}
