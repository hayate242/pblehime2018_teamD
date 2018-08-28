<?php
  if( ($query=$_SERVER['QUERY_STRING'])=='' ) exit;
  list($id, $data)=explode(',', $query, 2);
  if( $id=='' ) exit;
  $ymd = 'log/'.date("Ymd");
  if( !file_exists($ymd) ) mkdir($ymd);    // ディレクトリの作成
  $fw = fopen("$ymd/$id", 'a');      // 追記する
  fwrite($fw, date("His").','."$data\n");
  fclose($fw);
?>
