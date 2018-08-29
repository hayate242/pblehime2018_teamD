<?php
    if( ($query=$_SERVER['QUERY_STRING'])=='' ) exit;
    list($user_name, $data)=explode(',', $query, 2);
    $data =explode(',', $data);
    $user = "root";
    $pass = "";
    echo $user_name;
    echo"<br>";
    print_r($data);

    try {
        $dbh = new PDO('mysql:host=localhost;dbname=TeamD', $user, $pass);
        $sql = "INSERT INTO members_locations (user_name, time, latitude, longitude, altitude, accuracy, velocity) VALUES (
            :user_name, :time, :latitude, :longitude, :altitude, :accuracy, :velocity)";
     
        // 挿入する値は空のまま、SQL実行の準備をする
        $stmt = $dbh->prepare($sql);
        // 挿入する値を配列に格納する
        $params = array(
            ':user_name' => $user_name,
            ':time' => $data[0],
            ':latitude' => $data[1],
            ':longitude' => $data[2],
            ':altitude' => $data[3],
            ':accuracy' => $data[4],
            ':velocity' => $data[5]
        );
        // 挿入する値が入った変数をexecuteにセットしてSQLを実行
        $stmt->execute($params);
        
        // 登録完了のメッセージ
        echo '登録完了しました';
    
        $dbh = null;
    } catch (PDOException $e) {
        print "エラー!: " . $e->getMessage() . "<br/>";
        die();
    }

