package carrot;

import javax.persistence.*;
import org.springframework.beans.BeanUtils;

@Entity
@Table(name="WtbInbox_table")
public class WtbInbox {

    private Long inboxId;
    private Long wtbId;
    private Long paymentId;
    private Long productId;
    private String state;

    @PostUpdate
    public void onPostUpdate(){

        if(state.equals("Accepted")){
            WtbAccepted wtbAccepted = new WtbAccepted();
            BeanUtils.copyProperties(this, wtbAccepted);
            wtbAccepted.publishAfterCommit();
        }
        
        if(state.equals("Rejected")){
            WtbRejected wtbRejected = new WtbRejected();
            BeanUtils.copyProperties(this, wtbRejected);
            wtbRejected.publishAfterCommit();
        }

    }

    public String getState() {
        return state;
    }

    public void setState(String state) {
        this.state = state;
    }

    public Long getInboxId() {
        return inboxId;
    }

    public void setInboxId(Long inboxId) {
        this.inboxId = inboxId;
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
    public Long getProductId() {
        return productId;
    }

    public void setProductId(Long productId) {
        this.productId = productId;
    }




}
