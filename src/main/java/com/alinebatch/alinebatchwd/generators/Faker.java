package com.alinebatch.alinebatchwd.generators;

import com.vangogiel.luhnalgorithms.LuhnAlgorithms;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.time.LocalDate;

@Component
@Slf4j
public class Faker {

    public static String[] firstSyllables = new String[]{"Te","Re","Bo","Le","Vi","Yo","Harri","Pa","Se","Ma"};
    public static String[] consanants = new String[]{"b","d","ss","y","t","m","p","ck","rse","mes","ll","rk","trick"};
    public static String[] lastLeaders = new String[]{"Mc","Von","De"};
    public static String[] lastFollowers = new String[]{"son","smith","mer","muel","sums","mberry","din"};

    public static String[] merchantLeader = new String[]{"Duffer","ILM","Big Box","Lucas","Charles","Beef","Chicken","Titan","Giant Media","Runner"};
    public static String[] merchantFollower = new String[]{"Industries","Productions","Department","Warehouse","Company","Enterprises","Organization","and Sons","Group","Games"};
    public String firstName()
    {
        return randomFrom(firstSyllables) + randomFrom(consanants);
    }

    public String generatePassword()
    {
            String returner = "";
            String charsAlp = "abcdefghijklmnopqrstuvwxyzABCDEFGHIJKLMNOPQRSTUVWXYZ";
            String charsNum = "1234567890";
            String charsSpec = "!@#$%^&*(){}[]?/><.,";

            for (int i = 0; i < 4; i++) {
                returner += randomFromString(charsAlp);
            }
            returner += randomFromString(charsNum);
            returner += randomFromString(charsSpec);
            for (int i = 0; i < 4; i++) {
                returner += randomFromString(charsAlp);
            }


        return returner;
    }

    private String randomFromString(String giver)
    {
        int index = (int)(Math.random()*giver.length());
        return giver.substring(index, index+1);
    }

    public String phone()
    {
        String charsNum = "1234567890";
        String phone = "";
        for (int i = 0; i < 3; i++) {
            phone += randomFromString(charsNum);
        }
        phone += "-";
        for (int i = 0; i < 3; i++) {
            phone += randomFromString(charsNum);
        }
        phone += "-";
        for (int i = 0; i < 4; i++) {
            phone += randomFromString(charsNum);
        }
        return phone;
    }

    public String lastName()
    {
        String n = "";
        if (Math.random()* 100 < 10)
        {
            n += randomFrom(lastLeaders);
        }
        n += firstName();
        if (Math.random()* 100 < 10)
        {
            n += randomFrom(lastFollowers);
        }
        return n;
    }

    private String randomFrom(String[] list)
    {
        int len = list.length;
        int index = (int)Math.floor(Math.random()*len);
        return list[index];
    }

    public String cardNumber()
    {
        return "" + LuhnAlgorithms.generateRandomLuhn(16);
    }

    public String cvv() {
        String valid = "0123456789";
        String ret = "";
        for (int i = 0; i < 3; i ++)
        {
            ret += randomFrom(valid.split(""));
        }
        return ret;
    }

    public String merchantName()
    {
        return randomFrom(merchantLeader) + " " + randomFrom(merchantFollower);
    }

    public String expiration(boolean valid)
    {
        LocalDate when = LocalDate.now();
        String year = "";
        String month = "";
        year = "" + when.getYear();
        year = year.substring(2,4);
        month = "" + when.getMonthValue();
        int newYear;
        int newMonth;
        if (valid)
        {
            newYear = (Integer.valueOf(year) + (int)(Math.random()*5)) % 100;
            newMonth = (Integer.valueOf(month) + (int)(Math.random()*12)) % 12 + 1;
        } else {
            newYear = (Integer.valueOf(year) - (int)(Math.random()*5)) % 100;
            newMonth = (Integer.valueOf(month) - (int)(Math.random()*12)) % 12 + 1;
        }
        year = "" + newYear;
        month = "" + newMonth;
        if (month.length() == 1) month = "0" + month;
        String ans = month+"/"+year;
        return ans;
    }
}
