<?php
header("Access-Control-Allow-Origin: *");
header("Access-Control-Allow-Methods: GET");
header("Content-Type: application/json");

$conn = new mysqli("localhost", "root", "6165", "rfid");

if ($conn->connect_error) {
    die(json_encode(["error" => "Database connection failed"]));
}

if (isset($_GET['parent_file_id'])) {
    $parent_file_id = $_GET['parent_file_id'];
    
    // Use prepared statements to prevent SQL injection
    $stmt = $conn->prepare("SELECT * FROM classwork_files WHERE parent_file_id = ? ORDER BY upload_date DESC");
    $stmt->bind_param("i", $parent_file_id);
    $stmt->execute();
    $result = $stmt->get_result();
    
    $attachments = [];
    while ($row = $result->fetch_assoc()) {
        $row['file_path'] = "http://localhost/classwork/" . $row['file_path']; // Full URL path
        $attachments[] = $row;
    }
    
    echo json_encode($attachments);
    
    $stmt->close();
} else {
    echo json_encode(["error" => "Missing parent_file_id"]);
}

$conn->close();
?>
