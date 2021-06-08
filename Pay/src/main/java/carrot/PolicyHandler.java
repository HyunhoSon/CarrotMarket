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

        Long wtbId = dealFinished.getWtbId();
        Payment payment = paymentRepository.findByWtbId(wtbId);
        System.out.println("**** Send To Seller "+ payment.getPrice() + "  Bcs WTB " + wtbId + "is Finished ****" );
            
    }
    @StreamListener(KafkaProcessor.INPUT)
    public void wheneverWtbRejected_Refund(@Payload WtbRejected wtbRejected){

        if(!wtbRejected.validate()) return;

        System.out.println("\n\n##### listener Refund : " + wtbRejected.toJson() + "\n\n");

        Long wtbId = wtbRejected.getWtbId();
        Payment payment = paymentRepository.findByWtbId(wtbId);
        System.out.println("**** Refund "+ payment.getPrice() + "  Bcs WTB " + wtbId + "is Rejected ****" );
    }


    @StreamListener(KafkaProcessor.INPUT)
    public void whatever(@Payload String eventString){}


}
