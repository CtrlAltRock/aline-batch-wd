package com.alinebatch.alinebatchwd.caches;

import com.alinebatch.alinebatchwd.models.User;
import com.alinebatch.alinebatchwd.models.UserDTO;

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

    private AbstractMap<Long, UserDTO> cacheMap = new ConcurrentHashMap<>();

    private HashSet<Long> seenCache = new HashSet<>();

    public UserDTO get(Long id)
    {
        return UserCache.getInstance().cacheMap.get(id);
    }

    public ArrayList<Object> collect()
    {
        return new ArrayList<Object>(Arrays.asList(UserCache.getInstance().cacheMap.values().toArray()));
    }

    public UserDTO set(Long id, UserDTO user)
    {
        UserCache.getInstance().cacheMap.put(id, user);
        return user;
    }

    public AbstractMap<Long, UserDTO> getAll()
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
