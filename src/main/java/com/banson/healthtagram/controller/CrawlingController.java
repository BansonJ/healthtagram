package com.banson.healthtagram.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.openqa.selenium.By;
import org.openqa.selenium.Keys;
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
import java.util.List;
import java.util.Set;

@RequestMapping("/api")
@RequiredArgsConstructor
@RestController
@Slf4j
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

    @GetMapping("/interpark")
    public ResponseEntity interpark() throws InterruptedException {
        final String WEB_DRIVER_ID = "webdriver.chrome.driver";
        final String WEB_DRIVER_PATH = "E:\\chromedriver-win64\\chromedriver-win64\\chromedriver.exe";

        try {
            System.setProperty(WEB_DRIVER_ID, WEB_DRIVER_PATH);
        } catch (Exception e) {
            e.printStackTrace();
        }

        ChromeOptions options = new ChromeOptions(); // 크롬 설정을 담은 객체 생성
        options.addArguments("--disable-notifications");
        options.addArguments("--disable-popup-blocking");
        options.addArguments("--disable-blink-features=AutomationControlled");

        WebDriver driver = new ChromeDriver(options);

        driver.get("https://tickets.interpark.com/goods/25003238/");
        Thread.sleep(2000);

        try {
            driver.findElement(By.cssSelector("#popup-prdGuide > div > div.popupFooter > button")).sendKeys(Keys.ENTER);
        } catch (Exception e) {

        }

        //예매하기 클릭
        WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(10));  // 최대 10초 대기
        WebElement date = wait.until(ExpectedConditions.visibilityOfElementLocated(By.cssSelector("#productSide > div > div.sideBtnWrap > a.sideBtn.is-primary")));
        date.sendKeys(Keys.ENTER);

        Thread.sleep(6000);

        //login 페이지
        WebElement id = driver.findElement(By.cssSelector("#login > div.login_loginForm__N3opX > div > div:nth-child(1) > label > input"));
        WebElement password = driver.findElement(By.cssSelector("#login > div.login_loginForm__N3opX > div > div:nth-child(2) > label > input"));

        id.sendKeys("wjdtmdgus313");
        password.sendKeys("nan00700@!");
        driver.findElement(By.cssSelector("#login > div.login_loginButtonBox__C0gty > button")).sendKeys(Keys.ENTER);

        Thread.sleep(1000);

        //예매하기 다시 클릭
        try {
            driver.findElement(By.cssSelector("#popup-prdGuide > div > div.popupFooter > button")).sendKeys(Keys.ENTER);
        } catch (Exception e) {
        }

        WebElement date2 = wait.until(ExpectedConditions.visibilityOfElementLocated(By.cssSelector("#productSide > div > div.sideBtnWrap > a.sideBtn.is-primary")));
        date2.sendKeys(Keys.ENTER);

        Thread.sleep(1000);

        //예매창으로 전환  main > ifrmSeat > ifrmSeatDetail 순으로 되어있다.
        //지금은 main
        Set<String> windowHandles = driver.getWindowHandles();
        for (String window : windowHandles) {
            driver.switchTo().window(window);
        }

        //ifrmSeat 진입 후 잠깐 접어두기
        wait.until(WebDriver::switchTo).frame("ifrmSeat");
        WebElement cap = driver.findElement(By.cssSelector("#divCaptchaFolding > a"));
        cap.sendKeys(Keys.ENTER);

        //자리선택 ifrmSeat > ifrmSeatDetail으로 진입 후 자리 가져오기
        wait.until(WebDriver::switchTo).frame("ifrmSeatDetail");
        List<WebElement> seats = driver.findElements(By.className("stySeat"));
        Thread.sleep(1000);

        for (WebElement seat : seats) {
            try {
                seat.click();
                Thread.sleep(1000);
                //ifrmSeat로 나가기
                driver.switchTo().parentFrame();
                driver.findElement(By.cssSelector("body > form:nth-child(40) > div > div.contWrap > div.seatR > div > div.btnWrap > a")).sendKeys(Keys.ENTER);
                break;
            } catch(Exception e) {
                driver.switchTo().parentFrame();
                wait.until(WebDriver::switchTo).frame("ifrmSeatDetail");
            }
        }

        return ResponseEntity.ok("finish");
    }
}