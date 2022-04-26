package com.alinebatch.alinebatchwd.processors;

import com.alinebatch.alinebatchwd.models.User;
import lombok.extern.slf4j.Slf4j;
import org.springframework.batch.item.ItemProcessor;

@Slf4j
public class UserCacheProcessor implements ItemProcessor<User, User> {

    @Override
    public User process(User user)
    {
        log.info(user.toString());
        return user;
    }
}
