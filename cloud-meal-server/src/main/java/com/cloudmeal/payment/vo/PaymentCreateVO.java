package com.cloudmeal.payment.vo;

public record PaymentCreateVO(String mode, String status, String appId, String timeStamp,
                              String nonceStr, String packageValue, String signType, String paySign) {
    public static PaymentCreateVO mockPaid() {
        return new PaymentCreateVO("MOCK", "PAID", null, null, null, null, null, null);
    }

    public static PaymentCreateVO freePaid() {
        return new PaymentCreateVO("FREE", "PAID", null, null, null, null, null, null);
    }
}
