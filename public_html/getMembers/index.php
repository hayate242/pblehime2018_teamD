<?php
if(isset($_GET['team_name'])){
    $team_name = $_GET['team_name'];
    echo $team_name;
}
$user = "root";
$pass = "";
try {
    $dbh = new PDO('mysql:host=localhost;dbname=TeamD', $user, $pass);
    foreach($dbh->query('SELECT user_name from team_members where team_name="'.$team_name.'"') as $row) {
        echo json_encode($row);
        // var_dump($row);
    }
    $dbh = null;
} catch (PDOException $e) {
    print "エラー!: " . $e->getMessage() . "<br/>";
    die();
}
?> 