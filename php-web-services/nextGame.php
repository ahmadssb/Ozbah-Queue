<?php

require("config.inc.php");
/***
*	Not Developed Yet
*/
$query ="
UPDATE  `ozbah_users` SET  `user_priority` = (
(
SELECT selected_value
FROM (

SELECT MAX(  `user_priority` ) AS selected_value
FROM  `ozbah_users` 
WHERE user_status LIKE  'W'
) AS sub_selected_value
) +1
)
WHERE user_id =5
AND user_status LIKE  'W'
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