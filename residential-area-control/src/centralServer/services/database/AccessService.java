package centralServer.services.database;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import encryptation.Encryptation;

public class AccessService {
	Connection connection;

	public AccessService() {
		connection = new ConnectAccess().getConnection();
	}

	public void addLicensePlate(String plate, long time, String resident) {
		boolean exists = false;
		try {
			String sqlSelect = "select * from vehicles where licensePlate = ?";
			PreparedStatement ps = connection.prepareStatement(sqlSelect);
			ps.setString(1, plate);
			ResultSet rs = ps.executeQuery();
			List<String> result = new ArrayList<String>();

			while (rs.next()) {
				if (rs.getString("licensePlate").equals(plate)) {
					exists = true;
				} else {

					exists = false;
				}
			}
			if (!exists) {
				String sqlInsert = "INSERT INTO vehicles (licensePlate, entered, resident) VALUES(?, ?, ?)";
				PreparedStatement psInsert = connection.prepareStatement(sqlInsert);

				psInsert.setString(1, plate);
				psInsert.setLong(2, time);
				psInsert.setString(3, resident);
				psInsert.executeUpdate();

			} else {
				String sqlUpdate = "update vehicles set entered = ?, resident = ? where licensePlate = ?";
				PreparedStatement ps1 = connection.prepareStatement(sqlUpdate);
				ps1.setLong(1, time);
				ps1.setString(2, resident);
				ps1.setString(3, plate);
				ps1.executeUpdate();

			}

		} catch (SQLException e) {
			e.getMessage();
		}

	}
	
	public void vehicleExit(String matricula, long time) {
		String sqlUpdate = "update vehicles set timeOut = ? where licensePlate = ?";
		try {
			matricula = Encryptation.Encrypt(matricula);
			
			PreparedStatement ps1 = connection.prepareStatement(sqlUpdate);
			ps1.setLong(1, time);
			ps1.setString(2, matricula);
			ps1.executeUpdate();
			
			System.out.println("Vehicle updated\n");
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}
	
	public void leaveParking(String plate, long time) {
		try {
			String sqlUpdate = "update vehicles set parking = ? where licensePlate = ?";
			PreparedStatement ps1 = connection.prepareStatement(sqlUpdate);
			ps1.setLong(1, time);
			ps1.setString(3, plate);
			ps1.executeUpdate();
		} catch (SQLException e) {
			e.printStackTrace();
		}

		
	}

}
