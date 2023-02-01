package com.withyuu.qatraining;

import io.github.bonigarcia.wdm.WebDriverManager;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;

import java.time.Duration;

import static org.assertj.core.api.Assertions.assertThat;

public class AddItemToCartTest {
    private WebDriver driver;

    @BeforeAll
    static void setupAll() {
        WebDriverManager.chromedriver().setup();
    }

    @BeforeEach
    void setup() {
        setUpDriver();
        goToJuiceShopHome();
    }

    @AfterEach
    void teardown() {
        driver.quit();
    }

    @Test
    void testAddItemToCart() throws Exception {
        login("test01@withyuu.com", "password01");
        addItemToBasket("Apple Juice (1000ml)");
        assertThat(getNumberOfItemsInBasket()).isEqualTo(1);

        goToBasket();
        removeItemFromBasket();
    }

    private void setUpDriver() {
        driver = new ChromeDriver();
        driver.manage().timeouts().implicitlyWait(Duration.ofSeconds(10));
    }

    private void goToJuiceShopHome() {
        driver.get("http://localhost:3000/");
        driver.findElement(By.cssSelector("[aria-label=\"Close Welcome Banner\"]")).click();
        assertThat(driver.getTitle()).contains("OWASP Juice Shop");
    }

    private void login(String email, String password) {
        driver.findElement(By.id("navbarAccount")).click();
        driver.findElement(By.id("navbarLoginButton")).click();
        driver.findElement(By.id("email")).sendKeys(email);
        driver.findElement(By.id("password")).sendKeys(password);
        driver.findElement(By.id("loginButton")).click();
    }

    private void addItemToBasket(String item) {
        WebElement itemCard = driver.findElement(
                By.xpath(
                        String.format("//div[contains(@class, 'mat-grid-tile-content') and .//div[contains(text(),'%s')]]", item)
                ));
        WebElement addToCartButton = itemCard.findElement(By.tagName("button"));

        WebElement itemCountBadge = driver.findElement(By.cssSelector("span.warn-notification"));
        String currentValue = itemCountBadge.getText();
        addToCartButton.click();

        WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(2));
        wait.until(ExpectedConditions.not(ExpectedConditions.textToBePresentInElement(itemCountBadge, currentValue)));
    }

    private int getNumberOfItemsInBasket() {
        return Integer.parseInt(driver.findElement(By.cssSelector("span.warn-notification")).getText());
    }

    private void goToBasket() {
        driver.findElement(By.xpath("//button[.//span[contains(text(), 'Your Basket')]]")).click();
    }

    private void removeItemFromBasket() {
        driver.findElement(By.cssSelector("button > span > svg[data-icon=trash-alt]")).click();
    }


}
