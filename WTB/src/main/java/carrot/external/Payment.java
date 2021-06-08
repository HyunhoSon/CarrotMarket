package carrot.external;

public class Payment {

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
}
