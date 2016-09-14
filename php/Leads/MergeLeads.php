<?php
/*
   Marketo SOAP API Sample Code
   Copyright (C) 2016 Marketo, Inc.

   This software may be modified and distributed under the terms
   of the MIT license.  See the LICENSE file for details.
*/
$debug = true;
$marketoSoapEndPoint    = "";  // CHANGE ME
$marketoUserId      = "";  // CHANGE ME
$marketoSecretKey   = ""; // CHANGE ME
$marketoNameSpace   = "http://www.marketo.com/mktows/";
 
// Create Signature
$dtzObj = new DateTimeZone("America/Los_Angeles");
$dtObj  = new DateTime('now', $dtzObj);
$timeStamp = $dtObj->format(DATE_W3C);
$encryptString = $timeStamp . $marketoUserId;
$signature = hash_hmac('sha1', $encryptString, $marketoSecretKey);
 
// Create SOAP Header
$attrs = new stdClass();
$attrs->mktowsUserId = $marketoUserId;
$attrs->requestSignature = $signature;
$attrs->requestTimestamp = $timeStamp;
$authHdr = new SoapHeader($marketoNameSpace, 'AuthenticationHeader', $attrs);
$options = array("connection_timeout" => 15, "location" => $marketoSoapEndPoint);
if ($debug) {
  $options["trace"] = 1;
}
 
// Create Request
$keyAttrib1 = new stdClass();
$keyAttrib1->attrName = 'IDNUM';
$keyAttrib1->attrValue = '2';
$winningKeyList = new stdClass();
$winningKeyList->attribute = array($keyAttrib1);
$loser1 = new stdClass();
$loser1->attrName = "IDNUM";
$loser1->attrValue = "15";
$loser2 = new stdClass();
$loser2->attrName = "IDNUM";
$loser2->attrValue = "16";
$keyList1 = new stdClass();
$keyList1->attribute = array($loser1); 
$keyList2 = new stdClass();
$keyList2->attribute = array($loser2); 
$params = new stdClass();
$params->winningLeadKeyList = $winningKeyList;
$params->losingLeadKeyLists = array($keyList1, $keyList2);
$soapClient = new SoapClient($marketoSoapEndPoint ."?WSDL", $options);
try {
  $leads = $soapClient->__soapCall('mergeLeads', array($params), $options, $authHdr);
  //      print_r($leads);
}
catch(Exception $ex) {
  var_dump($ex);
}
if ($debug) {
  print "RAW request:\n" .$soapClient->__getLastRequest() ."\n";
  print "RAW response:\n" .$soapClient->__getLastResponse() ."\n";
}
?>