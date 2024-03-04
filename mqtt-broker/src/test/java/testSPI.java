import com.micerlab.iot.mqtt.server.broker.BrokerApplication;
import com.micerlab.iot.mqtt.server.broker.protocol.ProtocolProcess;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

/**
 * @Author hyl
 * @Date 2024/3/3
 */
@SpringBootTest(classes= BrokerApplication.class)
@RunWith(SpringRunner.class)
public class testSPI {
    @Autowired
    private ProtocolProcess protocolProcess;

    @Test
    public void test(){
        System.out.println(protocolProcess.getTypes().size());
    }
}
