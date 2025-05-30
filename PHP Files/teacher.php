<?php
error_reporting(E_ALL);
ini_set('display_errors', 1);

// Database connection
$host = "localhost";
$user = "root"; // Change if needed
$password = "6165"; // Change if needed
$database = "rfid";

$conn = new mysqli($host, $user, $password, $database);

if ($conn->connect_error) {
    die("Database connection failed: " . $conn->connect_error);
}

if ($_SERVER["REQUEST_METHOD"] == "POST") {
    $Tr_name = isset($_POST['Tr_name']) ? trim($_POST['Tr_name']) : '';
    $Tr_id = isset($_POST['Tr_id']) ? trim($_POST['Tr_id']) : '';

    file_put_contents("debug_log.txt", "Received: Tr_name=$Tr_name, Tr_id=$Tr_id\n", FILE_APPEND);

    if (empty($Tr_name) || empty($Tr_id)) {
        echo "Invalid input";
        exit();
    }

    // Try debugging SQL query
    $query = "SELECT * FROM teacher WHERE Tr_name = '$Tr_name' AND Tr_id = '$Tr_id'";
    file_put_contents("debug_log.txt", "Query: $query\n", FILE_APPEND);

    // Execute query
    $stmt = $conn->prepare("SELECT * FROM teacher WHERE Tr_name = ? AND Tr_id = ?");
    $stmt->bind_param("ss", $Tr_name, $Tr_id);
    $stmt->execute();
    $result = $stmt->get_result();

    if ($result->num_rows > 0) {
        echo "Login successful";
    } else {
        echo "Invalid Tr_name or Tr_id";
        file_put_contents("debug_log.txt", "Login Failed for: Tr_name=$Tr_name, Tr_id=$Tr_id\n", FILE_APPEND);
    }

    $stmt->close();
}

$conn->close();
?>
