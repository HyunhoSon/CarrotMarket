package carrot;

import javax.persistence.*;
import org.springframework.beans.BeanUtils;
import java.util.List;
import java.util.Date;

@Entity
@Table(name="Wtb_table")
public class Wtb {

    private Long wtbId;
    private Long paymentId;
    private String state;
    private Integer price;
    private Long productId;

    @PostUpdate
    public void onPostUpdate(){
        DealFinished dealFinished = new DealFinished();
        BeanUtils.copyProperties(this, dealFinished);
        dealFinished.publishAfterCommit();


    }

    @PrePersist
    public void onPrePersist(){
        WtbAdded wtbAdded = new WtbAdded();
        BeanUtils.copyProperties(this, wtbAdded);
        wtbAdded.publishAfterCommit();

        //Following code causes dependency to external APIs
        // it is NOT A GOOD PRACTICE. instead, Event-Policy mapping is recommended.

        carrot.external.Payment payment = new carrot.external.Payment();
        // mappings goes here
        Application.applicationContext.getBean(carrot.external.PaymentService.class)
            .pay(payment);


    }


    public Long getWtbId() {
        return wtbId;
    }

    public void setWtbId(Long wtbId) {
        this.wtbId = wtbId;
    }
    public Long getPaymentId() {
        return paymentId;
    }

    public void setPaymentId(Long paymentId) {
        this.paymentId = paymentId;
    }
    public String getState() {
        return state;
    }

    public void setState(String state) {
        this.state = state;
    }
    public Integer getPrice() {
        return price;
    }

    public void setPrice(Integer price) {
        this.price = price;
    }
    public Long getProductId() {
        return productId;
    }

    public void setProductId(Long productId) {
        this.productId = productId;
    }




}
