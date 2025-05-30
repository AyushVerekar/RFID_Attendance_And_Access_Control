<?php
error_reporting(E_ALL);
ini_set('display_errors', 1);

// Database connection
$host = "localhost";
$user = "root"; 
$password = "6165"; 
$database = "rfid"; 

$conn = new mysqli($host, $user, $password, $database);

// Check connection
if ($conn->connect_error) {
    die("Database connection failed: " . $conn->connect_error);
}

// Check if request is POST
if ($_SERVER["REQUEST_METHOD"] == "POST") {
    $student_name = isset($_POST['student_name']) ? trim($_POST['student_name']) : '';
    $CUIN = isset($_POST['CUIN']) ? intval($_POST['CUIN']) : 0;

    // Debugging log
    file_put_contents("debug_log.txt", "Received: student_name=$student_name, CUIN=$CUIN\n", FILE_APPEND);

    if (empty($student_name) || $CUIN == 0) {
        echo "Invalid input";
        exit();
    }

    // Validate student
    $stmt = $conn->prepare("SELECT * FROM student WHERE Student_name = ? AND CUIN = ?");
    if (!$stmt) {
        die("Prepare failed: " . $conn->error);
    }
    
    $stmt->bind_param("si", $student_name, $CUIN); // CUIN is an INT, so use "si"
    $stmt->execute();
    $result = $stmt->get_result();

    if ($result->num_rows > 0) {
        echo "Login successful";
    } else {
        echo "Invalid credentials";
    }

    $stmt->close();
}

$conn->close();
?>
