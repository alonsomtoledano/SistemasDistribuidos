package parking.service;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class Connect {
	public Connection getConnection() {
		try {
			return DriverManager.getConnection(
					"jdbc:mysql://localhost:3306/parking?useUnicode=true&useJDBCCompliantTimezoneShift=true&useLegacyDatetimeCode=false&serverTimezone=UTC",
					"root", "agentealfa");
		} catch (SQLException e) {
			System.out.println(e.getMessage());
		}
		return null;
	}
}
