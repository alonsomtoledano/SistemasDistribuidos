package publicAdministration.database;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class UsersService {
	Connection connection;
	public int counter = 0;

	public UsersService() {
		connection = new ConnectUsers().getConnection();
	}

	public boolean signUpUser(String user, String password, String dni) {
		boolean signUp = false;
		try {
			String sqlSelect = "select * from admins where dni = ?";
			PreparedStatement ps = connection.prepareStatement(sqlSelect);
			ps.setString(1, dni);
			ResultSet rs = ps.executeQuery();
			List<String> result = new ArrayList<String>();

			while (rs.next()) {
				if (rs.getString("dni").equals(dni)) {
					result.add(rs.getInt("id") + " - " + rs.getString("username") + rs.getString("password"));
					signUp = true;
				} else {
					signUp = false;
				}
			}
			if (!signUp) {
				String sqlInsert = "INSERT INTO admins (username, password, dni, status) VALUES(?, ?, ?, 0)";
				PreparedStatement psInsert = connection.prepareStatement(sqlInsert);

				psInsert.setString(1, user);
				psInsert.setString(2, password);
				psInsert.setString(3, dni);

				psInsert.executeUpdate();
			}

		} catch (SQLException e) {
			e.getMessage();
		}

		return signUp;
	}

	public String loginUser(String user, String password) {
		String loginStatus = null;
		boolean status = false;
		List<String> result = new ArrayList<String>();

		try {
			String sqlSelect = "select * from admins where username = ?";
			PreparedStatement ps = connection.prepareStatement(sqlSelect);
			ps.setString(1, user);
			ResultSet rs = ps.executeQuery();
			while (rs.next()) {
				if (rs.getString("password").equals(password)) {
					result.add(rs.getInt("id") + " - " + rs.getString("username") + rs.getString("password")
							+ rs.getString("dni"));
					status = true;
					loginStatus = rs.getString("dni");
				} else {
					status = true;
					loginStatus = "1";
				}
			}
			if (!status) {
				loginStatus = "2";
			}

		} catch (SQLException e) {
			e.getMessage();
		}
		return loginStatus;
	}

	public void changeStatus(String dni, String status) {
		try {
			String sqlSelect = "update admins set status = ? where dni = ?";
			PreparedStatement ps = connection.prepareStatement(sqlSelect);
			ps.setString(1, status);
			ps.setString(2, dni);
			ps.execute();
		} catch (SQLException e) {
			e.getMessage();
		}
	}

	public String getStatus(String dni) {
		String status = null;
		List<String> result = new ArrayList<String>();
		try {
			String sqlSelect = "select * from admins where dni = ?";
			PreparedStatement ps = connection.prepareStatement(sqlSelect);
			ps.setString(1, dni);
			ResultSet rs = ps.executeQuery();
			while (rs.next()) {
				result.add(rs.getString("status"));
				status = rs.getString("status");
			}
		} catch (SQLException e) {
			e.getMessage();
		}
		return status;
	}
}