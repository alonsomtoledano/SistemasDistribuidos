package clock;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class ClockService {
	Connection connection;

	public ClockService() {
		connection = new Connect().getConnection();
	}
	//FUNCTIONS MySQL
	public void setError(long st, long at, long error) {
		try {
			String sqlInsert = "INSERT INTO clock_table (serverTime, adjustTime, error) VALUES(?, ?, ?)";
			PreparedStatement ps = connection.prepareStatement(sqlInsert);
			
			ps.setLong(1,st);
			ps.setLong(2, at);
			ps.setLong(3, error);
			
			ps.executeUpdate();
			
		} catch (SQLException e) {
			System.out.println(e.getMessage());
		}
	}
	
	public List getAverage() {
		List <Long> result = new ArrayList<Long>();
		String sqlSelect = "select * from clock_table order by error";
		
		try {
			PreparedStatement ps = connection.prepareStatement(sqlSelect);
			ResultSet rs = ps.executeQuery();
			while (rs.next()) {;
				result.add(rs.getLong("error"));
			}
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return result;
	}
}