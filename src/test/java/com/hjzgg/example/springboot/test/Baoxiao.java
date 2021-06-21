package com.hjzgg.example.springboot.test;

import java.time.*;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

/**
 * @author hujunzheng
 * @create 2019-03-26 13:58
 **/
public class Baoxiao {
    public static void main(String[] args) {
        //cal(2400, 13, 1, 2, 3);
        cal(1200, 14, 4);
        cal(1200, 14, 5);
        cal(1200, 14, 6);
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
            int x = 1 + random.nextInt(5);
            int i1 = random.nextInt(money.length);
            int i2 = random.nextInt(money.length);
            if (money[i1] + x < 150) {
                money[i1] += x;
                money[i2] -= x;
            } else if (money[i2] + x < 150) {
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
            List<LocalDate> workDays = new ArrayList<>();
            for (LocalDate beginDate = LocalDate.of(year, months[i], 1), endDate = YearMonth.of(year, months[i]).atEndOfMonth(); endDate.compareTo(beginDate) >= 0; beginDate = beginDate.plusDays(1)) {
                if (beginDate.getDayOfWeek() == DayOfWeek.SUNDAY || beginDate.getDayOfWeek() == DayOfWeek.SATURDAY) {
                    continue;
                }
                workDays.add(beginDate);
            }
            while (workDays.size() > md[i]) {
                workDays.remove(random.nextInt(workDays.size()));
            }
            for (int j = 0; j < md[i]; ++j, ++k) {
                LocalTime time = LocalTime.of(random.nextInt(24), random.nextInt(60), random.nextInt(60));
                LocalDateTime dateTime = LocalDateTime.of(workDays.get(j), time);
                System.out.println(dateTime.format(formatter) + "   " + money[k]);
            }
        }
    }
}