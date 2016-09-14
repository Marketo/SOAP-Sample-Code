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
import javax.xml.bind.Marshaller;
 
public class MergeLeads {
 
    public static void main(String[] args) {
        System.out.println("Executing Merge Lead");
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
            ParamsMergeLeads request = new ParamsMergeLeads();
 
            ArrayOfAttribute winningLeadArray = new ArrayOfAttribute();
     
            Attribute winner = new Attribute();
            winner.setAttrName("IDNUM");
            winner.setAttrValue("2");
            winningLeadArray.getAttributes().add(winner);
            request.setWinningLeadKeyList(winningLeadArray);
             
            ArrayOfAttribute losingLeadArray = new ArrayOfAttribute();
             
            Attribute loser = new Attribute();
            loser.setAttrName("IDNUM");
            loser.setAttrValue("15");
            losingLeadArray.getAttributes().add(loser);
             
            ArrayOfAttribute losingLeadArray2 = new ArrayOfAttribute();
            Attribute loser2 = new Attribute();
            loser2.setAttrName("IDNUM");
            loser2.setAttrValue("16");
            losingLeadArray2.getAttributes().add(loser2);
             
            ArrayOfKeyList losingKeyList = new ArrayOfKeyList();
            losingKeyList.getKeyLists().add(losingLeadArray);
            losingKeyList.getKeyLists().add(losingLeadArray2);
            request.setLosingLeadKeyLists(losingKeyList);
             
            SuccessMergeLeads result = port.mergeLeads(request, header);
 
            JAXBContext context = JAXBContext.newInstance(SuccessMergeLeads.class);
            Marshaller m = context.createMarshaller();
            m.setProperty(Marshaller.JAXB_FORMATTED_OUTPUT, true);
            m.marshal(result, System.out);
             
        }
        catch(Exception e) {
            e.printStackTrace();
        }
    }
}