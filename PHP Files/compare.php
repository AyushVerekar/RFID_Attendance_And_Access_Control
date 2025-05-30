<?php
// Database connection
$servername = "localhost";
$username = "root";
$password = "6165";
$dbname = "rfid";

$conn = new mysqli($servername, $username, $password, $dbname);

// Check connection
if ($conn->connect_error) {
    die("Connection failed: " . $conn->connect_error);
}

if (isset($_GET['rfid'])) {
    $rfid = strtoupper($_GET['rfid']); // Convert to uppercase for consistency

    // Check if the RFID is a teacher's card
    $sql_teacher = "SELECT * FROM teacher WHERE Tr_cardno = '$rfid'";
    $result_teacher = $conn->query($sql_teacher);

    if ($result_teacher->num_rows > 0) {
        // Check if there's an active session for this teacher
        $check_session = "SELECT * FROM active_sessions WHERE Tr_cardno = '$rfid' AND status = 1";
        $result_session = $conn->query($check_session);

        if ($result_session->num_rows > 0) {
            // If session exists, stop student scanning
            $stop_session = "UPDATE active_sessions SET status = 0 WHERE Tr_cardno = '$rfid' AND status = 1";
            $conn->query($stop_session);
            echo "Tr_cardno scanned again. Student scanning is now disabled. WHITE_LED_OFF";
        } else {
            // Get the subject code for the teacher
            $sql_subject = "SELECT subject_code FROM teacher_subject WHERE Tr_id = (SELECT Tr_id FROM teacher WHERE Tr_cardno = '$rfid')";
            $result_subject = $conn->query($sql_subject);

            if ($result_subject->num_rows > 0) {
                $subject_code = $result_subject->fetch_assoc()['subject_code'];
                
                // Start a new session with subject code
                $start_session = "INSERT INTO active_sessions (Tr_cardno, status, subject_code) VALUES ('$rfid', 1, '$subject_code')";
                $conn->query($start_session);
                echo "Tr_cardno scanned. Students can now scan their cards. WHITE_LED_ON";
            } else {
                echo "No subject found for this teacher.";
            }
        }
    } 
    // If teacher scanning is active, allow student scanning
    else {
        // Check if any teacher session is active
        $check_active_session = "SELECT * FROM active_sessions WHERE status = 1";
        $result_active_session = $conn->query($check_active_session);

        if ($result_active_session->num_rows > 0) {
            // Get the active session's subject code
            $active_session = $result_active_session->fetch_assoc();
            $subject_code = $active_session['subject_code'];

            // Check if it's a valid student card
            $sql_student = "SELECT * FROM student WHERE Student_cardno = '$rfid'";
            $result_student = $conn->query($sql_student);

            if ($result_student->num_rows > 0) {
                // Insert student attendance with subject code
                $insert_sql = "INSERT INTO student_attendance (Student_cardno, entrytime, Subject_code) VALUES ('$rfid', NOW(), '$subject_code')";
                if ($conn->query($insert_sql) === TRUE) {
                    echo "Student card scanned successfully! Attendance recorded.";
                } else {
                    echo "Error: " . $insert_sql . "<br>" . $conn->error;
                }
            } else {
                echo "Invalid student card.";
            }
        } else {
            echo "Please scan a teacher card first.";
        }
    }
} else {
    echo "No RFID data received.";
}

$conn->close();
?>