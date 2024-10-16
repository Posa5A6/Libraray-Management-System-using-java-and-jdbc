import java.sql.*;
import java.util.*;

public class Students {
    Scanner scanner = new Scanner(System.in);
    Books b = new Books();

    // Main menu for students operations
    void menus() {
        try {
            // Check if the students table exists, if not, create it
            if (!checkTableExists("students")) {
                createStudentsTable();
            }
            System.out.println("1. Insert Student");
            System.out.println("2. Update Student");
            System.out.println("3. Delete Student");
            System.out.println("4. View Student Details");
            System.out.println("5. Borrow Book");
            System.out.println("6. Return Book");
            System.out.println("7. Go to Books Manager");

            int choice = scanner.nextInt();
            scanner.nextLine(); // Consume newline

            switch (choice) {
                case 1 -> insertStudent();
                case 2 -> updateStudent();
                case 3 -> deleteStudent();
                case 4 -> viewStudents();
                case 5 -> borrowBook();
                case 6 -> returnBooks();
                case 7 -> b.menu();
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

    void createStudentsTable() throws SQLException {
        try (Connection conn = getConnection();
             Statement stmt = conn.createStatement()) {
            String createTableSQL = "CREATE TABLE students (name VARCHAR(50), id VARCHAR(20) PRIMARY KEY, email VARCHAR(50), phone VARCHAR(15), noofcards INT, booksborrowed INT, returnbooks INT DEFAULT 0)";
            stmt.executeUpdate(createTableSQL);
            System.out.println("Students table created successfully.");
        }
    }

    void insertStudent() throws SQLException {
        try (Connection conn = getConnection()) {
            System.out.print("Enter student name: ");
            String name = scanner.nextLine();
            System.out.print("Enter student ID: ");
            String id = scanner.nextLine();
            System.out.print("Enter student email: ");
            String email = scanner.nextLine();
            System.out.print("Enter student phone: ");
            String phone = scanner.nextLine();
            System.out.print("Enter number of cards available: ");
            int noOfCards = scanner.nextInt();
            System.out.print("Enter number of books borrowed: ");
            int booksBorrowed = scanner.nextInt();
            int returnBooks = 0; // Initialize return books to 0

            String insertSQL = "INSERT INTO students (name, id, email, phone, noofcards, booksborrowed, returnbooks) VALUES (?, ?, ?, ?, ?, ?, ?)";
            try (PreparedStatement pstmt = conn.prepareStatement(insertSQL)) {
                pstmt.setString(1, name);
                pstmt.setString(2, id);
                pstmt.setString(3, email);
                pstmt.setString(4, phone);
                pstmt.setInt(5, noOfCards);
                pstmt.setInt(6, booksBorrowed);
                pstmt.setInt(7, returnBooks);
                pstmt.executeUpdate();
                System.out.println("Student inserted successfully.");
                menus();
            }
        }
    }

    void updateStudent() throws SQLException {
        try (Connection conn = getConnection()) {
            System.out.print("Enter student ID to update: ");
            String id = scanner.nextLine();
            System.out.print("Enter new student name: ");
            String name = scanner.nextLine();
            System.out.print("Enter new student email: ");
            String email = scanner.nextLine();
            System.out.print("Enter new student phone: ");
            String phone = scanner.nextLine();
            System.out.print("Enter new number of cards available: ");
            int noOfCards = scanner.nextInt();
            System.out.print("Enter new number of books borrowed: ");
            int booksBorrowed = scanner.nextInt();

            String updateSQL = "UPDATE students SET name = ?, email = ?, phone = ?, noofcards = ?, booksborrowed = ? WHERE id = ?";
            try (PreparedStatement pstmt = conn.prepareStatement(updateSQL)) {
                pstmt.setString(1, name);
                pstmt.setString(2, email);
                pstmt.setString(3, phone);
                pstmt.setInt(4, noOfCards);
                pstmt.setInt(5, booksBorrowed);
                pstmt.setString(6, id);
                int rowsUpdated = pstmt.executeUpdate();
                if (rowsUpdated > 0) {
                    System.out.println("Student updated successfully.");
                } else {
                    System.out.println("Student ID not found.");
                }
                menus();
            }
        }
    }

    void deleteStudent() throws SQLException {
        try (Connection conn = getConnection()) {
            System.out.print("Enter student ID to delete: ");
            String id = scanner.nextLine();

            String deleteSQL = "DELETE FROM students WHERE id = ?";
            try (PreparedStatement pstmt = conn.prepareStatement(deleteSQL)) {
                pstmt.setString(1, id);
                int rowsDeleted = pstmt.executeUpdate();
                if (rowsDeleted > 0) {
                    System.out.println("Student deleted successfully.");
                } else {
                    System.out.println("Student ID not found.");
                }
                menus();
            }
        }
    }

    void viewStudents() throws SQLException {
        try (Connection conn = getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery("SELECT * FROM students")) {
            while (rs.next()) {
                System.out.println("Name: " + rs.getString("name") + ", ID: " + rs.getString("id") +
                                   ", Email: " + rs.getString("email") + ", Phone: " + rs.getString("phone") +
                                   ", Cards: " + rs.getInt("noofcards") + ", Books Borrowed: " + rs.getInt("booksborrowed") +
                                   ", Books Returned: " + rs.getInt("returnbooks"));
            }
            menus();
        }
    }

    void borrowBook() throws SQLException {
        try (Connection conn = getConnection()) {
            System.out.print("Enter student ID: ");
            String studentId = scanner.nextLine();
            System.out.print("Enter book ID to borrow: ");
            String bookId = scanner.nextLine();

            // Check if student ID exists
            if (!isStudentExists(studentId)) {
                System.out.println("Student ID not found.");
                return;
            }

            // Check if the book is available
            if (!isBookAvailable(bookId)) {
                System.out.println("Book not available. Please add more copies in Books section.");
                Books booksProgram = new Books();
                booksProgram.menu();
                return;
            }

            // Update the books table by decreasing the available books count
            String updateBookSQL = "UPDATE books SET noofbooks = noofbooks - 1 WHERE bookid = ?";
            try (PreparedStatement bookStmt = conn.prepareStatement(updateBookSQL)) {
                bookStmt.setString(1, bookId);
                bookStmt.executeUpdate();
            }

            // Update the student's borrowed books count and reduce available cards
            String updateStudentSQL = "UPDATE students SET booksborrowed = booksborrowed + 1, noofcards = noofcards - 1 WHERE id = ?";
            try (PreparedStatement studentStmt = conn.prepareStatement(updateStudentSQL)) {
                studentStmt.setString(1, studentId);
                studentStmt.executeUpdate();
            }
            System.out.println("Book borrowed successfully.");
            menus();
        }
    }

    boolean isBookAvailable(String bookId) throws SQLException {
        try (Connection conn = getConnection();
             PreparedStatement pstmt = conn.prepareStatement("SELECT noofbooks FROM books WHERE bookid = ?")) {
            pstmt.setString(1, bookId);
            try (ResultSet rs = pstmt.executeQuery()) {
                if (rs.next()) {
                    return rs.getInt("noofbooks") > 0;
                }
            }
        }
        return false;
    }

    boolean isStudentExists(String studentId) throws SQLException {
        try (Connection conn = getConnection();
             PreparedStatement pstmt = conn.prepareStatement("SELECT id FROM students WHERE id = ?")) {
            pstmt.setString(1, studentId);
            try (ResultSet rs = pstmt.executeQuery()) {
                return rs.next(); // Returns true if a record is found
            }
        }
    }

    void returnBooks() throws SQLException {
        try (Connection conn = getConnection()) {
            System.out.print("Enter student ID: ");
            String studentId = scanner.nextLine();
            System.out.print("Enter book ID to return: ");
            String bookId = scanner.nextLine();

            // Check if student ID exists
            if (!isStudentExists(studentId)) {
                System.out.println("Student ID not found.");
                return;
            }

            // Update the books table by increasing the available books count
            String updateBookSQL = "UPDATE books SET noofbooks = noofbooks + 1 WHERE bookid = ?";
            try (PreparedStatement bookStmt = conn.prepareStatement(updateBookSQL)) {
                bookStmt.setString(1, bookId);
                bookStmt.executeUpdate();
            }

            // Update the student's borrowed books count and increase cards
            String updateStudentSQL = "UPDATE students SET booksborrowed = booksborrowed - 1, noofcards = noofcards + 1, returnbooks = returnbooks + 1 WHERE id = ?";
            try (PreparedStatement studentStmt = conn.prepareStatement(updateStudentSQL)) {
                studentStmt.setString(1, studentId);
                studentStmt.executeUpdate();
            }
            System.out.println("Book returned successfully.");
            menus();
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
