<?php

require("config.inc.php");

if(!empty($_POST)){

    if(empty($_POST['user_name']) || empty($_POST['user_status'])||empty($_POST['user_event_id']) ){

        $response["success"] = 0;
        $response["message"] = "All Fields Required";
        
        die(json_encode($response));
    }
    $query = "SELECT * FROM ozbah_users 
	WHERE 
	user_event_id = :eventid
	 ";
    
    $query_params = array(
        ':eventid' => $_POST['user_event_id']
    );
    
    try{
        $stmt = $db->prepare($query);
        $result = $stmt->execute($query_params);
    }catch(PDOException $ex){
        $response["success"] = 0;
        $response["message"] = "The name is already in use";
        die(json_encode($response));
    }
	
	$rows = $stmt->fetchAll();

if ($rows){

    $response["success"] = 0;
    $response["message"] = "The name is already in use";
    die(json_encode($response));
    }
    
    $query = "INSERT INTO ozbah_users 
	(user_name, user_status, user_event_id,user_modifiedon) 
	VALUES 
	(:name, :status, :eventid, :modifiedon)";
    
    $query_params = array(
        ':name' => $_POST['user_name'],
        ':status' =>  "W",
        ':eventid' => $_POST['user_event_id'],
		':modifiedon' => "CURRENT_TIMESTAMP"
    );
    
    try {
        $stmt = $db->prepare($query);
        $result = $stmt->execute($query_params);
               
    } catch (PDOException $ex) {
	
        $response["success"] = 0;
        $response["message"] = "The username is already in use, please try again";
        die(json_encode($response));
    }
	
    $response["success"] = 1;
    $response["message"] = "Username Successfully Added";
    echo json_encode($response);
        
}else{
    ?>

<h1>Register User</h1>
<form action="register.php" method="post">
    user_name: <br/>
    <input type="text" name="user_name" placeholder="user_name"/><br/>
    user_status: <br/>
    <input name="user_status" type="user_status" placeholder="user_status"/><br/>
    
	user_event_id: <br/>
    <input type="text" name="user_event_id" placeholder="user_event_id"/><br/>
    <br/>
	<input type="submit" value="Register User"/>
</form>
<?php
}

?>