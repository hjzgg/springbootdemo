package com.hjzgg.example.springboot.test;

import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.Random;

/**
 * @author hujunzheng
 * @create 2019-03-26 13:58
 **/
public class Baoxiao {
    public static void main(String[] args) {
        //cal(2400, 13, 1, 2, 3);
        cal(800, 5, 4);
        cal(800, 5, 5);
        cal(800, 5, 6);
    }

    public static void cal(int amount, int days, int... months) {
        Random random = new Random();
        int[] money = new int[days];
        for (int i = 0; i < money.length; ++i) {
            money[i] = amount / days;
        }
        for (int i = 0; i < amount % days; ++i) {
            money[random.nextInt(money.length)] += 1;
        }
        for (int i = 0; i < money.length; ++i) {
            int x = 1 + random.nextInt(8);
            int i1 = random.nextInt(money.length);
            int i2 = random.nextInt(money.length);
            if (money[i1] + x < 200) {
                money[i1] += x;
                money[i2] -= x;
            } else if (money[i2] + x < 200) {
                money[i1] -= x;
                money[i2] += x;
            }
        }

        int[] md = new int[months.length];
        for (int i = 0; i < md.length; ++i) {
            md[i] = days / md.length;
        }
        for (int i = 0; i < days % md.length; ++i) {
            md[random.nextInt(md.length)] += 1;
        }

        int year = LocalDate.now().getYear();
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
        for (int k = 0, i = 0; i < md.length; ++i) {
            LocalDate date = LocalDate.of(year, months[i], 1);
            for (int j = 0; j < md[i]; ++j, ++k) {
                while (true) {
                    date = date.plusDays(1 + random.nextInt(2));
                    DayOfWeek dayOfWeek = date.getDayOfWeek();
                    if (!(dayOfWeek == DayOfWeek.SUNDAY || dayOfWeek == DayOfWeek.SATURDAY)) {
                        break;
                    }
                }
                LocalTime time = LocalTime.of(random.nextInt(24), random.nextInt(60), random.nextInt(60));
                LocalDateTime dateTime = LocalDateTime.of(date, time);
                System.out.println(dateTime.format(formatter) + "   " + money[k]);
            }
        }
    }
}