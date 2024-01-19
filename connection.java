package bank;

import java.sql.Connection;
import java.sql.DriverManager;

// Global connection Class
public class connection {
	public static Connection getConnection() {
		Connection connection = null;

		try {
			Class.forName("org.postgresql.Driver");
			connection = DriverManager.getConnection("jdbc:postgresql://localhost:5432/bank", "joosunhwang",
					"harry8595");
			if (connection != null) {
				System.out.println("good");
			} else {
				System.out.println("fail");

			}

		} catch (Exception e) {
			System.out.println(e);

		}
		return connection;

	}
}
