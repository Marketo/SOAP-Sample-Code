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
 
public class SyncMultipleLeads {
 
    public static void main(String[] args) {
        System.out.println("Executing syncMultipleLeads");
        try {
            URL marketoSoapEndPoint = new URL("https://100-AEK-913.mktoapi.com/soap/mktows/2_1" + "?WSDL");
            String marketoUserId = "demo17_1_809934544BFABAE58E5D27";
            String marketoSecretKey = "27272727aa";
             
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
            ParamsSyncMultipleLeads request = new ParamsSyncMultipleLeads();
             
            ObjectFactory objectFactory = new ObjectFactory();
 
            JAXBElement<Boolean> dedup = objectFactory.createParamsSyncMultipleLeadsDedupEnabled(true);
            request.setDedupEnabled(dedup);
 
            ArrayOfLeadRecord arrayOfLeadRecords = new ArrayOfLeadRecord();
             
            // Create First Lead Record
            LeadRecord rec1 = new LeadRecord();
             
            JAXBElement<String> email = objectFactory.createLeadRecordEmail("t@t.com");
            rec1.setEmail(email);           
             
            Attribute attr1 = new Attribute();
            attr1.setAttrName("FirstName");
            attr1.setAttrValue("George");
             
            Attribute attr2 = new Attribute();
            attr2.setAttrName("LastName");
            attr2.setAttrValue("of the Jungle");
             
            ArrayOfAttribute aoa = new ArrayOfAttribute();
            aoa.getAttributes().add(attr1);
            aoa.getAttributes().add(attr2);
             
            QName qname = new QName("http://www.marketo.com/mktows/", "leadAttributeList");
            JAXBElement<ArrayOfAttribute> attrList = new JAXBElement(qname, ArrayOfAttribute.class, aoa);
             
            rec1.setLeadAttributeList(attrList);
            arrayOfLeadRecords.getLeadRecords().add(rec1);
             
            // Create Second Lead Record
            LeadRecord rec2 = new LeadRecord();
             
            JAXBElement<String> email2 = objectFactory.createLeadRecordEmail("myemail@test.com");
            rec2.setEmail(email2);          
             
            Attribute attr11 = new Attribute();
            attr11.setAttrName("FirstName");
            attr11.setAttrValue("Nancy");
             
            Attribute attr21 = new Attribute();
            attr21.setAttrName("LastName");
            attr21.setAttrValue("Lady");
             
            ArrayOfAttribute aoa2 = new ArrayOfAttribute();
            aoa2.getAttributes().add(attr11);
            aoa2.getAttributes().add(attr21);
             
            qname = new QName("http://www.marketo.com/mktows/", "leadAttributeList");
            JAXBElement<ArrayOfAttribute> attrList2 = new JAXBElement(qname, ArrayOfAttribute.class, aoa2);
             
            rec2.setLeadAttributeList(attrList);
            arrayOfLeadRecords.getLeadRecords().add(rec2);
             
            request.setLeadRecordList(arrayOfLeadRecords);          
             
            SuccessSyncMultipleLeads result = port.syncMultipleLeads(request, header);
 
            JAXBContext context = JAXBContext.newInstance(SuccessSyncMultipleLeads.class);
            Marshaller m = context.createMarshaller();
            m.setProperty(Marshaller.JAXB_FORMATTED_OUTPUT, true);
            m.marshal(result, System.out);
             
        }
        catch(Exception e) {
            e.printStackTrace();
        }
    }
}