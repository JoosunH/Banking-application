package bank;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.SQLIntegrityConstraintViolationException;
import java.sql.Statement;

public class Banking { // these class provides all
						// bank method

	private static final int NULL = 0;

	static Connection con = connection.getConnection();
	static String sql = "";

	public static boolean createAccount(String name, int passCode) {
		try {
			if (name == null || passCode == NULL) {
				System.out.println("All Field Required!");
				return false;
			}

			// customer is a table name, cname,balance,pass_code are column name, and
			// putting money in to balance columns
			String sql = "INSERT INTO customer(cname, balance, pass_code) VALUES (?, 50000, ?)";
			try (PreparedStatement st = con.prepareStatement(sql, new String[] { "ac_no" })) {
				// now we are putting the input into first ? value which is cname to name as our
				// input
				st.setString(1, name);
				// same thing but this time now we are putting the value passCode into second ?
				st.setInt(2, passCode);
				// This line executes the SQL update (INSERT) statement. It returns the number
				// of rows that were affected by the execution
				int affectedRows = st.executeUpdate();
				// if there is an update than look for a key. Since we said "ac_no" than its a
				// key
				if (affectedRows > 0) {
					ResultSet generatedKeys = st.getGeneratedKeys();
					// the block fetches a key and return true
					if (generatedKeys.next()) {
						int ac_no = generatedKeys.getInt(1);
						System.out.println(name + ", Your account (ac_no=" + ac_no + ") has been created!");
						return true;
					}
				}
			}
		} catch (SQLIntegrityConstraintViolationException e) {
			System.out.println("Username Not Available!");
		} catch (Exception e) {
			e.printStackTrace();
		}
		return false;
	}

	public static boolean loginAccount(String name, int passCode) {
		try {
			// validation
			if (name.equals("") || passCode == NULL) {
				System.out.println("All Fields Required!");
				return false;
			}

			// query
			// selecting a query where cname = name and pass_code = passCode
			String sql = "SELECT * FROM customer WHERE cname = ? AND pass_code = ?";
			try (PreparedStatement st = con.prepareStatement(sql)) {
				st.setString(1, name);
				st.setInt(2, passCode);

				ResultSet rs = st.executeQuery();
				// Execution
				BufferedReader sc = new BufferedReader(new InputStreamReader(System.in));

				if (rs.next()) {
					// after login menu-driven interface method

					int ch = 5;
					int amt = 0;
					int senderAc = rs.getInt("ac_no");
					int receiveAc;
					while (true) {
						try {
							System.out.println("Hello, " + rs.getString("cname"));
							System.out.println("1) Transfer Money");
							System.out.println("2) View Balance");
							System.out.println("5) LogOut");

							System.out.print("Enter Choice:");
							ch = Integer.parseInt(sc.readLine());
							if (ch == 1) {
								System.out.print("Enter Receiver A/c No:");
								receiveAc = Integer.parseInt(sc.readLine());
								System.out.print("Enter Amount:");
								amt = Integer.parseInt(sc.readLine());

								if (Banking.transferMoney(senderAc, receiveAc, amt)) {
									System.out.println("MSG: Money Sent Successfully!\n");
								} else {
									System.out.println("ERR: Failed!\n");
								}
							} else if (ch == 2) {
								Banking.getBalance(senderAc);
							} else if (ch == 5) {
								break;
							} else {
								System.out.println("Err: Enter Valid input!\n");
							}
						} catch (Exception e) {
							e.printStackTrace();
						}
					}
				} else {
					return false;
				}
			} catch (SQLException e) {
				e.printStackTrace();
			}
			// return
			return true;
		} catch (Exception e) {
			e.printStackTrace();
		}
		return false;
	}

	public static void getBalance(int acNo) {
		try {
			String sql = "SELECT * FROM customer WHERE ac_no=" + acNo;
			try (Statement st = con.createStatement(); ResultSet rs = st.executeQuery(sql)) {

				System.out.println("-----------------------------------------------------------");
				System.out.printf("%12s %10s %10s\n", "Account No", "Name", "Balance");

				while (rs.next()) {
					System.out.printf("%12d %10s %10d.00\n", rs.getInt("ac_no"), rs.getString("cname"),
							rs.getInt("balance"));
				}
				System.out.println("-----------------------------------------------------------\n");
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public static boolean transferMoney(int sender_ac, int reveiver_ac, int amount) throws SQLException {
	    // Validation
	    if (reveiver_ac == NULL || amount == NULL) {
	        System.out.println("All Field Required!");
	        return false;
	    }

	    try {
	        con.setAutoCommit(false);

	        // Check sender's balance
	        String checkBalanceSql = "SELECT balance FROM customer WHERE ac_no = ?";
	        try (PreparedStatement checkBalanceStatement = con.prepareStatement(checkBalanceSql)) {
	            checkBalanceStatement.setInt(1, sender_ac);
	            ResultSet rs = checkBalanceStatement.executeQuery();

	            if (rs.next()) {
	                int senderBalance = rs.getInt("balance");
	                if (senderBalance < amount) {
	                    System.out.println("Insufficient Balance!");
	                    return false;
	                }
	            }
	        }

	        // Debit
	        String debitSql = "UPDATE customer SET balance = balance - ?::integer WHERE ac_no = ?";
	        try (PreparedStatement debitStatement = con.prepareStatement(debitSql)) {
	            debitStatement.setInt(1, amount);
	            debitStatement.setInt(2, sender_ac);
	            debitStatement.executeUpdate();
	            System.out.println("Amount Debited!");
	        }

	        // Credit
	        String creditSql = "UPDATE customer SET balance = balance - ?::integer WHERE ac_no = ?";
	        try (PreparedStatement creditStatement = con.prepareStatement(creditSql)) {
	            creditStatement.setInt(1, amount);
	            creditStatement.setInt(2, reveiver_ac);
	            creditStatement.executeUpdate();
	        }

	        con.commit();
	        return true;
	    } catch (Exception e) {
	        e.printStackTrace();
	        con.rollback();
	    } finally {
	        con.setAutoCommit(true);  // Reset auto-commit mode
	    }

	    return false;
	}

}
