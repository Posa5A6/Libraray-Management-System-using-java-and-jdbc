import java.sql.*;
import java.util.Scanner;
import java.util.logging.Level;
import java.util.logging.Logger;

public class AdminLogin {
    
    /**
     *
     */
    public static Scanner scan = new Scanner(System.in);
    public static void main(String[] args) {
         String jdbcDriver = "oracle.jdbc.driver.OracleDriver";
     String dbURL = "jdbc:oracle:thin:@localhost:1521:XE";
     String user = "system";
     String pass = "1234";
     
        Connection conn = null;
        try {
            // Load the JDBC driver
            Class.forName(jdbcDriver);
            System.out.println("Connecting to Admin database...");

            // Establish a connection
            conn = DriverManager.getConnection(dbURL, user, pass);
            System.out.println("Connection to Admin established!");

            // Check if Admin table exists, if not, create it
            if (!isAdminTablePresent(conn)) {
                createAdminTable(conn);
            } else {
                System.out.println("Admin table already exists.");
            }

            // Navigate to view options
            viewOptions(conn);

        } catch (ClassNotFoundException e) {
            System.err.println("JDBC Driver not found: " + e.getMessage());
        } catch (SQLException e) {
            System.err.println("SQL error: " + e.getMessage());
        } finally {
            // Close resources
            closeResources(conn);
        }
    }

    private static boolean isAdminTablePresent(Connection conn) {
        String checkTableSQL = "SELECT COUNT(*) FROM user_tables WHERE table_name = 'ADMIN'";
        try (Statement stmt = conn.createStatement(); ResultSet rs = stmt.executeQuery(checkTableSQL)) {
            if (rs.next()) {
                return rs.getInt(1) > 0;
            }
        } catch (SQLException e) {
            System.err.println("Error checking Admin table presence: " + e.getMessage());
        }
        return false;
    }

    private static void createAdminTable(Connection conn) {
        String sqlq = "CREATE TABLE Admin (admin_id INT PRIMARY KEY, name VARCHAR(20), email VARCHAR(50), phone VARCHAR(15), password VARCHAR(20))";
        try (Statement stmt = conn.createStatement()) {
            stmt.executeUpdate(sqlq);
            System.out.println("Admin table created successfully...");
        } catch (SQLException e) {
            System.err.println("Error creating Admin table: " + e.getMessage());
        }
    }

    private static void viewOptions(Connection conn) {
        while (true) {
            System.out.println("\nChoose an option:");
            System.out.println("1. Insert Admin Record");
            System.out.println("2. Update Admin Record");
            System.out.println("3. Delete Admin Record");
            System.out.println("4. Display Admin Records");
            System.out.println("5. Check User Availability and Login to Admin");
            System.out.println("6. Exit");
            System.out.print("Enter your choice: ");
            int choice = scan.nextInt();
            scan.nextLine(); // Consume newline

            switch (choice) {
                case 1 -> insertAdminRecord(conn);
                case 2 -> updateAdminRecord(conn);
                case 3 -> deleteAdminRecord(conn);
                case 4 -> displayAdminRecords(conn);
                case 5 -> checkUserAvailability(conn);
                case 6 -> {
                    System.out.println("Exiting...");
                    return; // Exit the loop and terminate the program
                }
                default -> System.out.println("Invalid choice. Please try again.");
            }
        }
    }

    private static void insertAdminRecord(Connection conn) {
        String insertSQL = "INSERT INTO Admin (admin_id, name, email, phone, password) VALUES (?, ?, ?, ?, ?)";
        try (PreparedStatement pstmt = conn.prepareStatement(insertSQL)) {
            System.out.print("Enter Admin ID: ");
            int adminId = scan.nextInt();
            scan.nextLine(); // Consume newline

            System.out.print("Enter Name: ");
            String name =scan.nextLine();

            System.out.print("Enter Email: ");
            String email = scan.nextLine();

            System.out.print("Enter Phone Number: ");
            String phone = scan.nextLine();

            System.out.print("Enter Password: ");
            String password = scan.nextLine();

            pstmt.setInt(1, adminId);
            pstmt.setString(2, name);
            pstmt.setString(3, email);
            pstmt.setString(4, phone);
            pstmt.setString(5, password);

            pstmt.executeUpdate();
            System.out.println("Admin record inserted successfully!");
        } catch (SQLException e) {
            System.err.println("Error inserting record: " + e.getMessage());
        } catch (Exception e) {
            System.err.println("Unexpected error: " + e.getMessage());
        }
    }

    private static void updateAdminRecord(Connection conn) {
        System.out.print("Enter Admin ID to update: ");
        int adminId = scan.nextInt();
        scan.nextLine(); // Consume newline

        // Prompt for new values
        System.out.print("Enter new Name (leave blank to keep current): ");
        String name = scan.nextLine();

        System.out.print("Enter new Email (leave blank to keep current): ");
        String email = scan.nextLine();

        System.out.print("Enter new Phone Number (leave blank to keep current): ");
        String phone = scan.nextLine();

        System.out.print("Enter new Password (leave blank to keep current): ");
        String password = scan.nextLine();

        // Build the update SQL query dynamically based on provided values
        StringBuilder updateSQL = new StringBuilder("UPDATE Admin SET ");
        boolean first = true;

        if (!name.isEmpty()) {
            updateSQL.append("name = ?, ");
            first = false;
        }
        if (!email.isEmpty()) {
            updateSQL.append("email = ?, ");
            first = false;
        }
        if (!phone.isEmpty()) {
            updateSQL.append("phone = ?, ");
            first = false;
        }
        if (!password.isEmpty()) {
            updateSQL.append("password = ?, ");
            first = false;
        }

        // Remove the last comma and space
        if (!first) {
            updateSQL.setLength(updateSQL.length() - 2); // Remove last comma and space
        } else {
            System.out.println("No fields to update.");
            return; // Exit if no fields were provided
        }

        updateSQL.append(" WHERE admin_id = ?");

        try (PreparedStatement pstmt = conn.prepareStatement(updateSQL.toString())) {
            int parameterIndex = 1;

            if (!name.isEmpty()) {
                pstmt.setString(parameterIndex++, name);
            }
            if (!email.isEmpty()) {
                pstmt.setString(parameterIndex++, email);
            }
            if (!phone.isEmpty()) {
                pstmt.setString(parameterIndex++, phone);
            }
            if (!password.isEmpty()) {
                pstmt.setString(parameterIndex++, password);
            }

            pstmt.setInt(parameterIndex, adminId); // Set the admin_id for the WHERE clause

            int affectedRows = pstmt.executeUpdate();
            if (affectedRows > 0) {
                System.out.println("Admin record updated successfully!");
            } else {
                System.out.println("No record found with Admin ID: " + adminId);
            }
        } catch (SQLException e) {
            System.err.println("Error updating record: " + e.getMessage());
        } catch (Exception e) {
            System.err.println("Unexpected error: " + e.getMessage());
        }
    }

    private static void deleteAdminRecord(Connection conn) {
        System.out.print("Enter Admin ID to delete: ");
        int adminId = scan.nextInt();
        scan.nextLine(); // Consume newline

        String deleteSQL = "DELETE FROM Admin WHERE admin_id = ?";
        try (PreparedStatement pstmt = conn.prepareStatement(deleteSQL)) {
            pstmt.setInt(1, adminId);

            int affectedRows = pstmt.executeUpdate();
            if (affectedRows > 0) {
                System.out.println("Admin record deleted successfully!");
            } else {
                System.out.println("No record found with Admin ID: " + adminId);
            }
        } catch (SQLException e) {
            System.err.println("Error deleting record: " + e.getMessage());
        } catch (Exception e) {
            System.err.println("Unexpected error: " + e.getMessage());
        }
    }

    private static void displayAdminRecords(Connection conn) {
        String selectSQL = "SELECT * FROM Admin";
        try (Statement stmt = conn.createStatement(); ResultSet rs = stmt.executeQuery(selectSQL)) {
            while (rs.next()) {
                System.out.println("Admin ID: " + rs.getInt("admin_id"));
                System.out.println("Name: " + rs.getString("name"));
                System.out.println("Email: " + rs.getString("email"));
                System.out.println("Phone: " + rs.getString("phone"));
                System.out.println("Password: " + rs.getString("password"));
                System.out.println("-------------------------------");
            }
        } catch (SQLException e) {
            System.err.println("Error displaying records: " + e.getMessage());
        } catch (Exception e) {
            System.err.println("Unexpected error: " + e.getMessage());
        }
    }

    private static void checkUserAvailability(Connection conn) {
        System.out.print("Enter Email or Phone Number: ");
        String identifier = scan.nextLine();

        System.out.print("Enter Password: ");
        String password = scan.nextLine();

        String query = "SELECT * FROM Admin WHERE (email = ? OR phone = ?) AND password = ?";
        try (PreparedStatement pstmt = conn.prepareStatement(query)) {
            pstmt.setString(1, identifier);
            pstmt.setString(2, identifier);
            pstmt.setString(3, password);

            ResultSet rs = pstmt.executeQuery();

            if (rs.next()) {
                System.out.println("User available:");
                System.out.println("Name: " + rs.getString("name"));
                System.out.println("Admin ID: " + rs.getInt("admin_id"));
                System.out.println("going to login ");
                Login();
                // Additional functionality can be added here, e.g., navigating to another program
            } else {
                System.out.println("No user found. Please re-enter login details.");
                System.out.print("Press 1 to retry or any other key to return to the options: ");
                int retryChoice = scan.nextInt();
                scan.nextLine(); // Consume newline
                if (retryChoice == 1) {
                    checkUserAvailability(conn); // Retry login
                } else {
                    viewOptions(conn);
                }
            }
        } catch (SQLException e) {
            System.err.println("Error checking user availability: " + e.getMessage());
        } catch (Exception e) {
            System.err.println("Unexpected error: " + e.getMessage());
        }
    }

    private static void closeResources(Connection conn) {
        try {
            if (conn != null) conn.close();
        } catch (SQLException e) {
            System.err.println("Error closing resources: " + e.getMessage());
        } catch (Exception e) {
            System.err.println("Unexpected error while closing resources: " + e.getMessage());
        }
    }

    public static void Login() {
        Books b=new Books();
        try {
            b.start();
        } catch (SQLException ex) {
            Logger.getLogger(AdminLogin.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
}
