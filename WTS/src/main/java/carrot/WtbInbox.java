package carrot;

import javax.persistence.*;
import org.springframework.beans.BeanUtils;
import java.util.List;
import java.util.Date;

@Entity
@Table(name="WtbInbox_table")
public class WtbInbox {

    private Long inboxId;
    private Long wtbId;
    private Long paymentId;
    private Long productId;

    @PostUpdate
    public void onPostUpdate(){
        WtbAccepted wtbAccepted = new WtbAccepted();
        BeanUtils.copyProperties(this, wtbAccepted);
        wtbAccepted.publishAfterCommit();


        WtbRejected wtbRejected = new WtbRejected();
        BeanUtils.copyProperties(this, wtbRejected);
        wtbRejected.publishAfterCommit();


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
