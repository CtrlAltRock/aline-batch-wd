package com.alinebatch.alinebatchwd.analytics;

import lombok.extern.slf4j.Slf4j;

import javax.persistence.criteria.CriteriaBuilder;
import java.lang.reflect.Array;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.NoSuchElementException;

@Slf4j
public class QueryList {

    private Class ofType;

    private Field[] fields;

    private Method[] methods;

    private ArrayList<Object> collection;

    public QueryList(Class type)
    {
        this.ofType = type;
        this.fields = getFields();
        this.methods = getMethods();
    }

    public QueryList define(ArrayList<Object> collection)
    {
        this.collection = collection;
        return this;
    }

    public static ArrayList<String> getTopX(HashMap<String, Integer> collection, Integer count)
    {
        ArrayList<String> topX = new ArrayList<>();
        collection.forEach((key, value) ->
        {

            int i = 0;
            int storedVal = value;
            String storedString = key + " " + value;
            while (i < count)
            {
                if (topX.size() == count)
                {
                    if (storedVal < decode(topX.get(count - 1)))
                    {
                        break;
                    }
                }
                while (i < count)
                {
                    if (i == topX.size())
                    {

                        topX.add(storedString);
                        break;
                    }
                    int queriedValue = decode(topX.get(i));
                    if (queriedValue< storedVal)
                    {
                        String newString = topX.get(i);
                        topX.set(i, storedString);
                        storedString = newString;
                        storedVal = queriedValue;
                    }
                    i++;
                }
            }
        });

        return topX;
    }

    public static Integer decode(String entry)
    {
        try {
            return Integer.parseInt(entry.split(" ")[1]);
        } catch (Exception e)
        {
            log.info("Empty or malformed value, returning default 0");
            return 0;
        }
    }


    public HashMap<String, Integer> countBy(String field)
    {
        HashMap<String, Integer> filteredMap = new HashMap<>();
        Iterator<Object> iter = collection.iterator();
        while (iter.hasNext())
        {
            Object o = iter.next();
            Method getter = getMethod(field);
            try {
                String answer = getter.invoke(o).toString();
                if (filteredMap.get(answer) == null)
                {
                    filteredMap.put(answer, 0);
                }
                int count = filteredMap.get(answer);
                count += 1;
                filteredMap.put(answer, count);
            } catch (Exception e)
            {
                log.info("Malformed expression in query");
            }
        }
        return filteredMap;
    }

    public HashMap<String, ArrayList<Object>> groupBy(String field)
    {
        HashMap<String, ArrayList<Object>> filteredMap = new HashMap<>();
        Iterator<Object> iter = collection.iterator();
        while (iter.hasNext())
        {
            Object o = iter.next();
            Method getter = getMethod(field);
            try {
                String answer = getter.invoke(o).toString();
                if (filteredMap.get(answer) == null)
                {
                    filteredMap.put(answer, new ArrayList<>());
                }
                ArrayList<Object> set = filteredMap.get(answer);
                set.add(o);
                filteredMap.put(answer, set);
            } catch (Exception e)
            {
                log.info("Malformed expression in query");
            }
        }
        return filteredMap;
    }




    public ArrayList<Object> collect()
    {
        return collection;
    }

    public QueryList whereLargerThan(String fieldName, String value)
    {
        ArrayList<Object> filteredList = new ArrayList<>();
        Method getter = getMethod(fieldName);
        Iterator<Object> iter = collection.iterator();
        while (iter.hasNext())
        {
            String answer = "";
            Object o = iter.next();
            try {
                answer = getter.invoke(o).toString();
            } catch (Exception e)
            {

            }
            try
            {
                if (Double.parseDouble(answer) > Double.parseDouble(value))
                {
                    filteredList.add(o);
                }
            } catch (NumberFormatException e)
            {
                log.info("The field provided is not convertible to a double, please adjust your query");
            }

        }
        QueryList r = new QueryList(ofType);
        return r.define(filteredList);
    }

    public QueryList whereSmallerThan(String fieldName, String value)
    {
        ArrayList<Object> filteredList = new ArrayList<>();
        Method getter = getMethod(fieldName);
        Iterator<Object> iter = collection.iterator();
        while (iter.hasNext())
        {
            String answer = "";
            Object o = iter.next();
            try {
                answer = getter.invoke(o).toString();
            } catch (Exception e)
            {

            }
            try
            {
                if (Double.parseDouble(answer) < Double.parseDouble(value))
                {
                    filteredList.add(o);
                }
            } catch (NumberFormatException e)
            {
                log.info("The field provided is not convertible to a double, please adjust your query");
            }

        }
        QueryList r = new QueryList(ofType);
        return r.define(filteredList);
    }

    public QueryList whereEquals(String fieldName, String value)
    {
        ArrayList<Object> filteredList = new ArrayList<>();
        Method getter = getMethod(fieldName);
        Iterator<Object> iter = collection.iterator();
        while (iter.hasNext())
        {
            String answer = "";
            Object o = iter.next();
            try {
                answer = getter.invoke(o).toString();
            } catch (Exception e)
            {

            }
            if (answer.equals(value))
            {
                filteredList.add(o);
            }
        }
        QueryList r = new QueryList(ofType);
        return r.define(filteredList);
    }

    private Method getMethod(String fieldName)
    {
        String against = "GET" + fieldName.toUpperCase();
        for (int i = 0; i < methods.length; i++)
        {
            Method is = methods[i];
            if (is.getName().toUpperCase().equals(against))
            {
                return is;
            }
        }
        throw new NoSuchElementException();
    }

    public Field[] getFields()
    {
        return this.ofType.getFields();
    }

    public Method[] getMethods()
    {
        return this.ofType.getMethods();
    }
}
