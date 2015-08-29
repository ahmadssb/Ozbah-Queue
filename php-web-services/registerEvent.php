<?php

require("config.inc.php");

if(!empty($_POST)){

    if(empty($_POST['event_name']) || empty($_POST['event_password'])){

        $response["success"] = 0;
        $response["message"] = "All Fields Required";
        
        die(json_encode($response));
    }
    $query = "SELECT * FROM ozbah_events 
	WHERE 
	event_name = :name
	 ";
    
    $query_params = array(
        ':name' => $_POST['event_name']
    );
    
    try{
        $stmt = $db->prepare($query);
        $result = $stmt->execute($query_params);
    }catch(PDOException $ex){
        $response["success"] = 2;
        $response["message"] = "The name is already in use";
        die(json_encode($response));
    }
	
	$rows = $stmt->fetchAll();

if ($rows){

    $response["success"] = 3;
    $response["message"] = "The name is already in use";
    die(json_encode($response));
    }
    
    
	
    $query = "INSERT INTO ozbah_events (event_name, event_password) VALUES (:name, :pass)
	";
    
    
    $query_params = array(
        ':name' => $_POST['event_name'],
		':pass' => $_POST['event_password']
    );
    
    try {
        $stmt = $db->prepare($query);
        $result = $stmt->execute($query_params);
               
    } catch (PDOException $ex) {
	
        $response["success"] = 2;
        $response["message"] = "The name is already in use, please try again";
        die(json_encode($response));
    }
	
	 $query = "SELECT * FROM ozbah_events 
	WHERE 
	event_name = :name
	 ";
    
    $query_params = array(
        ':name' => $_POST['event_name']
    );
    
    try{
        $stmt = $db->prepare($query);
        $result = $stmt->execute($query_params);
    }catch(PDOException $ex){
        $response["success"] = 2;
        $response["message"] = "The name is already in use";
        die(json_encode($response));
    }
	
	$rows = $stmt->fetchAll();

if ($rows){

$response["events"] = array();
    
    $response["success"] = 1;
    $response["message"] = "Event Successfully Added";
	
    foreach ($rows as $row){
        $event = array();
        $event["event_id"] = $row["event_id"];
        $event["event_name"] = $row["event_name"];
        $event["event_password"] = $row["event_password"];
        array_push($response["events"], $event);
    }
	
    echo json_encode($response);
    }
    

	
        
}else{
    ?>

<h1>Register Event</h1>
<form action="registerEvent.php" method="post">
    Event Name: <br/>
    <input type="text" name="event_name" placeholder="event_name"/><br/>
    <br/>
    Event Password: <br/>
    <input type="text" name="event_password" placeholder="event_password"/><br/>
    <br/>
	<input type="submit" value="Register User"/>
</form>
<?php
}

?>