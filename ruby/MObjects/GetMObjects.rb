#
# Marketo SOAP API Sample Code
# Copyright (C) 2016 Marketo, Inc.
#
# This software may be modified and distributed under the terms
# of the MIT license.  See the LICENSE file for details.
#
require 'savon' # Use version 2.0 Savon gem
require 'date'

mktowsUserId = "" # CHANGE ME
marketoSecretKey = "" # CHANGE ME
marketoSoapEndPoint = "" # CHANGE ME
marketoNameSpace = "http://www.marketo.com/mktows/"

#Create Signature
Timestamp = DateTime.now
requestTimestamp = Timestamp.to_s
encryptString = requestTimestamp + mktowsUserId
digest = OpenSSL::Digest.new('sha1')
hashedsignature = OpenSSL::HMAC.hexdigest(digest, marketoSecretKey, encryptString)
requestSignature = hashedsignature.to_s

#Create SOAP Header
headers = { 
	'ns1:AuthenticationHeader' =&gt; { "mktowsUserId" =&gt; mktowsUserId, "requestSignature" =&gt; requestSignature, 					
	"requestTimestamp"  =&gt; requestTimestamp 
	}
}

client = Savon.client(wsdl: 'http://app.marketo.com/soap/mktows/2_3?WSDL', soap_header: headers, endpoint: marketoSoapEndPoint, open_timeout: 90, read_timeout: 90, namespace_identifier: :ns1, env_namespace: 'SOAP-ENV')

#Create Request
request = {
	:type =&gt; "Program",
	:m_obj_criteria_list =&gt; {
  		:m_obj_criteria =&gt; {
    		:attr_name =&gt; "Id",
    		:comparsion =&gt; "LE",
    		:attr_value =&gt; "1010"
  		},
  		:m_obj_criteria! =&gt; {
    		:attr_name =&gt; "Name",
    		:comparsion =&gt; "NE",
    		:attr_value =&gt; "elizprogramtest"
  		}
  	}
}

response = client.call(:get_m_objects, message: request)

puts response