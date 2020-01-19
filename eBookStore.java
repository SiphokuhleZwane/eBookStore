import java.sql.*;
import javax.swing.*;

public class eBookStore {
	static Statement stmt = null; // object for database connection
	static JFrame f  = null; // object for display frame

	// connecting to database 
	public static void connect() {
		while (true) {
			String username = JOptionPane.showInputDialog(f, "Enter username for database:");
			String password = JOptionPane.showInputDialog(f,"Enter password:");
			
			// cancel clicked on either username or password
			if (username == null || password == null) {
				System.exit(0);
			}
			// allocate a database 'Connection' object
			Connection conn;
			try {
				conn = DriverManager.getConnection("jdbc:mysql://localhost:3306/ebookstore?useSSL=false", 
						username, password); // Enter database details
				stmt = conn.createStatement(); 
				break;
			} catch (SQLException e) {
				JOptionPane.showMessageDialog(f, "Incorrect password or username", "Alert", JOptionPane.WARNING_MESSAGE);
			}
		}
	}

	// SQL statements for inserting new entries in database
	public static void insert() {
		String insertID = JOptionPane.showInputDialog(f,"Enter unique ID number for book:");
		String insertTitle = JOptionPane.showInputDialog(f,"Enter title for book:");
		String insertAuth = JOptionPane.showInputDialog(f, "Enter author for book:");
		String insertQty = JOptionPane.showInputDialog(f,"Enter the quantity of the book:");

		String strInsert = "insert into books values (" + insertID + ", '" + insertTitle + "'" 
				+ ", '" + insertAuth + "'" + ", " + insertQty + ")" ;
		String option =  "Insert Option";

		execute(strInsert, option, "", "");
	}

	// SQL statements for updating entries in database
	public static void update() {
		String updSet = attribute("Enter attribute name you want to change:");
		String set = JOptionPane.showInputDialog(f, "Enter new value for attribute:\r\n");
		String updWhere = attribute("Enter attribute name where you want the change to occur:");
		String where = JOptionPane.showInputDialog(f, "Enter value of the attribute:");

		if (updSet.equals("Title") || updSet.equals("Author")) // attributes type string
			set = "'" + set + "'";

		String strUpdate = "update books set " + updSet + " = " + set +  " where " + updWhere + " = ";
		String option =  "Update Option";

		execute(strUpdate, option, updWhere, where);
	}

	// SQL statements for deleting in database
	public static void delete() {
		String delWhere = attribute("Enter attribute name where you want the delete to occur:");
		String where = JOptionPane.showInputDialog(f, "Enter value of the attribute:");

		String strDelete = "delete from books where " + delWhere + " = ";
		String option =  "Delete Option";

		execute(strDelete, option, delWhere, where);
	}

	// SQL statements for searching in database
	public static void search() {
		String searchWhere = attribute("Enter attribute name where you want the search to occur: \n(leave blank for entire books)");
		String where = JOptionPane.showInputDialog(f, "Enter value of the attribute: \n(leave blank for entire books)");
		
		String option =  "Search Option";
		results(option, searchWhere, where);
	}

	// SQL statements execution
	public static void execute(String sqlStmt, String option, String attrWhere, String where) {
		try {
			String strSelect;
			if (option.equals("Insert Option")) // special case (no adjustments needed)  
				strSelect = sqlStmt;
			else if (attrWhere.equals("Title") || attrWhere.equals("Author")) // attributes type string
				strSelect = sqlStmt + "'" + where + "'";
			else
				strSelect = sqlStmt + where;

			JOptionPane.showMessageDialog(f, "The SQL is: " + strSelect, option, JOptionPane.INFORMATION_MESSAGE); // displaying SQL statement

			int count = stmt.executeUpdate(strSelect); // SQL statement executor

			if (count == 0) {
				JOptionPane.showMessageDialog(f, count + " record affected."); // no results if not affected
			}
			else if (count == 1) {
				JOptionPane.showMessageDialog(f, count + " record affected.");  // grammar 
				results(option, attrWhere, where);
			} else {
				JOptionPane.showMessageDialog(f, count + " records affected.");
				results(option, attrWhere, where);
			}
		}catch(SQLException ex) {
			JOptionPane.showMessageDialog(f,"Incorrect entry","Alert",JOptionPane.WARNING_MESSAGE);  
		}
	}

	// SQL statements for results display
	public static void results(String option, String attrWhere, String where) {
		try {
			String strSelect;
			// attributes type string and delete as row is deleted
			if (attrWhere.equals("") || where.contentEquals("") || option.equals("Delete Option"))
				strSelect = "select * from books";
			else
				if (attrWhere.equals("Title") || attrWhere.equals("Author")) // attributes type string
					strSelect = "select * from books where " + attrWhere + " = " + "'" + where + "'";
				else
					strSelect = "select * from books where " + attrWhere + " = " + where;

			ResultSet rset = stmt.executeQuery(strSelect); // SQL statement executor
			String finalStrSelect = ""; 
			while(rset.next()) { // Move the cursor to the next row
				finalStrSelect += rset.getInt("id") + ", "
						+ rset.getString("title") + ", "
						+ rset.getString("author") + ", "
						+ rset.getInt("qty") + "\n";
			}
			JOptionPane.showMessageDialog(f, finalStrSelect, "Resulting Table", JOptionPane.INFORMATION_MESSAGE); // displaying resulting table

		}catch(SQLException ex) {
			JOptionPane.showMessageDialog(f,"Incorrect entry","Alert",JOptionPane.WARNING_MESSAGE);  
		}
	}

	// attributes table
	public static String attribute(String question) {
		return JOptionPane.showInputDialog(f, question + "\r\n"
				+ "id\r\n"
				+ "Title\r\n"
				+ "Author\r\n"
				+ "Qty\r\n");
	}

	public static void main (String args []) {
		f  = new JFrame("Ebooks Store"); // creating frame for display
		connect(); // connecting to database

		// Menu option
		while (true) {
			String choice = JOptionPane.showInputDialog(f,"Ebook Store Menu (select number):\r\n"
					+ "1. Enter book\r\n"  
					+ "2. Update book\r\n"  
					+ "3. Delete book\r\n"  
					+ "4. Search books\r\n" 
					+ "0. Exit\r\n");

			if (choice.equals("0")) {
				try {
					stmt.close();
				} catch (SQLException e) {
					JOptionPane.showMessageDialog(f, "Connection failed to close", "Alert", JOptionPane.WARNING_MESSAGE);
				}
				System.exit(0);
			}
			else if (choice.equals("1")) {
				insert();
			}
			else if (choice.equals("2"))
				update();

			else if (choice.equals("3")) {
				delete();
			}
			else if (choice.equals("4")) {
				search();
			}
			else 
				JOptionPane.showMessageDialog(f, "Incorrect option selected", "Alert", JOptionPane.WARNING_MESSAGE);
		}	
	}
}