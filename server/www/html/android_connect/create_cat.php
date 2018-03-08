<?php
// This code opens a connection to the database, gets the input value and inserts
// a new cat record into the database with the input value as the cat name.

// Include the login information stored in the db_config.php file.
require '../../includes/db_config.php';

// Create connecton using required DEFINEs
$conn = new mysqli(DB_SERVER, DB_USER, DB_PASSWORD, DB_DATABASE);

// Check connection (from w3schools website)
if ($conn->connect_error) {
    die("Connection failed: " . $conn->connect_error);
}

// Get the JSON struct and decode it.
$data = json_decode(file_get_contents('php://input'), true);

$unsafe_image = $data['image'];

// Sanitize input before quering the database.
$data['name'] = mysqli_real_escape_string($conn, $data['name']);
$data['image'] = mysqli_real_escape_string($conn, $data['image']);

// Insert the provided name of the cat into the database.
$sql = "INSERT INTO cats (name, image) VALUES ('".$data['name']."', '".$data['image']."')";
$sqlHC = "INSERT INTO cats (name, image) VALUES ('Hej', 'Då')";
$sqlUnsafe = $sql = "INSERT INTO cats (name, image) VALUES ('".$data['name']."', '".$unsafe_image."')";

if (!empty($data)) {
mysqli_query($conn, $sql) or die(mysqli_error($conn));
} else {
  echo "Empty!";
}
$conn->close();
?>
