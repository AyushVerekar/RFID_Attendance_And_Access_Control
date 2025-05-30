<?php
$upload_dir = "uploads/"; // Folder to store files

// Check if a file was uploaded
if ($_FILES['file']['name']) {
    $file_name = basename($_FILES['file']['name']);
    $file_path = $upload_dir . $file_name;
    
    // Get teacher ID from the request
    if (isset($_POST['teacher_id'])) {
        $Tr_Id = $_POST['teacher_id'];
    } else {
        echo json_encode(["error" => "Teacher ID is required"]);
        exit;
    }

    // Move file to the upload directory
    if (move_uploaded_file($_FILES['file']['tmp_name'], $file_path)) {
        // Database connection
        $conn = new mysqli("localhost", "root", "6165", "rfid");

        if ($conn->connect_error) {
            die(json_encode(["error" => "Database connection failed"]));
        }

        // Insert file details into the database
        $stmt = $conn->prepare("INSERT INTO classwork (file_name, file_url, Tr_Id) VALUES (?, ?, ?)");
        $stmt->bind_param("ssi", $file_name, $file_path, $Tr_Id);

        if ($stmt->execute()) {
            echo json_encode(["success" => "File uploaded successfully"]);
        } else {
            echo json_encode(["error" => "Database insertion failed"]);
        }

        $stmt->close();
        $conn->close();
    } else {
        echo json_encode(["error" => "File upload failed"]);
    }
} else {
    echo json_encode(["error" => "No file received"]);
}
?>