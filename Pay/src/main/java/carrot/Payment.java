package carrot;

import javax.persistence.*;
import org.springframework.beans.BeanUtils;
import java.util.List;
import java.util.Date;

@Entity
@Table(name="Payment_table")
public class Payment {

    @Id
    @GeneratedValue(strategy=GenerationType.AUTO)
    private Long paymentId;
    private Integer price;
    private Long wtbId;

    public Long getPaymentId() {
        return paymentId;
    }

    public void setPaymentId(Long paymentId) {
        this.paymentId = paymentId;
    }
    public Integer getPrice() {
        return price;
    }

    public void setPrice(Integer price) {
        this.price = price;
    }

    public Long getWtbId() {
        return wtbId;
    }

    public void setWtbId(Long wtbId) {
        this.wtbId = wtbId;
    }

    @PrePersist
    void onPrePersist()
    {
        System.out.println(price);
        //if(price==1500)
        //{
        //    try{
        //        Thread.sleep(5000);
        //    }
        //    catch(Exception e){
        //        e.printStackTrace();
        //        System.out.println();
        //    }
        //}
    }
}
