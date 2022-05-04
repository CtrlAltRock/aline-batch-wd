package com.alinebatch.alinebatchwd.analytics;

import java.util.ArrayList;
import java.lang.Class;
import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.lang.reflect.Method;

public class Querier {
    public static QueryList create(Class ofType)
    {
        return new QueryList(ofType);
    }

}
