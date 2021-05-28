package com.example.demo.e2e;


import com.example.demo.DemoApplication;
import lombok.AllArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.junit.Before;
import org.junit.Ignore;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.openqa.selenium.By;
import org.openqa.selenium.JavascriptExecutor;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.interactions.Actions;
import org.openqa.selenium.phantomjs.PhantomJSDriver;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.context.annotation.Bean;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.event.annotation.BeforeTestClass;

import java.io.File;
import java.util.Optional;
import java.util.Properties;
import java.util.concurrent.TimeUnit;

import static org.junit.jupiter.api.Assertions.assertTrue;

@Log4j2
public class UITests {



    @Test
    public void phantomDriver() throws Exception {
        uiTest(new PhantomJSDriver());
    }

    //    @Ignore
//    @Test
    public void chromeDriver() throws Exception {
        String chromeDriverPath = (new File("src/test/resources").getAbsolutePath() + "\\chromedriver.exe");
        System.setProperty("webdriver.chrome.driver", chromeDriverPath);
        uiTest(new ChromeDriver());
    }

    private void uiTest(WebDriver driver) throws Exception {
        String[] args1 = new String[0];
        ConfigurableApplicationContext ctx = SpringApplication.run(DemoApplication.class, args1);

        Properties appProperties = (Properties)ctx.getBean("appProperties");
        String host = Optional.ofNullable(appProperties.getProperty("uitest.host"))
                .orElseGet(() -> {
                    log.error("please set env variable uitest.host using localhost:8080");
                    return "http://localhost:8080";
                });
        String recipientMail = Optional.ofNullable(appProperties.getProperty("uitest.mail"))
                .orElseGet(() -> {
                    log.error("please set env variable uitest.mail, currently use default email");
                    return "ms_jack@inbox.ru";
                });

        JavascriptExecutor js = (JavascriptExecutor) driver;
        driver.get(host);
        Thread.sleep(15000);
        driver.manage().timeouts().implicitlyWait(3, TimeUnit.SECONDS);
        WebElement secondRecord = driver.findElement(By.xpath("//table/tbody/tr[2]"));
        secondRecord.click();
        Thread.sleep(1000);
        WebElement commentsToReport = driver.findElement(By.id("messageId"));
        commentsToReport.click();
        commentsToReport.sendKeys("some details for report");
        WebElement emailAddress = driver.findElement(By.id("exampleInputEmail"));
        emailAddress.click();
        emailAddress.sendKeys(recipientMail);
        Thread.sleep(1000);
        js.executeScript("window.scrollBy(0,250)", "");
        Thread.sleep(1000);
        WebElement sendReportButton = driver.findElement(By.id("sendReport"));
        Actions actions = new Actions(driver);
        actions.moveToElement(sendReportButton).perform();
        sendReportButton.click();
        Thread.sleep(10000);
        assertTrue(sendReportButton.getAttribute("class").contains("btn-success"));
        Thread.sleep(5000);
        ctx.close();
        driver.quit();
    }

}
