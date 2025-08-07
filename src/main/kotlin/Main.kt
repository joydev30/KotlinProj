import java.sql.Connection
import java.sql.DriverManager
import java.sql.PreparedStatement
import java.util.Scanner

fun main() {
    val scanner = Scanner(System.`in`)

    val url = "jdbc:h2:file:./data/testdb"
    val user = "sa"
    val password = ""

    DriverManager.getConnection(url, user, password).use { connection ->
        println("Connected to H2 In-Memory Database")
        createTables(connection)

        println("\n1. Create User\n2. Create Post")
        print("Enter your choice (1 or 2): ")
        val choice = scanner.nextLine()

        when (choice) {
            "1" -> createUser(connection, scanner)
            "2" -> createPost(connection, scanner)
            else -> println("Invalid choice. Please enter 1 or 2.")
        }
    }
}

fun createUser(connection: Connection, scanner: Scanner) {
    print("Enter username: ")
    val username = scanner.nextLine()

    val stmt = connection.prepareStatement(
        "INSERT INTO Users (username) VALUES (?)",
        PreparedStatement.RETURN_GENERATED_KEYS
    )
    stmt.setString(1, username)
    stmt.executeUpdate()

    val generatedKeys = stmt.generatedKeys
    if (generatedKeys.next()) {
        val userId = generatedKeys.getInt(1)
        println("User '$username' created with ID: $userId")
    } else {
        println("Failed to retrieve user ID.")
    }
}

fun createPost(connection: Connection, scanner: Scanner) {
    print("Enter user ID: ")
    val userId = scanner.nextLine().toIntOrNull()
    if (userId == null) {
        println("Invalid user ID.")
        return
    }

    print("Enter post description: ")
    val description = scanner.nextLine()

    print("Enter image URL (press Enter to skip): ")
    val imageUrlInput = scanner.nextLine()
    val imageUrl = if (imageUrlInput.isBlank()) null else imageUrlInput

    val stmt = connection.prepareStatement(
        "INSERT INTO Posts (user_id, description, image_url) VALUES (?, ?, ?)"
    )
    stmt.setInt(1, userId)
    stmt.setString(2, description)
    stmt.setString(3, imageUrl)
    stmt.executeUpdate()

    println("Post created for user ID $userId.")
}

fun createTables(connection: Connection) {
    val createUsersTableSQL = """
        CREATE TABLE IF NOT EXISTS Users (
            id INT AUTO_INCREMENT PRIMARY KEY,
            username VARCHAR(255) NOT NULL UNIQUE
        )
    """
    val createPostsTableSQL = """
        CREATE TABLE IF NOT EXISTS Posts (
            id INT AUTO_INCREMENT PRIMARY KEY,
            user_id INT,
            description VARCHAR(255),
            image_url VARCHAR(255),
            FOREIGN KEY (user_id) REFERENCES Users(id)
        )
    """
    connection.createStatement().use { statement ->
        statement.execute(createUsersTableSQL)
        statement.execute(createPostsTableSQL)
    }
} 
