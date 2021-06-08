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
    @Autowired WtbRepository wtbRepository;

    @StreamListener(KafkaProcessor.INPUT)
    public void wheneverWtbAccepted_StateChangeWtb(@Payload WtbAccepted wtbAccepted){

        if(!wtbAccepted.validate()) return;

        System.out.println("\n\n##### listener StateChangeWtb : " + wtbAccepted.toJson() + "\n\n");

        // Sample Logic //
        Wtb wtb = new Wtb();
        wtbRepository.save(wtb);
            
    }
    @StreamListener(KafkaProcessor.INPUT)
    public void wheneverWtbRejected_StateChangeWtb(@Payload WtbRejected wtbRejected){

        if(!wtbRejected.validate()) return;

        System.out.println("\n\n##### listener StateChangeWtb : " + wtbRejected.toJson() + "\n\n");

        // Sample Logic //
        Wtb wtb = new Wtb();
        wtbRepository.save(wtb);
            
    }


    @StreamListener(KafkaProcessor.INPUT)
    public void whatever(@Payload String eventString){}


}
