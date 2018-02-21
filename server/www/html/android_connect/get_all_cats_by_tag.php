<?php
// This opens a connection to the database, acquires the inputed value and
// querys the database for all cats with a tag that matches the inputed value.
// This is then encoded as JSON and returned.

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
echo $data['tag'];

$sql = "SELECT cats.name, tags.tag FROM cats
        JOIN catsTagsMap ON cats.id = catsTagsMap.cat_id
        JOIN tags ON tags.id = catsTagsMap.tag_id
        WHERE tags.tag = '".$data['tag']."';";

// Adapted code from a StackOverflow question, which URL we've lost.
$myArray = array();
  if ($result = $conn->query($sql)) {
    $tempArray = array();
      while($row = $result->fetch_object()) {
        $tempArray = $row;
        array_push($myArray, $tempArray);
      }
      echo json_encode($myArray);
  }

?>
