package com.alinebatch.alinebatchwd.generators;

import com.alinebatch.alinebatchwd.models.CardDTO;

public class CardGenerator {

    Faker faker = new Faker();

    public CardDTO generateCard(Long userId, Long cardId)
    {
        CardDTO card = new CardDTO();
        card.setId(cardId);
        card.setCardNumber(faker.cardNumber());
        card.setCvv(faker.cvv());
        card.setExpiration(faker.expiration(true));
        card.setUserId(userId);
        return card;
    }
}
