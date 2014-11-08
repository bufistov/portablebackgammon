<?php
header("Content-type: text/plain");
require("config.inc.php");

//retrieve data
$tunename = trim($_GET["tunename"]);
$artist = trim($_GET["artist"]);
$tune = trim($_GET["tune"]);


//connect to the DB
$db = connect();

//

///// LOG WHAT IS BEING CALLED
$logtxt="upload?artist=$artist&tune=$tune";
$type="-->";
logthis($logtxt,$db,$type);



//CHECK FIRST IF THEY ARE A REGISTERED USER!

//connect to the DB
$db = connect();

	//set the query
	$query = "INSERT INTO tunes(tune, artist,tunename, rating) VALUES('$tune', '$artist','$tunename',0)";
	//execute it
	$result = mysql_query($query, $db);
	if($result)
	{
		$reason = "tune uploaded!";
	$type="<--";
	logthis($reason,$db,$type);
	
	

		
		echo $reason;
		
	}
	else
	{
		$message  = 'Invalid query: ' . mysql_error();// . "\n";
		
	$type="<--";
	logthis($message,$db,$type);
		//$message .= 'Whole query: ' . $query;
		echo "Tune not uploaded :( " . $message;
		fwrite($fh, "<font color=red>" . $message . "</font><br>");
	}

mysql_close($db);

//fclose($fh);


?>