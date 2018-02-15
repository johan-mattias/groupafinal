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

// Get the JSON struct and decode it.
$data = json_decode(file_get_contents('php://input'), true);
echo $data['name'];
echo $data['tag'];

// Get the corresponding id of the provided cat. Get the corresponding id of the
// provided tag. Add the cat and tag id pair to the catsTagsMap table.
if ($resultCatID = $conn->query("SELECT cats.id FROM `cats` WHERE cats.name = '.$data['name'].'")) {
  if ($resultTagID = $conn->query("SELECT tags.id FROM `tags`WHERE tags.tag = '.$data['tag'].'")) {
    $conn->query("INSERT INTO catsTagsMap (cat_id, tag_id) VALUES ('.$resultCatID.', '.$resultTagID.')");
  }
}

// Close down.
$result->close();
$conn->close();
?>
mysqli_query($conn, "INSERT INTO tags (tag) VALUES ('".$data['tag']."')") or die(mysqli_error($conn));
