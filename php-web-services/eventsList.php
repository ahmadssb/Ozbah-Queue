<?php

require("config.inc.php");

$query ="SELECT * FROM `ozbah_events`";

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
    $response["message"] = "Events Available";
    $response["events"] = array();
    
    foreach ($rows as $row){
        $event = array();
        $event["event_id"] = $row["event_id"];
        $event["event_name"] = $row["event_name"];
        $event["event_password"] = $row["event_password"];
        array_push($response["events"], $event);
    }
    
    echo json_encode($response);
}else{
    $response["success"] = 0;
    $response["message"] = "No event Available";
    die(json_encode($response));
}
?>