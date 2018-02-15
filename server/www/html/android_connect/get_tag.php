<?php
// This code opens a connection to the database, querys it for one tag record,
// encodes it as JSON and returns it.

// Include the login information stored in the db_config.php file.
require '../../includes/db_config.php';

// Create connecton using required DEFINEs
$conn = new mysqli(DB_SERVER, DB_USER, DB_PASSWORD, DB_DATABASE);

// Check connection
if ($conn->connect_error) {
    die("Connection failed: " . $conn->connect_error);
}

// Read a tag record, create an array, encode it as JSON and echo it.
// Adapted code from a StackOverflow question, which URL we've lost.
$myArray = array();
  if ($result = $conn->query("SELECT * FROM tag LIMIT 1")) {
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
