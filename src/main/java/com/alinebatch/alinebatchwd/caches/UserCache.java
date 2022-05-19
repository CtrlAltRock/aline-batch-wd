package com.alinebatch.alinebatchwd.caches;

import com.alinebatch.alinebatchwd.models.User;
import com.alinebatch.alinebatchwd.models.UserDTO;
import lombok.extern.slf4j.Slf4j;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

@Slf4j
public class UserCache {

    public static UserCache instance = null;

    public long latest = 0L;

    public void checkLatest(Long id)
    {
        if (id+1 > getInstance().latest)
        {
            getInstance().latest = id +1;
            log.info("Now serving user: " + (id+1));
        }
    }

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

    public Integer count()
    {
        return UserCache.getInstance().cacheMap.size();
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
