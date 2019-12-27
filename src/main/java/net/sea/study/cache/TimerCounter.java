package net.sea.study.cache;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * 时间计数器
 *
 * @author huacw
 * @date 2019/12/27
 */
public class TimerCounter extends GuavaCache<String, AtomicInteger> {

    public TimerCounter(long duration, TimeUnit timeUtil) {
        super(duration, timeUtil);
    }

    @Override
    protected AtomicInteger loadData(String key) {
        return new AtomicInteger(0);
    }

    public static void main(String[] args) throws InterruptedException {
        TimerCounter timerCounter = new TimerCounter(1, TimeUnit.MINUTES);
        while (true) {
            AtomicInteger counter = timerCounter.getCache(LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyyMMddHHmm")));
            int i = counter.incrementAndGet();
            System.out.println(LocalDateTime.now() + ":" + i);
            if (i == 10) {
                System.out.println("--------------------------");
                System.out.println(timerCounter.getCache(LocalDateTime.now().minusMinutes(1).format(DateTimeFormatter.ofPattern("yyyyMMddHHmm"))));
                System.out.println(timerCounter.getAllValues() + ":" + timerCounter.getSize());
                System.out.println("--------------------------");
            }
            Thread.sleep(1000);
        }
    }
}
