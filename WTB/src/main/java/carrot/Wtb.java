package carrot;

import javax.persistence.*;
import javax.print.attribute.standard.Finishings;

import org.springframework.beans.BeanUtils;
import java.util.List;
import java.util.Date;

@Entity
@Table(name="Wtb_table")
public class Wtb {
    @Id
    @GeneratedValue(strategy=GenerationType.IDENTITY)
    private Long wtbId;
    private Long paymentId;
    private String state;
    private Integer price;
    private Long productId;

    @PostUpdate
    public void onPostUpdate(){

        if(this.state.equals("Finished"))
        {
            DealFinished dealFinished = new DealFinished();
            BeanUtils.copyProperties(this, dealFinished);
            dealFinished.publishAfterCommit();
        }
        
    }


    @PostPersist
    public void onPostPersist(){
        
        carrot.external.Payment payment = new carrot.external.Payment();
        payment.setWtbId(this.wtbId);
        payment.setPrice(10000);

        try{
            WtbApplication.applicationContext.getBean(carrot.external.PaymentService.class)
            .pay(payment);
        }
        catch(Exception e)
        {
            e.printStackTrace();
            return;
        }

        this.state="Requested";

        WtbAdded wtbAdded = new WtbAdded();
        BeanUtils.copyProperties(this, wtbAdded);
        wtbAdded.publishAfterCommit();

        
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
