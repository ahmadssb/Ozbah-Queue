<?php

require("config.inc.php");
/***
*	Not Developed Yet
*/
$query ="
SET @delete = 2;

UPDATE ozbah_users
SET user_status = 
    CASE
        WHEN user_priority = @delete AND user_status LIKE 'W' 
            THEN 'D'
    END
, user_priority = 
    CASE
        WHEN user_status LIKE 'D' AND user_priority = @delete
            THEN -1
        WHEN user_priority > @delete AND user_status LIKE 'W'
            THEN user_priority - 1
    END
WHERE user_id = 6 AND user_status LIKE 'W' AND user_priority > @delete
or (user_id = 6 AND user_priority = @delete AND user_status LIKE 'W')

";

try {
    $stmt = $db->prepare($query);
    $result = @$stmt->execute($query_params);
} catch (PDOException $ex) {
    $response["success"] = 0;
    $response["message"] = "Database Error";
}

$rows = $stmt->fetchAll();

if ($rows){
    $response["success"] = 1;
    $response["message"] = "Users Available";
    $response["active_users"] = array();
	$response["waiting_users"] = array();
    
    foreach ($rows as $row){
		if($row["user_status"] == "A"){
		$active_users = array();
        $active_users["user_id"] = $row["user_id"];
        $active_users["user_name"] = $row["user_name"];
        $active_users["user_status"] = $row["user_status"];
        $active_users["user_event_id"] = $row["user_event_id"];
        $active_users["user_modifiedon"] = $row["user_modifiedon"];
        array_push($response["active_users"], $active_users);
		}else{
		$waiting_users = array();			
        $waiting_users["user_id"] = $row["user_id"];
        $waiting_users["user_name"] = $row["user_name"];
        $waiting_users["user_status"] = $row["user_status"];
        $waiting_users["user_event_id"] = $row["user_event_id"];
        $waiting_users["user_modifiedon"] = $row["user_modifiedon"];
        array_push($response["waiting_users"], $waiting_users);
		}
    }
    
    echo json_encode($response);
}else{
    $response["success"] = 0;
    $response["message"] = "No user Available";
    die(json_encode($response));
}
?>