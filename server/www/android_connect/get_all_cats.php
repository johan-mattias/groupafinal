<?php

  /*
   * Following code will list all the products
   * From guide at: https://www.androidhive.info/2012/05/how-to-connect-android-with-php-mysql/
   */

  // array for JSON response
  $response = array();

  // include db connect class
  require_once __DIR__ . '/db_connect.php';

  // connecting to db
  $db = new DB_CONNECT();

  // get all products from products table
  $result = mysql_query("SELECT *FROM cats") or die(mysql_error());

  // check for empty result
  if (mysql_num_rows($result) > 0) {
      // looping through all results
      // products node
      $response["cats"] = array();

      while ($row = mysql_fetch_array($result)) {
          // temp user array
          $cat = array();
          $cat["cid"] = $row["id"];
          $cat["name"] = $row["name"];

          // push single product into final response array
          array_push($response["cats"], $cat);
      }
      // success
      $response["success"] = 1;

      // echoing JSON response
      echo json_encode($response);
  } else {
      // no products found
      $response["success"] = 0;
      $response["message"] = "No cats found";

      // echo no users JSON
      echo json_encode($response);
  }
?>
