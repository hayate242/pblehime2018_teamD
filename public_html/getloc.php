<?php  // 現在地情報を得る
header('Content-type: text/plain; charset=utf-8');
$ymd=date("Ymd");                    // 今日の日付
//$ymd='20140725';
$dir = "/var/www/html/log/$ymd";
if( !file_exists($dir) ) exit;
exec("cd $dir; ls", $ls);
for($i = 0; $i < count($ls); $i++){
 $filename = "$dir/$ls[$i]";
 $tail = rtrim(shell_exec("/usr/bin/tail -1 '$filename'"));
 echo "$ls[$i],$tail\n";
}
?>
