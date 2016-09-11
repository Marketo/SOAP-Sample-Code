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
 
 
public class getMObjects {
 
    public static void main(String[] args) {
        System.out.println("Executing Get MObjects");
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
            ParamsGetMObjects request = new ParamsGetMObjects();
            request.setType("Program");
             
            MObjCriteria criteria = new MObjCriteria();
            criteria.setAttrName("Id");
            criteria.setComparison(ComparisonEnum.LE);
            criteria.setAttrValue("1010");
             
            MObjCriteria criteria2 = new MObjCriteria();
            criteria2.setAttrName("Name");
            criteria2.setComparison(ComparisonEnum.NE);
            criteria2.setAttrValue("elizprogramtest");
             
            ArrayOfMObjCriteria mObjCriteria= new ArrayOfMObjCriteria();
            mObjCriteria.getMObjCriterias().add(criteria);
            mObjCriteria.getMObjCriterias().add(criteria2);
             
            request.setMObjCriteriaList(mObjCriteria);
 
            SuccessGetMObjects result = port.getMObjects(request, header);
 
            JAXBContext context = JAXBContext.newInstance(SuccessGetMObjects.class);
            Marshaller m = context.createMarshaller();
            m.setProperty(Marshaller.JAXB_FORMATTED_OUTPUT, true);
            m.marshal(result, System.out);
             
        }
        catch(Exception e) {
            e.printStackTrace();
        }
    }
}