package parking.service;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;

import parking.service.Connect;

public class ProxyServiceWriteData {
	Connection connection;

	public ProxyServiceWriteData() {
		connection = new Connect().getConnection();
	}
	//FUNCTIONS MySQL
	public void writeInputLog(String data, long date) {

		try {
			String sqlInsert = "INSERT INTO inputLog (data, date) VALUES(?, ?)";
			PreparedStatement ps = connection.prepareStatement(sqlInsert);
			
			ps.setString(1, data);
			ps.setLong(2, date);
			//ps.setString(3, time);
			
			ps.executeUpdate();
			
		} catch (SQLException e) {
			System.out.println(e.getMessage());
		}
	}
	
	public void writeOutputLog(String data, long date) {

		try {
			String sqlInsert = "INSERT INTO outputlog (data, date) VALUES(?, ?)";
			PreparedStatement ps = connection.prepareStatement(sqlInsert);
			
			ps.setString(1, data);
			ps.setLong(2, date);
			//ps.setString(3, time);
			
			ps.executeUpdate();
			
		} catch (SQLException e) {
			System.out.println(e.getMessage());
		}
	}
}
