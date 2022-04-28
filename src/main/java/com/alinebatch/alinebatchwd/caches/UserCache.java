package com.alinebatch.alinebatchwd.caches;

import com.alinebatch.alinebatchwd.models.User;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

public class UserCache {

    public static UserCache instance = null;

    public static UserCache getInstance()
    {
        if (instance == null)
        {
            synchronized (UserCache.class)
            {
                if (instance == null)
                {
                    instance = new UserCache();
                }
            }
        }
        return instance;
    }

    private AbstractMap<Long, User> cacheMap = new ConcurrentHashMap<>();

    private HashSet<Long> seenCache = new HashSet<>();

    public User get(Long id)
    {
        return UserCache.getInstance().cacheMap.get(id);
    }

    public User set(Long id, User user)
    {
        UserCache.getInstance().cacheMap.put(id, user);
        return user;
    }

    public AbstractMap<Long, User> getAll()
    {
        return UserCache.getInstance().cacheMap;
    }

    public HashSet<Long> getSeen()
    {
        return UserCache.getInstance().seenCache;
    }

    public void setSeen(Long id)
    {
        UserCache.getInstance().seenCache.add(id);
    }


}
