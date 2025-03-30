package com.banson.healthtagram.controller;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.RequiredArgsConstructor;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeOptions;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.time.Duration;
import java.util.Arrays;
import java.util.List;

@RequestMapping("/api")
@RequiredArgsConstructor
@RestController
public class CrawlingController {
    @GetMapping("/crawling")
    public ResponseEntity crawling() throws InterruptedException {
        final String WEB_DRIVER_ID = "webdriver.chrome.driver";
        final String WEB_DRIVER_PATH = "E:\\chromedriver-win64\\chromedriver-win64\\chromedriver.exe";

        try {
            System.setProperty(WEB_DRIVER_ID, WEB_DRIVER_PATH);
        } catch (Exception e) {
            e.printStackTrace();
        }

        ChromeOptions options = new ChromeOptions(); // 크롬 설정을 담은 객체 생성

        WebDriver driver = new ChromeDriver(options);

        driver.get("https://weather.naver.com/");

        WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(10));  // 최대 10초 대기
        WebElement weather = wait.until(ExpectedConditions.visibilityOfElementLocated(By.cssSelector("#hourly > div.weather_graph > div > div > div > table > thead > tr")));

        String[] time = weather.getText().split("\n");

        return ResponseEntity.ok(time);
    }
}
