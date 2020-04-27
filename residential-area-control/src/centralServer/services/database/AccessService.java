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

	public void addLicensePlate(String plate, String image, long time, String resident) {
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
				String sqlInsert = "INSERT INTO vehicles (licensePlate, image, entered, resident) VALUES(?, ?, ?, ?)";
				PreparedStatement psInsert = connection.prepareStatement(sqlInsert);

				psInsert.setString(1, plate);
				psInsert.setString(2, image);
				psInsert.setLong(3, time);
				psInsert.setString(4, resident);
				psInsert.executeUpdate();

			} else {
				String sqlUpdate = "update vehicles set image = ?, entered = ?, resident = ? where licensePlate = ?";
				PreparedStatement ps1 = connection.prepareStatement(sqlUpdate);
				ps1.setString(1, image);
				ps1.setLong(2, time);
				ps1.setString(3, resident);
				ps1.setString(4, plate);
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
	
	public boolean checkSantioned(String plate) {
		boolean sanction = false;
		
		String sqlSelect = "select * from vehicles where licensePlate = ?";
		
		System.out.println(plate + " has left the area");
		
		try {
			plate = Encryptation.Encrypt(plate);
			
			PreparedStatement ps = connection.prepareStatement(sqlSelect);
			ps.setString(1, plate);
			ResultSet rs = ps.executeQuery();
			
			while (rs.next()) {
				if (rs.getString("licensePlate").equals(plate)) {
					
					String resident = rs.getString("resident");
					if (resident.equals("No")) {
						System.out.println("Is not resident");
						
						long parking = rs.getLong("parking");
						if (parking == 0) {
							System.out.println("Santion: Enter without being a resident and not visit a parking");
							sanction = true;
							
						} else {
							long timeOut = rs.getLong("timeOut");
							
							if (timeOut - parking > 6000) {
								System.out.println("Santion: Take too long to leave the area");
								sanction = true;
							} else {
								System.out.println("Vehicle left not santioned");
							}
							
						}
					} else {
						System.out.println("Is redident, vehicle not santioned");
					}
				}
			}
			
		} catch (Exception e) {
			e.printStackTrace();
		}
		
		return sanction;
	}
	
	public String getParking(String plate) {
		String sqlSelect = "select * from vehicles where licensePlate = ?";
		String result = null;
		
		try {
			plate = Encryptation.Encrypt(plate);
			
			PreparedStatement ps = connection.prepareStatement(sqlSelect);
			ps.setString(1, plate);
			ResultSet rs = ps.executeQuery();
			
			while (rs.next()) {
				if (rs.getString("licensePlate").equals(plate)) {
					result = rs.getString("resident");
				}
			}
			
		} catch (Exception e) {
			e.printStackTrace();
		}
		
		return result;
	}
	
	public void leaveParking(String plate, long time) {
		try {
			String sqlUpdate = "update vehicles set parking = ? where licensePlate = ?";
			PreparedStatement ps1 = connection.prepareStatement(sqlUpdate);
			ps1.setLong(1, time);
			ps1.setString(2, plate);
			ps1.executeUpdate();
		} catch (SQLException e) {
			e.printStackTrace();
		}

		
	}

}
