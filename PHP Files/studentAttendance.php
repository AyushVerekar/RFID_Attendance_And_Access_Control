<?php
header("Content-Type: application/json");

$servername = "localhost";
$username = "root";
$password = "6165";
$dbname = "rfid";

// Create connection
$conn = new mysqli($servername, $username, $password, $dbname);

// Check connection
if ($conn->connect_error) {
    die(json_encode(["status" => "error", "message" => "Database connection failed: " . $conn->connect_error]));
}

// Check if CUIN is provided in the request
if (!isset($_GET['CUIN']) || empty($_GET['CUIN'])) {
    echo json_encode(["status" => "error", "message" => "Invalid request"]);
    exit;
}

$CUIN = $_GET['CUIN'];

// Get Student Card Number
$sql_cardno = "SELECT Student_cardno FROM student WHERE CUIN = ?";
$stmt = $conn->prepare($sql_cardno);
$stmt->bind_param("s", $CUIN);
$stmt->execute();
$result_cardno = $stmt->get_result();

if ($result_cardno->num_rows === 0) {
    echo json_encode(["status" => "error", "message" => "Student not found"]);
    exit;
}

$row_cardno = $result_cardno->fetch_assoc();
$student_cardno = $row_cardno['Student_cardno'];

// Get Attendance Data (only for subjects the student is enrolled in)
$sql_attendance = "
    SELECT ss.Subject_code, 
           COUNT(sa.entrytime) AS attended_lectures,
           (SELECT COUNT(*) FROM active_sessions WHERE Subject_code = ss.Subject_code) AS total_lectures
    FROM student_subject ss
    LEFT JOIN student_attendance sa 
        ON ss.Subject_code = sa.Subject_code 
        AND sa.Student_cardno = ?
    WHERE ss.CUIN = ?
    GROUP BY ss.Subject_code";

$stmt = $conn->prepare($sql_attendance);
$stmt->bind_param("ss", $student_cardno, $CUIN);
$stmt->execute();
$result_attendance = $stmt->get_result();

$attendance_data = [];
while ($row = $result_attendance->fetch_assoc()) {
    $subject_code = $row['Subject_code'];
    $attended_lectures = (int) $row['attended_lectures'];
    $total_lectures = (int) $row['total_lectures'];

    // Calculate Attendance Percentage
    $attendance_percentage = ($total_lectures > 0) ? round(($attended_lectures / $total_lectures) * 100, 2) : 0;

    $attendance_data[] = [
        "subject_code" => $subject_code,
        "attended_lectures" => $attended_lectures,
        "total_lectures" => $total_lectures,
        "attendance_percentage" => $attendance_percentage
    ];
}

// Return JSON Response
echo json_encode([
    "status" => "success",
    "CUIN" => $CUIN,
    "attendance" => $attendance_data
]);

// Close connection
$conn->close();
?>
