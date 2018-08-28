<?php
if(isset($_GET['team_name']) && isset($_GET['user_name'])){
    $team_name = $_GET['team_name'];
    $user_name = $_GET['user_name'];
    // echo $team_name;
}
$user = "root";
$pass = "";

$userData = array();

try {
    $dbh = new PDO('mysql:host=localhost;dbname=TeamD', $user, $pass);
    $sql = "INSERT INTO team_members (team_name, user_name) VALUES (:team_name, :user_name)";
 
    // 挿入する値は空のまま、SQL実行の準備をする
    $stmt = $dbh->prepare($sql);
    // 挿入する値を配列に格納する
    $params = array(':team_name' => $team_name, ':user_name' => $user_name);
    // 挿入する値が入った変数をexecuteにセットしてSQLを実行
    $stmt->execute($params);
    
    // 登録完了のメッセージ
    echo '登録完了しました';

    $dbh = null;
} catch (PDOException $e) {
    print "エラー!: " . $e->getMessage() . "<br/>";
    die();
}

// example
// http://pbl.jp/td/registarMember/?team_name=test_team&user_name=yamaguchi


?> 