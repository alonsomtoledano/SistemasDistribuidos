package publicAdministration.database;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class CitizensService {
	Connection connection;
	static String path = "./src/publicAdministration/logs/citizensDatabase.log";

	public CitizensService() {
		connection = new ConnectCitizens().getConnection();
	}

	public boolean addResident(String dni, String plate, String name, String surname) {
		boolean add = false;
		try {
			// Check license plate
			String sqlSelect = "select * from residents where licensePlate = ?";
			PreparedStatement ps = connection.prepareStatement(sqlSelect);
			ps.setString(1, plate);
			ResultSet rs = ps.executeQuery();
			List<String> result = new ArrayList<String>();

			while (rs.next()) {
				if (rs.getString("licensePlate").equals(plate)) {
					result.add(rs.getString("licensePlate") + rs.getString("name"));
					add = true;
				} else {
					add = false;
				}
			}
			if (!add) {
				// If license plate is not registered
				String sqlInsert = "INSERT INTO residents (dni, name, surname, licensePlate) VALUES(?, ?, ?, ?)";
				PreparedStatement psInsert = connection.prepareStatement(sqlInsert);

				psInsert.setString(1, dni);
				psInsert.setString(2, name);
				psInsert.setString(3, surname);
				psInsert.setString(4, plate);
				psInsert.executeUpdate();

			}

		} catch (SQLException e) {
			e.printStackTrace();
		}

		return add;
	}

	public boolean deleteResident(String dni) {
		boolean delete = false;
		try {

			// Check DNI
			String sqlSelect = "select * from residents where dni = ?";
			PreparedStatement ps = connection.prepareStatement(sqlSelect);
			ps.setString(1, dni);
			ResultSet rs = ps.executeQuery();
			List<String> result = new ArrayList<String>();

			while (rs.next()) {
				if (rs.getString("dni").equals(dni)) {
					result.add(rs.getString("name"));
					delete = true;
				} else {
					delete = false;

				}
			}
			if (delete) {
				String sqlDelete = "DELETE FROM residents WHERE dni = ?";
				PreparedStatement psDlete = connection.prepareStatement(sqlDelete);
				psDlete.setString(1, dni);
				psDlete.executeUpdate();

			}

		} catch (SQLException e) {
			e.printStackTrace();
		}
		return delete;
	}

	public boolean findResident(String plate) {
		boolean found = false;
		try {
			// Check license plate
			String sqlSelect = "select * from residents where licensePlate = ?";
			PreparedStatement ps = connection.prepareStatement(sqlSelect);
			ps.setString(1, plate);
			ResultSet rs = ps.executeQuery();
			List<String> result = new ArrayList<String>();

			while (rs.next()) {
				if (rs.getString("licensePlate").equals(plate)) {
					result.add(rs.getString("licensePlate") + rs.getString("name"));
					found = true;
				} else {
					found = false;
				}
			}
		} catch (SQLException e) {
			e.printStackTrace();
		}

		return found;
	}

	public List<String> findSanctioned(String plate) {
		List<String> result = new ArrayList<String>();
		try {
			String sqlSelect = "select * from sanctioned where licensePlate = ?";
			PreparedStatement ps = connection.prepareStatement(sqlSelect);
			ps.setString(1, plate);
			ResultSet rs = ps.executeQuery();

			while (rs.next()) {
				if (rs.getString("licensePlate").equals(plate)) {
					result.add(
							rs.getString("licensePlate") + " - " + rs.getString("date"));

				}
			}

		} catch (SQLException e) {
			e.printStackTrace();
		}
		return result;
	}

	public void addSanction(String plate) {
		try {
			String sqlInsert = "INSERT INTO sanctioned (licensePlate) VALUES(?)";
			PreparedStatement psInsert = connection.prepareStatement(sqlInsert);

			psInsert.setString(1, plate);
			psInsert.executeUpdate();

		} catch (SQLException e) {
			e.getMessage();
		}
	}
}
