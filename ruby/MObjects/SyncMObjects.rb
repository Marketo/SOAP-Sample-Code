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
	'ns1:AuthenticationHeader' => { "mktowsUserId" => mktowsUserId, "requestSignature" => requestSignature, 					
	"requestTimestamp"  => requestTimestamp 
	}
}

client = Savon.client(wsdl: 'http://app.marketo.com/soap/mktows/2_3?WSDL', soap_header: headers, endpoint: marketoSoapEndPoint, open_timeout: 90, read_timeout: 90, namespace_identifier: :ns1, env_namespace: 'SOAP-ENV')

#Create Request
request = {
	:m_object_list => {
  		:m_object => {
  			:type => "Program",
  			:id => "1970",
  			:type_attrib_list => {
  				:type_attrib => {
  					:attr_type => "Cost",
  					:attr_list => {
  						:attrib => {
  							:name => "Month",
  							:value => "2013-06" },
						:attrib! => {
  							:name => "Amount",
  							:value =>  "2000" },
						:attrib! => {
  							:name => "Id",
  							:value => "153" }
					}
				}
			}
		}
	},
  	:operation => "UPDATE"
}

response = client.call(:sync_m_objects, message: request)

puts response