import java.net.URL;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;
import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBElement;
import javax.xml.bind.Marshaller;
import javax.xml.namespace.QName;
import org.apache.commons.codec.binary.Hex;
import com.marketo.mktows.ArrayOfString;
import com.marketo.mktows.AuthenticationHeader;
import com.marketo.mktows.MktMktowsApiService;
import com.marketo.mktows.MktowsPort;
import com.marketo.mktows.ObjectFactory;
import com.marketo.mktows.ParamsGetChannels;
import com.marketo.mktows.SuccessGetChannels;
import com.marketo.mktows.SuccessGetLead;
import com.marketo.mktows.SuccessGetMultipleLeads;
import com.marketo.mktows.Tag;
  
public class GetChannels {
  
    public static void main(String[] args) {
        System.out.println("Executing Get Channels");
        try {
            URL marketoSoapEndPoint = new URL("CHANGEME" + "?WSDL");
            String marketoUserId = "CHANGEME";
            String marketoSecretKey = "CHANGEME";
              
            QName serviceName = new QName("http://www.marketo.com/mktows/", "MktMktowsApiService");
            MktMktowsApiService service = new MktMktowsApiService(marketoSoapEndPoint, serviceName);
              
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
            ParamsGetChannels params = new ParamsGetChannels();
            Tag tags = new Tag();
            ArrayOfString tagArray = new ArrayOfString();
            tagArray.getStringItems().add("Webinar");
            tagArray.getStringItems().add("Blog");
            tagArray.getStringItems().add("Tradeshow");
            tags.setValues(tagArray);
                        
            MktowsPort port = service.getMktowsApiSoapPort();
            SuccessGetChannels result = port.getChannels(params, header);
  
            JAXBContext context = JAXBContext.newInstance(SuccessGetLead.class);
            Marshaller m = context.createMarshaller();
            m.setProperty(Marshaller.JAXB_FORMATTED_OUTPUT, true);
            m.marshal(result, System.out);
        }
        catch(Exception e) {
            e.printStackTrace();
        }
    }
}