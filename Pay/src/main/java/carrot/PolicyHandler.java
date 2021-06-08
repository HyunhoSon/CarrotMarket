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
    @Autowired PaymentRepository paymentRepository;

    @StreamListener(KafkaProcessor.INPUT)
    public void wheneverDealFinished_SendToSeller(@Payload DealFinished dealFinished){

        if(!dealFinished.validate()) return;

        System.out.println("\n\n##### listener SendToSeller : " + dealFinished.toJson() + "\n\n");

        // Sample Logic //
        Payment payment = new Payment();
        paymentRepository.save(payment);
            
    }
    @StreamListener(KafkaProcessor.INPUT)
    public void wheneverWtbRejected_Refund(@Payload WtbRejected wtbRejected){

        if(!wtbRejected.validate()) return;

        System.out.println("\n\n##### listener Refund : " + wtbRejected.toJson() + "\n\n");

        // Sample Logic //
        Payment payment = new Payment();
        paymentRepository.save(payment);
            
    }


    @StreamListener(KafkaProcessor.INPUT)
    public void whatever(@Payload String eventString){}


}
