package carrot;

public class WtbAccepted extends AbstractEvent {

    private Long inboxId;
    private Long wtbId;
    private Long paymentId;
    private Long productId;

    public Long getId() {
        return inboxId;
    }

    public void setId(Long inboxId) {
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