<?php
// This code opens a connection to the database, querys the database for one cat
// record, packges it into a JSON object and returns it.

// Include the login information stored in the db_config.php file.
require '../../includes/db_config.php';

// Create connecton using required DEFINEs
$conn = new mysqli(DB_SERVER, DB_USER, DB_PASSWORD, DB_DATABASE);

// Check connection
if ($conn->connect_error) {
    die("Connection failed: " . $conn->connect_error);
}
$sql = "SELECT * FROM cats WHERE name = 'carCat'";
$sqlHighestId = "SELECT * FROM cats ORDER BY id DESC LIMIT 1";

// Read a cat record, create an array, encode it as JSON and echo it.
// Adapted code from a StackOverflow question, which URL we've lost.
$myArray = array();
  if ($result = $conn->query($sqlHighestId)) {
    $tempArray = array();
      while($row = $result->fetch_object()) {
        $tempArray = $row;
        array_push($myArray, $tempArray);
      }
      echo json_encode($myArray);
  }

// Close down.
$result->close();
$conn->close();
?>
