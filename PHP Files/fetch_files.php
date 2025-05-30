<?php
header("Access-Control-Allow-Origin: *");
header("Content-Type: application/json");

// Database connection
$conn = new mysqli("localhost", "root", "6165", "rfid");

if ($conn->connect_error) {
    die(json_encode(["error" => "Database connection failed"]));
}

$response = ["success" => false];

if (isset($_GET["teacher_id"])) {
    $teacher_id = $_GET["teacher_id"];
    $sql = "SELECT c.id, c.file_name, c.file_url, c.Tr_Id, t.Tr_name 
            FROM classwork c 
            JOIN teacher t ON c.Tr_Id = t.Tr_id
            WHERE c.Tr_Id = ?
            ORDER BY c.uploaded_at DESC";

    $stmt = $conn->prepare($sql);
    $stmt->bind_param("i", $teacher_id);
} elseif (isset($_GET["cuin"])) {
    $cuin = $_GET["cuin"];
    $sql = "SELECT c.id, c.file_name, c.file_url, t.Tr_name 
            FROM classwork c
            JOIN teacher_subject ts ON c.Tr_Id = ts.Tr_id
            JOIN student_subject ss ON ts.Subject_code = ss.Subject_code
            JOIN teacher t ON c.Tr_Id = t.Tr_id
            WHERE ss.CUIN = ?
            ORDER BY c.uploaded_at DESC";

    $stmt = $conn->prepare($sql);
    $stmt->bind_param("i", $cuin);
} else {
    $stmt = null;
}

if ($stmt && $stmt->execute()) {
    $result = $stmt->get_result();
    $files = [];
    while ($row = $result->fetch_assoc()) {
        $files[] = [
            "id" => $row["id"],
            "name" => $row["file_name"],
            "url" => $row["file_url"],
            "teacher_name" => $row["Tr_name"]
        ];
    }
    $response["success"] = true;
    $response["files"] = $files;
}

echo json_encode($response);
$conn->close();
?>
