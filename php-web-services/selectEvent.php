<?php

require("config.inc.php");

if(!empty($_POST)){

    if(empty($_POST['event_id']) || empty($_POST['event_password'])){

        $response["success"] = 0;
        $response["message"] = "All Fields Required";
        
        die(json_encode($response));
    }
    $query = "SELECT * FROM ozbah_events 
	WHERE 
	event_id = :id
	AND 
	event_password = :pass
	 ";
    
    $query_params = array(
        ':id' => $_POST['event_id'],
        ':pass' => $_POST['event_password']
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

    $response["success"] = 1;
    $response["message"] = "Correct Password";
    die(json_encode($response));
    }else{
		
        $response["success"] = 2;
        $response["message"] = "Wrong password";
        die(json_encode($response));
	}
    
    
	
}else{
    ?>

<h1>Check Event Password</h1>
<form action="selectEvent.php" method="post">
    Event Name: <br/>
    <input type="text" name="event_id" placeholder="event_id"/><br/>
    <br/>
    Event Password: <br/>
    <input type="text" name="event_password" placeholder="event_password"/><br/>
    <br/>
	<input type="submit" value="Check Password"/>
</form>
<?php
}

?>