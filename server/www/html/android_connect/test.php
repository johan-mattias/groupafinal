<?php
// Include the login information stored in the db_config.php file.
require '../../includes/db_config.php';

// Create connecton using required DEFINEs
$conn = new mysqli(DB_SERVER, DB_USER, DB_PASSWORD, DB_DATABASE);

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
