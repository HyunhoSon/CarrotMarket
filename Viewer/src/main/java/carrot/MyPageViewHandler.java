package carrot;

import carrot.config.kafka.KafkaProcessor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cloud.stream.annotation.StreamListener;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.util.List;
import java.util.Optional;

@Service
public class MyPageViewHandler {


    @Autowired
    private MyPageRepository myPageRepository;

    @StreamListener(KafkaProcessor.INPUT)
    public void whenWtbAdded_then_CREATE_1 (@Payload WtbAdded wtbAdded) {
        try {

            if (!wtbAdded.validate()) return;

            // view 객체 생성
            MyPage myPage = new MyPage();
            // view 객체에 이벤트의 Value 를 set 함
            myPage.setWtbId(wtbAdded.getWtbId());
            myPage.setPaymentId(wtbAdded.getPaymentId());
            myPage.setProductId(wtbAdded.getProductId());
            myPage.setState(wtbAdded.getState());
            myPage.setPrice(wtbAdded.getPrice());
            // view 레파지 토리에 save
            myPageRepository.save(myPage);
        
        }catch (Exception e){
            e.printStackTrace();
        }
    }


    @StreamListener(KafkaProcessor.INPUT)
    public void whenWtbAccepted_then_UPDATE_1(@Payload WtbAccepted wtbAccepted) {
        try {
            if (!wtbAccepted.validate()) return;
                // view 객체 조회
            Optional<MyPage> myPageOptional = myPageRepository.findByWtbId(wtbAccepted.getWtbId());
            if( myPageOptional.isPresent()) {
                MyPage myPage = myPageOptional.get();
                // view 객체에 이벤트의 eventDirectValue 를 set 함
                    myPage.setState("Accepted");
                // view 레파지 토리에 save
                myPageRepository.save(myPage);
            }
            
        }catch (Exception e){
            e.printStackTrace();
        }
    }
    @StreamListener(KafkaProcessor.INPUT)
    public void whenWtbRejected_then_UPDATE_2(@Payload WtbRejected wtbRejected) {
        try {
            if (!wtbRejected.validate()) return;
                // view 객체 조회
            Optional<MyPage> myPageOptional = myPageRepository.findByWtbId(wtbRejected.getWtbId());
            if( myPageOptional.isPresent()) {
                MyPage myPage = myPageOptional.get();
                // view 객체에 이벤트의 eventDirectValue 를 set 함
                    myPage.setState("Rejected");
                // view 레파지 토리에 save
                myPageRepository.save(myPage);
            }
            
        }catch (Exception e){
            e.printStackTrace();
        }
    }
    @StreamListener(KafkaProcessor.INPUT)
    public void whenDealFinished_then_UPDATE_3(@Payload DealFinished dealFinished) {
        try {
            if (!dealFinished.validate()) return;
                // view 객체 조회
            Optional<MyPage> myPageOptional = myPageRepository.findByWtbId(dealFinished.getWtbId());
            if( myPageOptional.isPresent()) {
                MyPage myPage = myPageOptional.get();
                // view 객체에 이벤트의 eventDirectValue 를 set 함
                    myPage.setState("Finished");
                // view 레파지 토리에 save
                myPageRepository.save(myPage);
            }
            
        }catch (Exception e){
            e.printStackTrace();
        }
    }

}