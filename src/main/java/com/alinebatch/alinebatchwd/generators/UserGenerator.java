package com.alinebatch.alinebatchwd.generators;

import com.alinebatch.alinebatchwd.models.UserDTO;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.ArrayList;

public class UserGenerator {

    Faker faker = new Faker();

    public UserDTO generateUser(Long id)
    {
        UserDTO user = new UserDTO();
        user.setId(id);
        user.setFirstName(faker.firstName());
        user.setLastName(faker.lastName());
        user.setIbCount(0);
        user.setPassword(faker.generatePassword());
        user.setUserName(user.getFirstName()+user.getLastName()+user.getId());
        user.setEmail(user.getUserName() + "@smoothjava.com");
        user.setCards(new ArrayList<>());
        user.setPhone(faker.phone());
        return user;
    }
}
