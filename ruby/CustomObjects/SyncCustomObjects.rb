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

client = Savon.client(wsdl: 'http://app.marketo.com/soap/mktows/2_3?WSDL', soap_header: headers, endpoint: marketoSoapEndPoint, namespaces: namespaces, open_timeout: 90, read_timeout: 90, namespace_identifier: :ns1, env_namespace: 'SOAP-ENV')

#Create Request
request = {
    :obj_type_name => "RoadShow",
    :custom_obj_list => {
        :custom_obj => {
            :custom_obj_key_list => {
                :attribute => {
                    :attr_name => "MKTOID",
                    :attr_value => "1090177" },
                :attribute! => {
                    :attr_name => "rid",
                    :attr_value => "rid1" }
            },
            :custom_obj_attribute_list => {
                :attribute => {
                    :attr_name => "city",
                    :attr_value => "SanMateo" },
                :attribute! => {
                    :attr_name => "zip",
                    :attr_value => "94404" },
                :attribute! => {
                    :attr_name => "state",
                    :attr_value => "California" }
            }
        }
	},
    :operation => "UPSERT"
}

response = client.call(:sync_custom_objects, message: request)

puts response