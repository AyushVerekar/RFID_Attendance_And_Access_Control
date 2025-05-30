<?php
header('Content-Type: application/json'); // Set header to return JSON

// Database connection
$host = "localhost";  
$user = "root";       
$password = "6165";       
$database = "rfid";  

$conn = new mysqli($host, $user, $password, $database);

if ($conn->connect_error) {
    die(json_encode(["error" => "Database connection failed"]));
}

if (!isset($_GET['CUIN'])) {
    die(json_encode(["error" => "CUIN not provided"]));
}

$cuin = $_GET['CUIN'];
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

        $query = "SELECT subject_code FROM has 
                  WHERE day_id = $dayIndex AND slot_id = $slot 
                  AND subject_code IN (SELECT subject_code FROM student_subject WHERE CUIN = '$cuin')";

        $result = $conn->query($query);

        $dayData["slot_$slot"] = ($result->num_rows > 0) ? $result->fetch_assoc()["subject_code"] : "--";
    }

    $resultArray[] = $dayData;
}

echo json_encode($resultArray);
$conn->close();
?>
