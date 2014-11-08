<?php


//DB parameters

$dbHost = "72.167.233.23";
$dbUser = "feroxneutrino";
$dbPassword = "xxxxx";
$dbName = "feroxneutrino";

//connect to the db
function connect()
{
	$db = mysql_connect($GLOBALS["dbHost"], $GLOBALS["dbUser"], $GLOBALS["dbPassword"]);
	if($db == false)
		die("Error while connecting to the DB...");
	mysql_select_db($GLOBALS["dbName"], $db)
		or die("Error while connecting to the DB.");
	return $db;
}

/// LOCAL LOGGING FUNCTION
// logme text to log
// db db object to use
// type either --> or <-- to show a call or response
function logthis($logme,$db,$type)
{
	$ip=$_SERVER['REMOTE_ADDR']; 
	$date = date('d-m-Y, H:i',(time()-date('Z')));
	$stringData = $ip . " @ " . $date . " " . $type . " " . $logme ;
	$query = "INSERT INTO log (message) values ('$stringData')"; 
	//execute it
	$result = mysql_query($query, $db);
	
}

function test()
{
	echo "HELLO";
	
}

?>