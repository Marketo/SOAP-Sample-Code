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
    :winning_lead_key_list => {
  		:attribute => {
    		:attr_name => "IDNUM",
    		:attr_value => "2" }
	},
    :losing_lead_key_lists => {
		:key_list => {
			:attribute => {
    			:attr_name => "IDNUM",
    			:attr_value => "15" },
			:attribute! => {
    			:attr_name => "IDNUM",
    			:attr_value => "16" }
    	}
	}
}

response = client.call(:merge_leads, message: request)

puts response