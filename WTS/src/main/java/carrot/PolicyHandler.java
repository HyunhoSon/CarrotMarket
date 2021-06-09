package carrot;

import carrot.config.kafka.KafkaProcessor;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cloud.stream.annotation.StreamListener;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.stereotype.Service;

@Service
public class PolicyHandler{
    @Autowired WtbInboxRepository wtbInboxRepository;
    @Autowired WtsRepository wtsRepository;

    @StreamListener(KafkaProcessor.INPUT)
    public void wheneverWtbAdded_NewWtBtoInbox(@Payload WtbAdded wtbAdded){

        if(!wtbAdded.validate()) return;

        System.out.println("\n\n##### listener NewWtBtoInbox : " + wtbAdded.toJson() + "\n\n");

        WtbInbox wtbInbox = new WtbInbox();
        wtbInbox.setWtbId(wtbAdded.getWtbId());
        wtbInbox.setProductId(wtbAdded.getProductId());
        wtbInbox.setState("Pending");
        
        wtbInboxRepository.save(wtbInbox);
    }


    @StreamListener(KafkaProcessor.INPUT)
    public void whatever(@Payload String eventString){}


}
