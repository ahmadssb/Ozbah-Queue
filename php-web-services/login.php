<?php

require('config.inc.php');


if(!empty($_POST)){
    
    if(empty($_POST['username'])||empty($_POST['password'])){
        $response["success"] = 0;
        $response["message"] = "All Fields Required";
        die(json_encode($response));
    }
    
    $query = "
            SELECT * FROM `oneksa_users` 
            WHERE
            user_username = :user
			AND
			user_role = 'U'
             ";
    $query_params = array(
        ':user' => $_POST['username']
    );
	
    try {
        $stmt = $db->prepare($query);
        $result = $stmt->execute($query_params);
    } catch (PDOException $ex) {
        $response['success'] = 0 ;
        $response['message'] = "Database Error1, Please try Again";
        die(json_encode($response));
    }
	
    $login_ok = false;
	$user_id = -1;
    // encrypt password using PasswordHash
	$hash = new PasswordHash(8 , false);
   
    $row = $stmt->fetch();
    if ($row){
        //$encr_user_pass = $hash->HashPassword($_POST['password']);
        $check = $hash->CheckPassword($_POST['password'] , $row['user_password']); 
		
        if($check == 1){
            $login_ok = true;
			$user_id  =  $row['user_id'];
        }else{
			$login_ok = false;
		}
		
    }
    

    if($login_ok){
        $response["success"] = 1;
        $response["message"] = "Login Successful";
        $response["userid"] = $user_id;
        $response["user_username"] = $row["user_username"];
        $response["user_diplayname"] = $row["user_diplayname"];
        $response["user_photo"] = $row["user_photo"];
        die(json_encode($response));
    }else{
        $response["success"] = 0;
        $response["message"] = "username or password Incorrect";
        die(json_encode($response));
    }
    
}else{
?>
<h1>Login</h1>
<form action="login.php" method="post">
    Username: <br/>
    <input type="text" name="username" placeholder="Username"/><br/>
    Password:<br/>
    <input type="password" name="password" placeholder="Password"/><br/>
    <input type="submit" value="Login"/>
    <a href="register.php">Register</a>
</form>
<?php
}
?>

