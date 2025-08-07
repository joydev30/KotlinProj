import java.sql.Connection
import java.sql.DriverManager
import java.sql.PreparedStatement
import java.util.Scanner

fun main() {
    val scanner = Scanner(System.`in`)

    val url = "jdbc:sqlserver://localhost:1433;databaseName=KotlinAppDB;encrypt=true;trustServerCertificate=true"
    val user = "sa"
    val password = "YourStrong@Passw0rd"

    DriverManager.getConnection(url, user, password).use { connection ->
        println("Connected to SQL Server!")

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
