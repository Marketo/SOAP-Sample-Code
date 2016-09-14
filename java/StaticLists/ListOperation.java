/*
   Marketo SOAP API Sample Code
   Copyright (C) 2016 Marketo, Inc.

   This software may be modified and distributed under the terms
   of the MIT license.  See the LICENSE file for details.
*/
import com.marketo.mktows.*;
import java.net.URL;
import javax.xml.namespace.QName;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;
import org.apache.commons.codec.binary.Hex;
import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBElement;
import javax.xml.bind.Marshaller;
 
 
public class ListOperation {
    public static void main(String[] args) {
 
        System.out.println("Executing List Operation");
        try {
            URL marketoSoapEndPoint = new URL("CHANGE ME" + "?WSDL");
            String marketoUserId = "CHANGE ME";
            String marketoSecretKey = "CHANGE ME";
             
            QName serviceName = new QName("http://www.marketo.com/mktows/", "MktMktowsApiService");
            MktMktowsApiService service = new MktMktowsApiService(marketoSoapEndPoint, serviceName);
            MktowsPort port = service.getMktowsApiSoapPort();
             
            // Create Signature
            DateFormat df = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ssZ");
            String text = df.format(new Date());
            String requestTimestamp = text.substring(0, 22) + ":" + text.substring(22);           
            String encryptString = requestTimestamp + marketoUserId ;
             
            SecretKeySpec secretKey = new SecretKeySpec(marketoSecretKey.getBytes(), "HmacSHA1");
            Mac mac = Mac.getInstance("HmacSHA1");
            mac.init(secretKey);
            byte[] rawHmac = mac.doFinal(encryptString.getBytes());
            char[] hexChars = Hex.encodeHex(rawHmac);
            String signature = new String(hexChars); 
             
            // Set Authentication Header
            AuthenticationHeader header = new AuthenticationHeader();
            header.setMktowsUserId(marketoUserId);
            header.setRequestTimestamp(requestTimestamp);
            header.setRequestSignature(signature);
             
            // Create Request
            ParamsListOperation request = new ParamsListOperation();
            request.setListOperation(ListOperationType.ISMEMBEROFLIST);
             
            ListKey listKey = new ListKey();
            listKey.setKeyType(ListKeyType.MKTOLISTNAME);
            listKey.setKeyValue("Trav-Test-List");
            request.setListKey(listKey);
             
            LeadKey key = new LeadKey();
            key.setKeyType(LeadKeyRef.IDNUM);
            key.setKeyValue("87710");
             
            LeadKey key2 = new LeadKey();
            key2.setKeyType(LeadKeyRef.IDNUM);
            key2.setKeyValue("1089946");
             
            ArrayOfLeadKey leadKeys = new ArrayOfLeadKey();
            leadKeys.getLeadKeies().add(key);
            leadKeys.getLeadKeies().add(key2);
             
            request.setListMemberList(leadKeys);
             
            JAXBElement<Boolean> strict = new ObjectFactory().createParamsListOperationStrict(false);
            request.setStrict(strict);
             
            SuccessListOperation result = port.listOperation(request, header);
             
            JAXBContext context = JAXBContext.newInstance(SuccessListOperation.class);
            Marshaller m = context.createMarshaller();
            m.setProperty(Marshaller.JAXB_FORMATTED_OUTPUT, true);
            m.marshal(result, System.out);
             
        }
        catch(Exception e) {
            e.printStackTrace();
        }
    }
}