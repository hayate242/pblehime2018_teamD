<?php
if(isset($_GET['team_name'])){
    $team_name = $_GET['team_name'];
    // echo $team_name;
}
$user = "root";
$pass = "";

$userData = "";


try {
    $dbh = new PDO('mysql:host=localhost;dbname=TeamD', $user, $pass);
    $i = 0;
    // 'select * from members_locations where user_name IN (select user_name from team_members where team_name = "test_team");'
    foreach($dbh->query('select * from members_locations where user_name IN (select user_name from team_members where team_name = "'.$team_name.'")') as $row) {
        // $userData += $row;
        $userData += (string)$row["user_name"],",";
        $userData += (string)$row["time"],",";
        // print_r($row);
    }
    var_dump($userData);
    // print_r($userData);  
    //jsonとして出力
    // header('Content-type: application/json');
    // echo json_encode($userData);
    $dbh = null;
} catch (PDOException $e) {
    print "エラー!: " . $e->getMessage() . "<br/>";
    die();
}
?>