package carrot;

import javax.persistence.*;
import java.util.List;

@Entity
@Table(name="MyPage_table")
public class MyPage {

        @Id
        @GeneratedValue(strategy=GenerationType.AUTO)
        private Long wtbId;
        private Long paymentId;
        private Long productId;
        private Integer price;
        private String state;


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
        public Integer getPrice() {
            return price;
        }

        public void setPrice(Integer price) {
            this.price = price;
        }
        public String getState() {
            return state;
        }

        public void setState(String state) {
            this.state = state;
        }

}
