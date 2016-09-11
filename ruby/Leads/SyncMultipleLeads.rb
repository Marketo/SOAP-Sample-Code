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
	:lead_record_list => {
   		:lead_record => {
   			:email => "em1000@etestd.marketo.net",
    		:lead_attribute_list => {
    			:attribute => {
    				:attr_name => "Company",
    				:attr_value => "Marketo1000" },
   			 :attribute! => {
    				:attr_name => "Phone",
    				:attr_value => "650-555-1000" }
    		}
    	},
    	:lead_record! => {
   			:email => "em1001@etestd.marketo.net",
    		:lead_attribute_list => {
    			:attribute => {
    				:attr_name => "Company",
    				:attr_value => "Marketo1001" },
   			 :attribute! => {
    				:attr_name => "Phone",
    				:attr_value => "650-555-1001" }
    		}
    	}
	},
    :dedup_enabled => "True"
}

response = client.call(:sync_multiple_leads, message: request)

puts response