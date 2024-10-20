import java.sql.*;
import java.util.Scanner;

public class Books {
    public  Scanner scanner = new Scanner(System.in);

    // Entry point for the Books program
    public  void start() throws SQLException {
        // Start the menu loop
        while (true) {
            menu();
            System.out.println("press  1 to stay in the books page ");
            int i=scanner.nextInt();
            if(i==1){}
            else{
                return;}
            
        }
        }
    

    // Main menu for books operations
    public  void menu() throws SQLException {
        try {
            // Check if the books table exists, if not, create it
            if (!checkTableExists("books")) {
                createBooksTable();
            }

            // Display the menu options
            System.out.println("0. search book");
            System.out.println("1. Insert Book");
            System.out.println("2. Update Book");
            System.out.println("3. Delete Book");
            System.out.println("4. View All Books");
            System.out.println("5. Go to Student Details");
            System.out.println("6. Exit"); // Added exit option

            int choice = scanner.nextInt();
            scanner.nextLine(); // Consume newline

            switch (choice) {
                case 0 -> searchBook();
                case 1 -> insertBook();
                case 2 -> updateBook();
                case 3 -> deleteBook();
                case 4 -> viewBooks();
                case 5 -> {
                    Students studentsProgram = new Students();
                    studentsProgram.menus();
                }
                case 6 -> {
                    System.out.println("Exiting Books program...");
                    return; // Exit the menu loop
                }
                default -> System.out.println("Invalid choice.");
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    boolean checkTableExists(String tableName) throws SQLException {
        try (Connection conn = getConnection();
             ResultSet tables = conn.getMetaData().getTables(null, null, tableName.toUpperCase(), null)) {
            return tables.next();
        }
    }

    void createBooksTable() throws SQLException {
        try (Connection conn = getConnection();
             Statement stmt = conn.createStatement()) {
            String createTableSQL = "CREATE TABLE books (bookname VARCHAR(50), bookid VARCHAR(20) PRIMARY KEY, noofbooks INT)";
            stmt.executeUpdate(createTableSQL);
            System.out.println("Books table created successfully.");
        } catch (SQLException e) {
            System.err.println("Error creating table: " + e.getMessage());
        }
    }

    void insertBook() throws SQLException {
        try (Connection conn = getConnection()) {
            System.out.print("Enter book name: ");
            String bookName = scanner.nextLine();
            System.out.print("Enter book ID: ");
            String bookId = scanner.nextLine();
            System.out.print("Enter number of books available: ");
            int noOfBooks = scanner.nextInt();
            scanner.nextLine(); // Consume newline

            String insertSQL = "INSERT INTO books (bookname, bookid, noofbooks) VALUES (?, ?, ?)";
            try (PreparedStatement pstmt = conn.prepareStatement(insertSQL)) {
                pstmt.setString(1, bookName);
                pstmt.setString(2, bookId);
                pstmt.setInt(3, noOfBooks);
                pstmt.executeUpdate();
                System.out.println("Book inserted successfully.");
            } catch (SQLException e) {
                System.err.println("Error inserting book: " + e.getMessage());
            }
        }
    }

    void updateBook() throws SQLException {
        try (Connection conn = getConnection()) {
            System.out.print("Enter book ID to update: ");
            String bookId = scanner.nextLine();
            System.out.print("Enter new number of books available: ");
            int noOfBooks = scanner.nextInt();
            scanner.nextLine(); // Consume newline

            String updateSQL = "UPDATE books SET noofbooks = ? WHERE bookid = ?";
            try (PreparedStatement pstmt = conn.prepareStatement(updateSQL)) {
                pstmt.setInt(1, noOfBooks);
                pstmt.setString(2, bookId);
                int rowsUpdated = pstmt.executeUpdate();
                if (rowsUpdated > 0) {
                    System.out.println("Book updated successfully.");
                } else {
                    System.out.println("Book ID not found.");
                }
            } catch (SQLException e) {
                System.err.println("Error updating book: " + e.getMessage());
            }
        }
    }

    void deleteBook() throws SQLException {
        try (Connection conn = getConnection()) {
            System.out.print("Enter book ID to delete: ");
            String bookId = scanner.nextLine();

            String deleteSQL = "DELETE FROM books WHERE bookid = ?";
            try (PreparedStatement pstmt = conn.prepareStatement(deleteSQL)) {
                pstmt.setString(1, bookId);
                int rowsDeleted = pstmt.executeUpdate();
                if (rowsDeleted > 0) {
                    System.out.println("Book deleted successfully.");
                } else {
                    System.out.println("Book ID not found.");
                }
            } catch (SQLException e) {
                System.err.println("Error deleting book: " + e.getMessage());
            }
        }
    }
void searchBook() throws SQLException {
    try (Connection conn = getConnection()) {
        System.out.print("Enter book name to search: ");
        String bookName = scanner.nextLine();

        String searchSQL = "SELECT * FROM books WHERE bookname LIKE ?";
        try (PreparedStatement pstmt = conn.prepareStatement(searchSQL)) {
            pstmt.setString(1, "%" + bookName + "%");
            try (ResultSet rs = pstmt.executeQuery()) {
                boolean found = false;
                while (rs.next()) {
                    found = true;
                    System.out.println("Book Name: " + rs.getString("bookname") +
                                       ", Book ID: " + rs.getString("bookid") +
                                       ", No. of Books: " + rs.getInt("noofbooks"));
                }
                if (!found) {
                    System.out.println("No books found with that name.");
                }
            }
        }
    } catch (SQLException e) {
        System.err.println("Error searching for book: " + e.getMessage());
    }
}

    void viewBooks() throws SQLException {
        try (Connection conn = getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery("SELECT * FROM books")) {
            while (rs.next()) {
                System.out.println("Book Name: " + rs.getString("bookname") +
                                   ", Book ID: " + rs.getString("bookid") +
                                   ", No. of Books: " + rs.getInt("noofbooks"));
            }
        } catch (SQLException e) {
            System.err.println("Error viewing books: " + e.getMessage());
        }
    }

    Connection getConnection() throws SQLException {
        String jdbcDriver = "oracle.jdbc.driver.OracleDriver";
        String dbURL = "jdbc:oracle:thin:@localhost:1521:XE";
        String user = "system";
        String pass = "1234";
        return DriverManager.getConnection(dbURL, user, pass);
    }
}
