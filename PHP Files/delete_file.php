<?php
header("Access-Control-Allow-Origin: *");
header("Content-Type: application/json");

// Database credentials
$servername = "localhost";
$username = "root";
$password = "6165";
$dbname = "rfid";

// Create connection
$conn = new mysqli($servername, $username, $password, $dbname);
if ($conn->connect_error) {
    die(json_encode(["success" => false, "message" => "Database connection failed: " . $conn->connect_error]));
}

// Check if request method is POST and file_name is set
if ($_SERVER["REQUEST_METHOD"] == "POST" && isset($_POST["file_name"])) {
    $file_name = $_POST["file_name"];
    $file_path = "uploads/" . $file_name;

    // Prepare SQL query to delete the file entry from the database
    $stmt = $conn->prepare("DELETE FROM classwork WHERE file_name = ?");
    if (!$stmt) {
        die(json_encode(["success" => false, "message" => "Query preparation failed: " . $conn->error]));
    }
    
    $stmt->bind_param("s", $file_name);
    $stmt->execute();

    if ($stmt->affected_rows > 0) {
        // File found in the database, now delete from the folder
        if (file_exists($file_path)) {
            if (unlink($file_path)) {
                echo json_encode(["success" => true, "message" => "File deleted successfully"]);
            } else {
                echo json_encode(["success" => false, "message" => "Failed to delete file from folder"]);
            }
        } else {
            echo json_encode(["success" => false, "message" => "File not found in folder, but deleted from database"]);
        }
    } else {
        echo json_encode(["success" => false, "message" => "File not found in database"]);
    }

    $stmt->close();
} else {
    echo json_encode(["success" => false, "message" => "Invalid request"]);
}

$conn->close();
?>
