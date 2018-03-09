<?php
// This code opens a connection to the database, gets all cat records and their
// corresponding tags, encodes it into JSON and returns it.

// Include the login information stored in the db_config.php file.
require '../../includes/db_config.php';

// Create connecton using required DEFINEs
$conn = new mysqli(DB_SERVER, DB_USER, DB_PASSWORD, DB_DATABASE);

// Check connection
if ($conn->connect_error) {
    die("Connection failed: " . $conn->connect_error);
}

$sql = "SELECT cats.name, cats.image, tags.tag FROM cats JOIN catsTagsMap ON cats.id = catsTagsMap.cat_id JOIN tags ON tags.id = catsTagsMap.tag_id";

// Query the DB for all cat records and their corresponding tags, encode and return.
$myArray = array();
  if ($result = $conn->query($sql)) {
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
