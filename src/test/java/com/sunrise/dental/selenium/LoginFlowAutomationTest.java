package com.sunrise.dental.selenium;

import org.junit.jupiter.api.*;
import org.openqa.selenium.By;
import org.openqa.selenium.Keys;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeOptions;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.Select;
import org.openqa.selenium.support.ui.WebDriverWait;

import java.time.Duration;

import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.junit.jupiter.api.Assertions.fail;


@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
class LoginFlowAutomationTest {

    private static final String BASE_URL = "http://localhost:8080/dental-clinic-system";
    private WebDriver driver;
    private WebDriverWait wait;

    @BeforeEach
    void startBrowser() {
        ChromeOptions options = new ChromeOptions();
        options.addArguments("--headless=new");
        driver = new ChromeDriver(options);
        wait = new WebDriverWait(driver, Duration.ofSeconds(10));
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
    @Order(1)
    @DisplayName("A staff member can log in and reach the appointments dashboard")
    void validLogin_reachesDashboard() {
        login("admin", "Admin@123");

        WebElement heading = driver.findElement(By.cssSelector(".card h2"));
        assertTrue(heading.getText().contains("All Appointments"),
                "Expected to land on the 'All Appointments' dashboard after login.");
    }

    @Test
    @Order(2)
    @DisplayName("An invalid password shows the error message and does not log in")
    void invalidLogin_showsError() {
        login("admin", "wrong-password");

        WebElement alert = driver.findElement(By.cssSelector(".alert-error"));
        assertTrue(alert.getText().toLowerCase().contains("invalid"));
    }

    private void assertOnAddAppointmentPage() {
        boolean onFormPage = !driver.findElements(By.id("appointmentForm")).isEmpty();
        if (!onFormPage) {
            String serverMessage = driver.findElements(By.cssSelector(".alert-error")).stream()
                    .findFirst().map(WebElement::getText).orElse("(no .alert-error message found)");
            fail("Expected the Add Appointment form at " + driver.getCurrentUrl() +
                    " but it wasn't there. Page heading: '" +
                    driver.findElements(By.cssSelector(".card h2")).stream().findFirst()
                            .map(WebElement::getText).orElse("<none>") +
                    "'. Server message: " + serverMessage);
        }
    }

    @Test
    @Order(3)
    @DisplayName("Registering a new-patient appointment end-to-end shows a success message with an appointment number")
    void registerNewPatientAppointment_endToEnd_showsSuccess() {
        login("admin", "Admin@123");
        driver.get(BASE_URL + "/control?action=addAppointmentPage");
        assertOnAddAppointmentPage();

        driver.findElement(By.id("patientNameInput")).sendKeys("Selenium Test Patient");
        driver.findElement(By.id("addressInput")).sendKeys("1 Automation Ave, Colombo");
        driver.findElement(By.id("contactInput")).sendKeys("0779998888");
        driver.findElement(By.id("patientBirthYearInput")).sendKeys("1990");
        new Select(driver.findElement(By.id("patientGenderInput"))).selectByValue("MALE");

        new Select(driver.findElement(By.name("dentistId"))).selectByIndex(1);
        new Select(driver.findElement(By.name("treatmentId"))).selectByIndex(1);

        WebElement dateInput = driver.findElement(By.id("appointmentDate"));
        dateInput.sendKeys("12/31/2026");
        dateInput.sendKeys(Keys.TAB);
        selectFirstAvailableTimeSlot();

        driver.findElement(By.cssSelector("#appointmentForm button[type=submit]")).click();

        WebElement alert = wait.until(ExpectedConditions.visibilityOfElementLocated(By.cssSelector(".alert-success")));
        assertTrue(alert.getText().contains("Appointment number"));
    }

    private void selectFirstAvailableTimeSlot() {
        WebElement timeSelectEl = wait.until(d ->
                d.findElement(By.id("appointmentTime")).isEnabled()
                        ? d.findElement(By.id("appointmentTime"))
                        : null);

        Select timeSelect = new Select(timeSelectEl);
        for (WebElement option : timeSelect.getOptions()) {
            String value = option.getAttribute("value");
            if (!value.isEmpty() && option.isEnabled()) {
                timeSelect.selectByValue(value);
                return;
            }
        }
        fail("No available appointment time slots were returned for the chosen dentist/treatment/date.");
    }

    @Test
    @Order(4)
    @DisplayName("The existing-patient search box finds and selects a previously registered patient")
    void existingPatientSearch_selectsPatient() {
        login("admin", "Admin@123");
        driver.get(BASE_URL + "/control?action=addAppointmentPage");
        assertOnAddAppointmentPage();

        driver.findElement(By.cssSelector("label[for='modeExisting']")).click();

        WebElement searchBox = driver.findElement(By.id("patientSearchBox"));
        searchBox.sendKeys("Selenium");
        WebElement firstSuggestion = wait.until(
                ExpectedConditions.presenceOfElementLocated(By.cssSelector(".suggest-item")));
        firstSuggestion.click();

        WebElement chip = wait.until(ExpectedConditions.visibilityOfElementLocated(By.id("selectedPatientChip")));
        assertTrue(chip.isDisplayed(), "Expected the selected-patient chip to be visible after choosing a search result.");
    }
}