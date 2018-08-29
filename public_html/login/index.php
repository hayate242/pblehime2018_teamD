<?php
if(isset($_GET['team_name']) && isset($_GET['password'])){
    $team_name = $_GET['team_name'];
    $password = $_GET['password'];
    // echo $team_name;
}
$user = "root";
$pass = "";
$flag = false;

$userData = array();

try {
    $dbh = new PDO('mysql:host=localhost;dbname=TeamD', $user, $pass);
    foreach($dbh->query('SELECT team_name from team_names where team_name="'.$team_name.'" AND password="'.$password.'"') as $row) {
        if($row){
            echo "ok";
            $flag = true;
        }
    }
    // if($flag == false){
    //     echo "login failed";
    // }
    $dbh = null;
} catch (PDOException $e) {
    print "エラー!: " . $e->getMessage() . "<br/>";
    die();
}

// example
// http://pbl.jp/td/registarMember/?team_name=test_team&user_name=yamaguchi


?> 