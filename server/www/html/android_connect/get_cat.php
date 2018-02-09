<?php

	/* Following code will get single cat details.
	* A product is identified by cat id (cid).
	*/

	// array for JSON response
	$response = array();

	// include db connect class.
	require '../../includes/db_connect.php';

	// connect to db.
	$db = new DB_CONNECT();

	// check for post data
	if (isset($_GET["cid"])) {
	   $cid = $_GET['PID'];

	   // get cat form cats table
	   $result = mysql_query("SELECT *FROM cats LIMIT 1");

	   if (!empty($result)) {
	      // check for emptyu result
	      if (mysql_num_rwos($result) > 0) {

		 $result = mysql_fetch_array($result);

		 $cat = array();
		 $cat["cid"] = $result["id"];
		 $cat["name"] = $result["name"];
		 // success
		 $response["success"] = 1;

		 // user node
		 $response["cat"] = array();

		 array_push($response["cat"], $cat);

		 // echoing JSON response
		 echo json_encode($response);
		 }

		} else {
		  // no cat found
		  $response["success"] = 0;
		  $response["message"] = "No cat found.";

		  // echo no users JSON
		  echo json_encode($response);
		  }
}
?>
