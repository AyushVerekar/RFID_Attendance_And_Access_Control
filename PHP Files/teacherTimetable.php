<?php
header('Content-Type: application/json');

// Database connection
$host = "localhost";  
$user = "root";       
$password = "6165";       
$database = "rfid";  

$conn = new mysqli($host, $user, $password, $database);

if ($conn->connect_error) {
    die(json_encode(["error" => "Database connection failed"]));
}

// Ensure Tr_id is provided
if (!isset($_GET['Tr_id'])) {
    die(json_encode(["error" => "Tr_id not provided"]));
}

$teacher_id = $_GET['Tr_id'];
$days = ["Monday", "Tuesday", "Wednesday", "Thursday", "Friday", "Saturday"];
$resultArray = [];

foreach ($days as $index => $day) {
    $dayIndex = $index + 1; // Assuming your database uses 1-6 for Monday-Saturday
    $dayData = ["day" => $day];

    for ($slot = 1; $slot <= 5; $slot++) {
        if ($slot == 3) {
            $dayData["slot_$slot"] = "Break";
            continue;
        }

        // Use a prepared statement to prevent SQL injection
        $query = "SELECT subject_code FROM has 
                  WHERE day_id = ? AND slot_id = ? 
                  AND subject_code IN (SELECT subject_code FROM teacher_subject WHERE Tr_id = ?)";
        
        $stmt = $conn->prepare($query);
        $stmt->bind_param("iis", $dayIndex, $slot, $teacher_id);
        $stmt->execute();
        $result = $stmt->get_result();

        $dayData["slot_$slot"] = ($result->num_rows > 0) ? $result->fetch_assoc()["subject_code"] : "--";

        $stmt->close();
    }

    $resultArray[] = $dayData;
}

echo json_encode($resultArray);
$conn->close();
?>
