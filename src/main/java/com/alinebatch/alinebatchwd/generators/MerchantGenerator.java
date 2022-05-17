package com.alinebatch.alinebatchwd.generators;

import com.alinebatch.alinebatchwd.models.Merchant;

public class MerchantGenerator {

    Faker faker = new Faker();

    public Merchant generateMerchant(Long id, String state, String city, String zip)
    {
        Merchant merchant = new Merchant();
        merchant.setName(faker.merchantName());
        merchant.setState(state);
        merchant.setZip(zip);
        merchant.setCity(city);
        return merchant;
    }
}
