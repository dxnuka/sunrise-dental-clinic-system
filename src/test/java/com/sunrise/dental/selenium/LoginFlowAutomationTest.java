package com.sunrise.dental.selenium;

import org.junit.jupiter.api.*;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeOptions;
import org.openqa.selenium.support.ui.Select;

import static org.junit.jupiter.api.Assertions.assertTrue;

/**
 * ---------------------------------------------------------------------------
 * END-TO-END UI TEST AUTOMATION (Selenium WebDriver)
 * ---------------------------------------------------------------------------
 * Drives a REAL Chrome browser against a REAL running deployment, proving
 * the full stack (JSP -> Servlet -> Service -> DAO -> MySQL) works together.
 * Unlike AppointmentServiceTest/BillingServiceTest, this needs MySQL +
 * Tomcat running and Chrome installed - see README.md "Running the Selenium
 * test". Excluded from a plain `mvn test` run; run explicitly with:
 *   mvn test -Dtest=LoginFlowAutomationTest
 * ---------------------------------------------------------------------------
 */
class LoginFlowAutomationTest {

    private static final String BASE_URL = "http://localhost:8080/dental-clinic-system";
    private WebDriver driver;

    @BeforeEach
    void startBrowser() {
        ChromeOptions options = new ChromeOptions();
        options.addArguments("--headless=new");
        driver = new ChromeDriver(options);
    }

    @AfterEach
    void closeBrowser() {
        if (driver != null) driver.quit();
    }

    private void login(String username, String password) {
        driver.get(BASE_URL + "/index.jsp");
        driver.findElement(By.name("username")).sendKeys(username);
        driver.findElement(By.name("password")).sendKeys(password);
        driver.findElement(By.cssSelector("button[type=submit]")).click();
    }

    @Test
    @DisplayName("A staff member can log in and reach the appointments dashboard")
    void validLogin_reachesDashboard() {
        login("admin", "Admin@123");

        WebElement heading = driver.findElement(By.cssSelector(".card h2"));
        assertTrue(heading.getText().contains("All Appointments"),
                "Expected to land on the 'All Appointments' dashboard after login.");
    }

    @Test
    @DisplayName("An invalid password shows the error message and does not log in")
    void invalidLogin_showsError() {
        login("admin", "wrong-password");

        WebElement alert = driver.findElement(By.cssSelector(".alert-error"));
        assertTrue(alert.getText().toLowerCase().contains("invalid"));
    }

    @Test
    @DisplayName("Registering a new-patient appointment end-to-end shows a success message with an appointment number")
    void registerNewPatientAppointment_endToEnd_showsSuccess() {
        login("admin", "Admin@123");
        driver.get(BASE_URL + "/control?action=addAppointmentPage");

        // "New Patient" mode is the default, so no toggle click needed.
        driver.findElement(By.id("patientNameInput")).sendKeys("Selenium Test Patient");
        driver.findElement(By.id("addressInput")).sendKeys("1 Automation Ave, Colombo");
        driver.findElement(By.id("contactInput")).sendKeys("0779998888");

        new Select(driver.findElement(By.name("dentistId"))).selectByIndex(1);
        new Select(driver.findElement(By.name("treatmentId"))).selectByIndex(1);

        driver.findElement(By.id("appointmentDate")).sendKeys("12/31/2026");
        new Select(driver.findElement(By.id("appointmentTime"))).selectByVisibleText("10:00");

        driver.findElement(By.cssSelector("#appointmentForm button[type=submit]")).click();

        WebElement alert = driver.findElement(By.cssSelector(".alert-success"));
        assertTrue(alert.getText().contains("Appointment number"));
    }

    @Test
    @DisplayName("The existing-patient search box finds and selects a previously registered patient")
    void existingPatientSearch_selectsPatient() throws InterruptedException {
        login("admin", "Admin@123");
        driver.get(BASE_URL + "/control?action=addAppointmentPage");

        driver.findElement(By.id("modeExisting")).click();
        WebElement searchBox = driver.findElement(By.id("patientSearchBox"));
        searchBox.sendKeys("Selenium"); // matches the patient created in the previous test
        Thread.sleep(600); // allow the debounced fetch() call to resolve

        WebElement firstSuggestion = driver.findElement(By.cssSelector(".suggest-item"));
        firstSuggestion.click();

        WebElement chip = driver.findElement(By.id("selectedPatientChip"));
        assertTrue(chip.isDisplayed(), "Expected the selected-patient chip to be visible after choosing a search result.");
    }
}
