<?php
$servername = "178.62.50.61";
$username = "catdex";
$password = "henrik_login";
$database = "catdex";

// Create connection
$conn = new mysqli($servername, $username, $password, $database);

// Check connection
if ($conn->connect_error) {
    die("Connection failed: " . $conn->connect_error);
}

// get the name of one cat
$sql = "SELECT * FROM cats LIMIT 1";
$result = $conn->query("SELECT name FROM cats LIMIT 1")
        or trigger_error($conn->error);

// try to echo the contents
$row=mysqli_fetch_array($result,MYSQLI_ASSOC);
//printf ("%s",$row["name"]);

echo json_encode($row);
//return $row["name"];

?>
