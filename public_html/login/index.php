<?php
if(isset($_GET['team_name']) && isset($_GET['password'])){
    $team_name = $_GET['team_name'];
    $password = $_GET['password'];
    $user_name = $_GET['user_name'];
    // echo $team_name;
}
$user = "root";
$pass = "";
$flag = false;
$regFlag = false;

$userData = array();

try {
    $dbh = new PDO('mysql:host=localhost;dbname=TeamD', $user, $pass);
    foreach($dbh->query('SELECT team_name from team_names where team_name="'.$team_name.'" AND password="'.$password.'"') as $row) {
        if($row){
            echo "ok";
            $flag = true;
        }
    }
    // member登録
    if( $flag ){
        foreach($dbh->query('SELECT team_name from team_members where team_name="'.$team_name.'" AND user_name="'.$user_name.'"') as $row) {
            if($row){
                echo "exist";
                $regFlag = true;
            }
        }
        if( $regFlag == false ){
            $sql = "INSERT INTO team_members (team_name, user_name) VALUES (:team_name, :user_name)";
        
            // 挿入する値は空のまま、SQL実行の準備をする
            $stmt = $dbh->prepare($sql);
            // 挿入する値を配列に格納する
            $params = array(':team_name' => $team_name, ':user_name' => $user_name);
            // 挿入する値が入った変数をexecuteにセットしてSQLを実行
            $stmt->execute($params);
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