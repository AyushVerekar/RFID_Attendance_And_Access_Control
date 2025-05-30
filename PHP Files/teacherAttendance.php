<?php
header("Content-Type: application/json");
header("Access-Control-Allow-Origin: *");
header("Access-Control-Allow-Methods: GET");

// Database Connection
$servername = "localhost";  
$username = "root";        
$password = "6165";            
$database = "rfid";        

$conn = new mysqli($servername, $username, $password, $database);
if ($conn->connect_error) {
    die(json_encode(["error" => "Database connection failed"]));
}

// Check if Teacher ID is provided
if (!isset($_GET['Tr_id'])) {
    echo json_encode(["error" => "Teacher ID is required"]);
    exit();
}

$teacher_id = $_GET['Tr_id'];

// Fetch teacher's assigned subjects
$sql_subjects = "SELECT Subject_code FROM teacher_subject WHERE Tr_id = ?";
$stmt = $conn->prepare($sql_subjects);
$stmt->bind_param("s", $teacher_id);
$stmt->execute();
$result_subjects = $stmt->get_result();

$subjects = [];
while ($row = $result_subjects->fetch_assoc()) {
    $subjects[] = $row['Subject_code'];
}

if (empty($subjects)) {
    echo json_encode(["message" => "No subjects assigned to this teacher."]);
    exit();
}

// Fetch student attendance for these subjects
$subject_placeholders = implode(',', array_fill(0, count($subjects), '?'));
$sql_attendance = "
    SELECT s.Student_name, sa.Subject_code, COUNT(sa.Student_cardno) AS attended_lectures, 
    (SELECT COUNT(*) FROM active_sessions WHERE Subject_code = sa.Subject_code) AS total_lectures
    FROM student_attendance sa
    JOIN student s ON sa.Student_cardno = s.Student_cardno
    WHERE sa.Subject_code IN ($subject_placeholders)
    GROUP BY sa.Student_cardno, sa.Subject_code
";

$stmt_attendance = $conn->prepare($sql_attendance);
$stmt_attendance->bind_param(str_repeat("s", count($subjects)), ...$subjects);
$stmt_attendance->execute();
$result_attendance = $stmt_attendance->get_result();

$attendance_data = [];
while ($row = $result_attendance->fetch_assoc()) {
    $attendance_percentage = ($row['total_lectures'] > 0) ? 
        ($row['attended_lectures'] / $row['total_lectures']) * 100 : 0;
    
    $attendance_data[] = [
        "Student_name" => $row["Student_name"],
        "Subject_code" => $row["Subject_code"],
        "Attended_lectures" => $row["attended_lectures"],
        "Total_lectures" => $row["total_lectures"],
        "Attendance_percentage" => round($attendance_percentage, 2)
    ];
}

echo json_encode($attendance_data);
?>