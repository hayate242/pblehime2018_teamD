<?php
if(isset($_GET['team_name'])){
    $team_name = $_GET['team_name'];
    // echo $team_name;
}
$user = "root";
$pass = "";

$userData = array();

try {
    $dbh = new PDO('mysql:host=localhost;dbname=TeamD', $user, $pass);
    $i = 0;
    foreach($dbh->query('SELECT user_name from team_members where team_name="'.$team_name.'"') as $row) {
        $userData += array("user".(string)$i=>$row["user_name"]);
        $i++;
        // print_r($row);
    }
    // print_r($userData);  
    //jsonとして出力
    header('Content-type: application/json');
    echo json_encode($userData);
    $dbh = null;
} catch (PDOException $e) {
    print "エラー!: " . $e->getMessage() . "<br/>";
    die();
}
?>