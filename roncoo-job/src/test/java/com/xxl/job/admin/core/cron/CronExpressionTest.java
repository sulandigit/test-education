package com.xxl.job.admin.core.cron;

import org.junit.jupiter.api.Test;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.TimeZone;

import static org.junit.jupiter.api.Assertions.*;

/**
 * CronExpression 类单元测试
 *
 * @author Test Generator
 */
public class CronExpressionTest {

    private final SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");

    @Test
    void testConstructor_validExpression() throws ParseException {
        String cronExpression = "0 0 12 * * ?";
        CronExpression cron = new CronExpression(cronExpression);
        
        assertNotNull(cron);
        assertEquals(cronExpression.toUpperCase(), cron.getCronExpression());
    }

    @Test
    void testConstructor_nullExpression() {
        assertThrows(IllegalArgumentException.class, () -> {
            new CronExpression(null);
        });
    }

    @Test
    void testConstructor_invalidExpression() {
        assertThrows(ParseException.class, () -> {
            new CronExpression("invalid expression");
        });
    }

    @Test
    void testCopyConstructor() throws ParseException {
        CronExpression original = new CronExpression("0 0 12 * * ?");
        CronExpression copy = new CronExpression(original);
        
        assertNotNull(copy);
        assertEquals(original.getCronExpression(), copy.getCronExpression());
        assertNotSame(original, copy);
    }

    @Test
    void testIsValidExpression_validExpressions() {
        assertTrue(CronExpression.isValidExpression("0 0 12 * * ?"));
        assertTrue(CronExpression.isValidExpression("0 15 10 ? * *"));
        assertTrue(CronExpression.isValidExpression("0 15 10 * * ?"));
        assertTrue(CronExpression.isValidExpression("0 15 10 * * ? *"));
        assertTrue(CronExpression.isValidExpression("0 */5 * * * ?"));
        assertTrue(CronExpression.isValidExpression("0 0/30 8-10 5,20 * ?"));
    }

    @Test
    void testIsValidExpression_invalidExpressions() {
        assertFalse(CronExpression.isValidExpression(""));
        assertFalse(CronExpression.isValidExpression("invalid"));
        assertFalse(CronExpression.isValidExpression("0 0 12"));
        assertFalse(CronExpression.isValidExpression("60 0 12 * * ?"));
        assertFalse(CronExpression.isValidExpression("0 60 12 * * ?"));
        assertFalse(CronExpression.isValidExpression("0 0 25 * * ?"));
    }

    @Test
    void testValidateExpression_validExpression() {
        assertDoesNotThrow(() -> {
            CronExpression.validateExpression("0 0 12 * * ?");
        });
    }

    @Test
    void testValidateExpression_invalidExpression() {
        assertThrows(ParseException.class, () -> {
            CronExpression.validateExpression("invalid expression");
        });
    }

    @Test
    void testIsSatisfiedBy_dailyAtNoon() throws ParseException {
        CronExpression cron = new CronExpression("0 0 12 * * ?");
        
        Calendar cal = Calendar.getInstance();
        cal.set(2023, Calendar.JANUARY, 1, 12, 0, 0);
        cal.set(Calendar.MILLISECOND, 0);
        
        assertTrue(cron.isSatisfiedBy(cal.getTime()));
        
        cal.set(Calendar.HOUR_OF_DAY, 13);
        assertFalse(cron.isSatisfiedBy(cal.getTime()));
    }

    @Test
    void testIsSatisfiedBy_everyMinute() throws ParseException {
        CronExpression cron = new CronExpression("0 * * * * ?");
        
        Calendar cal = Calendar.getInstance();
        cal.set(Calendar.SECOND, 0);
        cal.set(Calendar.MILLISECOND, 0);
        
        assertTrue(cron.isSatisfiedBy(cal.getTime()));
        
        cal.set(Calendar.SECOND, 30);
        assertFalse(cron.isSatisfiedBy(cal.getTime()));
    }

    @Test
    void testGetNextValidTimeAfter_dailyAtNoon() throws ParseException {
        CronExpression cron = new CronExpression("0 0 12 * * ?");
        
        Calendar cal = Calendar.getInstance();
        cal.set(2023, Calendar.JANUARY, 1, 10, 0, 0);
        cal.set(Calendar.MILLISECOND, 0);
        
        Date nextTime = cron.getNextValidTimeAfter(cal.getTime());
        assertNotNull(nextTime);
        
        Calendar nextCal = Calendar.getInstance();
        nextCal.setTime(nextTime);
        assertEquals(12, nextCal.get(Calendar.HOUR_OF_DAY));
        assertEquals(0, nextCal.get(Calendar.MINUTE));
        assertEquals(0, nextCal.get(Calendar.SECOND));
    }

    @Test
    void testGetNextValidTimeAfter_afterTriggerTime() throws ParseException {
        CronExpression cron = new CronExpression("0 0 12 * * ?");
        
        Calendar cal = Calendar.getInstance();
        cal.set(2023, Calendar.JANUARY, 1, 14, 0, 0);
        cal.set(Calendar.MILLISECOND, 0);
        
        Date nextTime = cron.getNextValidTimeAfter(cal.getTime());
        assertNotNull(nextTime);
        
        Calendar nextCal = Calendar.getInstance();
        nextCal.setTime(nextTime);
        assertEquals(2, nextCal.get(Calendar.DAY_OF_MONTH)); // 下一天
        assertEquals(12, nextCal.get(Calendar.HOUR_OF_DAY));
    }

    @Test
    void testGetNextValidTimeAfter_everyFiveMinutes() throws ParseException {
        CronExpression cron = new CronExpression("0 */5 * * * ?");
        
        Calendar cal = Calendar.getInstance();
        cal.set(Calendar.MINUTE, 3);
        cal.set(Calendar.SECOND, 0);
        cal.set(Calendar.MILLISECOND, 0);
        
        Date nextTime = cron.getNextValidTimeAfter(cal.getTime());
        assertNotNull(nextTime);
        
        Calendar nextCal = Calendar.getInstance();
        nextCal.setTime(nextTime);
        assertEquals(5, nextCal.get(Calendar.MINUTE));
    }

    @Test
    void testGetTimeZone_default() throws ParseException {
        CronExpression cron = new CronExpression("0 0 12 * * ?");
        TimeZone timeZone = cron.getTimeZone();
        
        assertNotNull(timeZone);
        assertEquals(TimeZone.getDefault(), timeZone);
    }

    @Test
    void testSetTimeZone() throws ParseException {
        CronExpression cron = new CronExpression("0 0 12 * * ?");
        TimeZone utc = TimeZone.getTimeZone("UTC");
        
        cron.setTimeZone(utc);
        assertEquals(utc, cron.getTimeZone());
    }

    @Test
    void testToString() throws ParseException {
        String cronExpression = "0 0 12 * * ?";
        CronExpression cron = new CronExpression(cronExpression);
        
        assertEquals(cronExpression.toUpperCase(), cron.toString());
    }

    @Test
    void testGetCronExpression() throws ParseException {
        String cronExpression = "0 0 12 * * ?";
        CronExpression cron = new CronExpression(cronExpression);
        
        assertEquals(cronExpression.toUpperCase(), cron.getCronExpression());
    }

    @Test
    void testGetExpressionSummary() throws ParseException {
        CronExpression cron = new CronExpression("0 0 12 * * ?");
        String summary = cron.getExpressionSummary();
        
        assertNotNull(summary);
        assertFalse(summary.isEmpty());
        assertTrue(summary.contains("seconds"));
        assertTrue(summary.contains("minutes"));
        assertTrue(summary.contains("hours"));
    }

    @Test
    void testWeeklyExpression() throws ParseException {
        // 每周一上午9点
        CronExpression cron = new CronExpression("0 0 9 ? * MON");
        
        Calendar cal = Calendar.getInstance();
        cal.set(2023, Calendar.JANUARY, 2, 9, 0, 0); // 2023-01-02是周一
        cal.set(Calendar.MILLISECOND, 0);
        
        assertTrue(cron.isSatisfiedBy(cal.getTime()));
        
        // 测试周二
        cal.set(Calendar.DAY_OF_MONTH, 3);
        assertFalse(cron.isSatisfiedBy(cal.getTime()));
    }

    @Test
    void testMonthlyExpression() throws ParseException {
        // 每月1号上午8点
        CronExpression cron = new CronExpression("0 0 8 1 * ?");
        
        Calendar cal = Calendar.getInstance();
        cal.set(2023, Calendar.JANUARY, 1, 8, 0, 0);
        cal.set(Calendar.MILLISECOND, 0);
        
        assertTrue(cron.isSatisfiedBy(cal.getTime()));
        
        // 测试月中其他日期
        cal.set(Calendar.DAY_OF_MONTH, 15);
        assertFalse(cron.isSatisfiedBy(cal.getTime()));
    }

    @Test
    void testRangeExpression() throws ParseException {
        // 工作日上午9-17点，每小时执行
        CronExpression cron = new CronExpression("0 0 9-17 ? * MON-FRI");
        
        Calendar cal = Calendar.getInstance();
        cal.set(2023, Calendar.JANUARY, 2, 10, 0, 0); // 2023-01-02是周一
        cal.set(Calendar.MILLISECOND, 0);
        
        assertTrue(cron.isSatisfiedBy(cal.getTime()));
        
        // 测试周末
        cal.set(Calendar.DAY_OF_MONTH, 7); // 2023-01-07是周六
        assertFalse(cron.isSatisfiedBy(cal.getTime()));
    }

    @Test
    void testStepExpression() throws ParseException {
        // 每10分钟执行一次
        CronExpression cron = new CronExpression("0 */10 * * * ?");
        
        Calendar cal = Calendar.getInstance();
        cal.set(Calendar.MINUTE, 0);
        cal.set(Calendar.SECOND, 0);
        cal.set(Calendar.MILLISECOND, 0);
        
        assertTrue(cron.isSatisfiedBy(cal.getTime()));
        
        cal.set(Calendar.MINUTE, 10);
        assertTrue(cron.isSatisfiedBy(cal.getTime()));
        
        cal.set(Calendar.MINUTE, 20);
        assertTrue(cron.isSatisfiedBy(cal.getTime()));
        
        cal.set(Calendar.MINUTE, 15);
        assertFalse(cron.isSatisfiedBy(cal.getTime()));
    }

    @Test
    void testListExpression() throws ParseException {
        // 在1,15,30号的上午9点执行
        CronExpression cron = new CronExpression("0 0 9 1,15,30 * ?");
        
        Calendar cal = Calendar.getInstance();
        cal.set(2023, Calendar.JANUARY, 1, 9, 0, 0);
        cal.set(Calendar.MILLISECOND, 0);
        
        assertTrue(cron.isSatisfiedBy(cal.getTime()));
        
        cal.set(Calendar.DAY_OF_MONTH, 15);
        assertTrue(cron.isSatisfiedBy(cal.getTime()));
        
        cal.set(Calendar.DAY_OF_MONTH, 30);
        assertTrue(cron.isSatisfiedBy(cal.getTime()));
        
        cal.set(Calendar.DAY_OF_MONTH, 10);
        assertFalse(cron.isSatisfiedBy(cal.getTime()));
    }

    @Test
    void testGetNextInvalidTimeAfter() throws ParseException {
        // 每分钟执行
        CronExpression cron = new CronExpression("0 * * * * ?");
        
        Calendar cal = Calendar.getInstance();
        cal.set(Calendar.SECOND, 0);
        cal.set(Calendar.MILLISECOND, 0);
        
        Date invalidTime = cron.getNextInvalidTimeAfter(cal.getTime());
        assertNotNull(invalidTime);
        
        Calendar invalidCal = Calendar.getInstance();
        invalidCal.setTime(invalidTime);
        assertNotEquals(0, invalidCal.get(Calendar.SECOND));
    }

    @Test
    void testCronExpressionWithYear() throws ParseException {
        // 2023年每天中午12点
        CronExpression cron = new CronExpression("0 0 12 * * ? 2023");
        
        Calendar cal = Calendar.getInstance();
        cal.set(2023, Calendar.JANUARY, 1, 12, 0, 0);
        cal.set(Calendar.MILLISECOND, 0);
        
        assertTrue(cron.isSatisfiedBy(cal.getTime()));
        
        // 测试其他年份
        cal.set(Calendar.YEAR, 2024);
        assertFalse(cron.isSatisfiedBy(cal.getTime()));
    }

    @Test
    void testComplexExpression() throws ParseException {
        // 工作日上午9点到下午5点，每30分钟执行一次
        CronExpression cron = new CronExpression("0 0/30 9-17 ? * MON-FRI");
        
        Calendar cal = Calendar.getInstance();
        cal.set(2023, Calendar.JANUARY, 2, 9, 0, 0); // 周一上午9点
        cal.set(Calendar.MILLISECOND, 0);
        
        assertTrue(cron.isSatisfiedBy(cal.getTime()));
        
        cal.set(Calendar.MINUTE, 30); // 9:30
        assertTrue(cron.isSatisfiedBy(cal.getTime()));
        
        cal.set(Calendar.HOUR_OF_DAY, 17); // 17:30
        assertTrue(cron.isSatisfiedBy(cal.getTime()));
        
        cal.set(Calendar.HOUR_OF_DAY, 18); // 18:30，超出范围
        assertFalse(cron.isSatisfiedBy(cal.getTime()));
    }
}