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

// Sanitize input before quering the database.
$data['name'] = mysqli_real_escape_string($conn, $data['name']);
$data['image'] = mysqli_real_escape_string($conn, $data['image']);
$data['tag'] = mysqli_real_escape_string($conn, $data['tag']);

// Combine the three statemens of insert tag, insert cat and update the catsTagsMap.
// TODO This is probably not safe for multiple parallel connections.
$sqlMulti = "INSERT IGNORE INTO tags (tag) VALUES ('".$data['tag']."'); ";
$sqlMulti.= "INSERT INTO cats (name, image) VALUES ('".$data['name']."', '".$data['image']."'); ";
$sqlMulti.= "INSERT INTO catsTagsMap (cat_id, tag_id) SELECT MAX(cats.id), tags.id FROM cats, tags WHERE tags.tag = '".$data['tag']."' GROUP BY id; ";

// Run all three statements.
if(!$conn->multi_query($sqlMulti)){
  echo "Multi query failed: (" . $conn->errno . ") " .$conn->error;
}
$conn->close();
?>
