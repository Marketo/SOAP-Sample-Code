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
 
 
public class SyncCustomObjects {
    public static void main(String[] args) {
        System.out.println("Executing Sync Custom Objects");
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
            ParamsSyncCustomObjects request = new ParamsSyncCustomObjects();
            request.setObjTypeName("RoadShow");
            JAXBElement<SyncOperationEnum> operation = new ObjectFactory().createParamsSyncCustomObjectsOperation(SyncOperationEnum.UPSERT);
            request.setOperation(operation);
             
            ArrayOfCustomObj customObjects = new ArrayOfCustomObj();
             
            CustomObj customObj = new CustomObj();
             
            ArrayOfAttribute arrayOfKeyAttributes = new ArrayOfAttribute();         
            Attribute attr = new Attribute();
            attr.setAttrName("MKTOID");
            attr.setAttrValue("1090177");
             
            Attribute attr2 = new Attribute();
            attr2.setAttrName("rid");
            attr2.setAttrValue("rid1");
             
            arrayOfKeyAttributes.getAttributes().add(attr);
            arrayOfKeyAttributes.getAttributes().add(attr2);
             
 
            JAXBElement<ArrayOfAttribute> keyAttributes = new ObjectFactory().createCustomObjCustomObjKeyList(arrayOfKeyAttributes);
            customObj.setCustomObjKeyList(keyAttributes);
            ArrayOfAttribute arrayOfValueAttributes = new ArrayOfAttribute();
             
            Attribute city = new Attribute();
            city.setAttrName("city");
            city.setAttrValue("SanMateo");
             
            Attribute zip = new Attribute();
            zip.setAttrName("zip");
            zip.setAttrValue("94404");
             
            Attribute state = new Attribute();
            state.setAttrName("state");
            state.setAttrValue("California");
             
            arrayOfValueAttributes.getAttributes().add(city);
            arrayOfValueAttributes.getAttributes().add(state);
            arrayOfValueAttributes.getAttributes().add(zip);
             
            JAXBElement<ArrayOfAttribute> valueAttributes = new ObjectFactory().createCustomObjCustomObjAttributeList(arrayOfValueAttributes);
            customObj.setCustomObjAttributeList(valueAttributes);
             
            customObjects.getCustomObjs().add(customObj);
 
            SuccessSyncCustomObjects result = port.syncCustomObjects(request, header);
 
            JAXBContext context = JAXBContext.newInstance(SuccessSyncCustomObjects.class);
            Marshaller m = context.createMarshaller();
            m.setProperty(Marshaller.JAXB_FORMATTED_OUTPUT, true);
            m.marshal(result, System.out);
             
        }
        catch(Exception e) {
            e.printStackTrace();
        }
    }
}