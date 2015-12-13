<?php

require("config.inc.php");

if(!empty($_POST)){

    if(empty($_POST['user_event_id']) ){

        $response["success"] = 0;
        $response["message"] = "All Fields Required";
        
        die(json_encode($response));
    }
    $query = "SELECT * FROM ozbah_users 
	WHERE 
	user_event_id = :eventid
	 ";
    
    $query_params = array(
        ':eventid' => $_POST['user_event_id'],
    );
    
    try{
        $stmt = $db->prepare($query);
        $result = $stmt->execute($query_params);
    }catch(PDOException $ex){
        $response["success"] = 2;
        $response["message"] = "OOPS, Something Went Wrong";
        die(json_encode($response));
    }
	
	$rows = $stmt->fetchAll();

if ($rows){
$query = "UPDATE `ozbah_users` 
SET 
`user_name`='',
`user_name_temp`='',
`user_modifiedon`= now()
WHERE 
`user_event_id` = :eventid;";
	
	$query_params = array(
        ':eventid' => $_POST['user_event_id']
    );
    
    try {
        $stmt = $db->prepare($query);
        $result = $stmt->execute($query_params);
               
    } catch (PDOException $ex) {
	
        $response["success"] = 0;
        $response["message"] = "OOPS, Something Went Wrong";
        die(json_encode($response));
    }
	
    $response["success"] = 1;
    $response["message"] = "Users Successfully Removed";
    echo json_encode($response);
}}else{
    ?>

    <h1>Register User</h1>
    <form action="deleteEventUsers.php" method="post">
        user_event_id:
        <br/>
        <input type="text" name="user_event_id" placeholder="user_event_id" />
        <br/>
        <br/>
        <input type="submit" value="Delete Event User" />
    </form>
    <?php
}

?>