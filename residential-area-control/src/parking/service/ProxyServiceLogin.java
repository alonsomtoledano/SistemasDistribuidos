package parking.service;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import parking.service.Connect;

public class ProxyServiceLogin {
	Connection connection;

	public ProxyServiceLogin() {
		connection = new Connect().getConnection();
	}
	//FUNCTIONS MySQL
	public boolean dataBaseAuthentication(String ip, String mac, int port) {
		boolean status = false;
		boolean auxPort = false;
		List<String> result = new ArrayList<String>();

		try {
			String sqlSelect = "select * from login where ip = ?";
			PreparedStatement ps = connection.prepareStatement(sqlSelect);
			ps.setString(1, ip);
			ResultSet rs = ps.executeQuery();
			while (rs.next()) {
				if((rs.getString("mac").equals(mac))) {
					result.add(rs.getString("ip") + " - "  +  " - " + rs.getString("mac"));
					auxPort = getPort(port);
					if(auxPort) {
						status = true;
					}else {
						status = false;
					}	
				}else {
					status = false;
				}
			}
		} catch (SQLException e) {
			System.out.println(e.getMessage());
		}
		return status;
	}
	
	public boolean getPort(int port) {
		boolean status = false;
		List<Integer> result = new ArrayList<Integer>();

		try {
			String sqlSelect = "select * from login where port = ?";
			PreparedStatement ps = connection.prepareStatement(sqlSelect);
			ps.setInt(1, port);
			ResultSet rs = ps.executeQuery();
			while (rs.next()) {
					result.add(rs.getInt("port"));
					status = true;
			}
		} catch (SQLException e) {
			System.out.println(e.getMessage());
		}
		return status;
	}
}