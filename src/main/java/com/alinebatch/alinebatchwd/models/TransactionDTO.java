package com.alinebatch.alinebatchwd.models;


import com.alinebatch.alinebatchwd.caches.CardCache;
import com.alinebatch.alinebatchwd.caches.UserCache;
import lombok.*;

@Getter
@Setter
@ToString
@NoArgsConstructor
@AllArgsConstructor
public class TransactionDTO {

    private Long user;
    private Long card;
    private Integer year;
    private Integer month;
    private Integer day;
    private String time;
    private String amount;
    private String method;
    private String merchant_name;
    private String merchant_city;
    private String merchant_state;
    private String merchant_zip;
    private String mcc;
    private String errors;
    private String fraud;

    public Transaction toTransaction()
    {
        Transaction t = new Transaction();
        //t.setUser(UserCache.getInstance().get(user));
        //t.setCard(CardCache.getInstance().get(user,card));
        t.setYear(year);
        t.setAmount(amount);
        t.setDay(day);
        t.setErrors(errors);
        t.setMonth(month);
        t.setTime(time);
        t.setMethod(method);
        t.setMerchant_state(merchant_state);
        t.setMerchant_city(merchant_city);
        t.setMerchant_name(merchant_name);
        t.setMerchant_zip(merchant_zip);
        t.setFraud(fraud);
        return t;
    }
}
