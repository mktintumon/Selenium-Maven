package demo;

import com.aventstack.extentreports.ExtentReports;
import com.aventstack.extentreports.ExtentTest;
import com.aventstack.extentreports.reporter.ExtentSparkReporter;
import org.example.ExcelReader;
import org.example.UserData;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.interactions.Actions;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;
import org.testng.Assert;
import org.testng.annotations.AfterClass;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.DataProvider;
import org.testng.annotations.Test;

import java.time.Duration;
import java.util.List;

public class SeleniumDemo{

    private WebDriver driver;
    Actions actions;
    ExtentTest test;
    ExtentReports extent = new ExtentReports();
    ExtentSparkReporter spark = new ExtentSparkReporter(AppConstants.REPORT_PATH);

    @BeforeClass
    public void setup() {
        extent.attachReporter(spark);

        try {
            System.setProperty(AppConstants.DRIVER_NAME, AppConstants.CHROME_DRIVER_LOCATION);
            driver = new ChromeDriver();
        } catch (Exception e) {
            throw new RuntimeException("WebDriver setup failed", e);
        }
    }

    @DataProvider(name = "userData")
    public Object[][] provideUserData() {
        String filePath = AppConstants.EXCEL_SHEET_LOCATION;

        List<UserData> userDataList = ExcelReader.readExcelData(filePath);

        // Convert userDataList to a 2D array
        Object[][] userDataArray = new Object[userDataList.size()][1];
        for (int i = 0; i < userDataList.size(); i++) {
            userDataArray[i][0] = userDataList.get(i);
        }
        return userDataArray;
    }


    @Test(dataProvider = "userData")
    public void WrongEmail(UserData userData) {
        if (!userData.getType().equalsIgnoreCase("email")) {
            return;
        }

        test = extent.createTest("Testing Wrong Email");
        actions = new Actions(driver);
        try {
            login(userData.getUsername(), userData.getPassword(), "Invalid credentials");
            test.info("Toastify displayed");
            test.pass("Wrong Email Verified");
        } catch (Exception e) {
            test.fail("Wrong Email Test failed: " + e.getMessage());
        }

    }


    @Test(dataProvider = "userData")
    public void WrongPassword(UserData userData) {
        if (!userData.getType().equalsIgnoreCase("password")) {
            return;
        }

        test = extent.createTest("Testing Wrong Password");
        actions = new Actions(driver);

        try {
            login(userData.getUsername(), userData.getPassword(), "Invalid credentials");
            test.info("Toastify displayed");
            test.pass("Wrong password verified");
        } catch (Exception e) {
            test.fail("Wrong password Test failed: " + e.getMessage());
        }
    }

    @Test(dataProvider = "userData")
    public void SuccessLogin(UserData userData) {
        if (!userData.getType().equalsIgnoreCase("success")) {
            return;
        }

        test = extent.createTest("Testing Successful Login");
        actions = new Actions(driver);
        try {
            login(userData.getUsername(), userData.getPassword(), "Login Successful");
            openUserSection();
            createUser();
            deleteUser();

            test.info("Successful Login done");
            test.pass("Login passed");
        } catch (Exception e) {
            test.fail("Success Login failed: " + e.getMessage());
        }
    }

    @AfterClass
    public void teardown() {
        extent.flush();
        try {
            if (driver != null) {
                driver.quit();
            }
        } catch (Exception e) {
            System.out.println("Error during WebDriver teardown: {}" + e.getMessage());
        }
    }

    private void login(String email, String password, String message) {
        try {
            driver.get(AppConstants.WEBSITE_NAME);
            driver.manage().window().maximize();
            // driver.manage().timeouts().implicitlyWait(Duration.ofSeconds(5));

            WebElement emailInput = driver.findElement(By.id("email"));
            slowType(emailInput, email);

            WebElement passwordInput = driver.findElement(By.id("password"));
            slowType(passwordInput, password);

            WebElement loginButton = driver.findElement(By.xpath("//button[contains(@class, 'btn-primary')]"));
            loginButton.click();
            driver.manage().timeouts().implicitlyWait(Duration.ofSeconds(30));

            testToastMessage(message);
        } catch (Exception e) {
            System.out.println("Login failed: {}" + e.getMessage());
            throw new RuntimeException("Login failed", e);
        }
    }

    public void testToastMessage(String assertValue) {
        try {
            WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(5));

            WebElement toastify = wait.until(ExpectedConditions.presenceOfElementLocated(By.cssSelector(".Toastify__toast-body > div:nth-child(2)")));

            // Get the text content of the div
            String toastifyContent = toastify.getText();

            // Print or use the content as needed
            System.out.println("Content inside the Toastify div: " + toastifyContent);

            // Check if the content is "Invalid credentials" using assertion
            Assert.assertEquals(toastifyContent, assertValue, "Test case failed: Unexpected toastify message");
        } catch (Exception e) {
            System.out.println("Error during testing toast message: {}" + e.getMessage());
            throw new RuntimeException("Toast message test failed", e);
        }
    }

    private void openUserSection() {
        try {
            WebElement openBar = driver.findElement(By.xpath("//*[@id='root']/div[2]/div[3]/div[1]/div/button"));
            openBar.click();

            WebElement usersBar = driver.findElement(By.xpath("//*[@id='root']/div[2]/div[3]/div[2]/a[2]/div"));
            usersBar.click();
        } catch (Exception e) {
            System.out.println("Error opening user section: { }" + e.getMessage());
            throw new RuntimeException("Error opening user section", e);
        }
    }

    private void createUser() {
        try {
            WebElement clickCreateUser = driver.findElement(By.xpath("//*[@id='root']/div[2]/div[4]/div[1]/div[2]/div/div[1]/div[1]/div[2]/div/button"));
            clickCreateUser.click();

            WebElement clickNewUser = driver.findElement(By.xpath("//*[@id='root']/div[2]/div[4]/div[1]/div[2]/div/div[1]/div[1]/div[2]/div/div/div[1]"));
            clickNewUser.click();

            WebElement usernameInput = driver.findElement(By.xpath("//*[@id='root']/div[2]/div[4]/div[1]/div[2]/div/div[2]/div/form/div/div[2]/div/div[1]/div[1]/div/input"));
            slowType(usernameInput, "Akshay Solanki");

            WebElement userEmail = driver.findElement(By.xpath("//*[@id='root']/div[2]/div[4]/div[1]/div[2]/div/div[2]/div/form/div/div[2]/div/div[1]/div[2]/div/input"));
            slowType(userEmail, "akshay.s@cyraacs.com");

            WebElement deptDropdown = driver.findElement(By.id("react-select-3-input"));
            deptDropdown.click();
            WebElement deptSelect = driver.findElement(By.id("react-select-3-option-0"));
            deptSelect.click();

            WebElement roleDropdown = driver.findElement(By.id("react-select-4-input"));
            roleDropdown.click();
            WebElement roleSelect = driver.findElement(By.id("react-select-4-option-1"));
            roleSelect.click();

            WebElement submit = driver.findElement(By.xpath("//*[@id='root']/div[2]/div[4]/div[1]/div[2]/div/div[1]/div/div/div/button"));
            submit.click();

            driver.manage().timeouts().implicitlyWait(Duration.ofSeconds(15));

            testToastMessage("User Created Successfully");
        } catch (Exception e) {
            System.out.println("Error creating user: {}" + e.getMessage());
            throw new RuntimeException("Error creating user", e);
        }
    }

    private void deleteUser() {
        try {
            WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(10));

            WebElement deleteIcon = wait.until(ExpectedConditions.elementToBeClickable(By.xpath("/html/body/div[2]/div[2]/div[4]/div[1]/div[2]/div/div[2]/div[1]/div[4]/button")));
            deleteIcon.click();

            WebElement confirmDelete = wait.until(ExpectedConditions.elementToBeClickable(By.xpath("//button[contains(.,'Yes')]")));
            confirmDelete.click();

            wait.until(ExpectedConditions.stalenessOf(deleteIcon)); // Wait for the deleteIcon to become stale

            driver.navigate().refresh();
        } catch (Exception e) {
            System.out.println("Error deleting user: {}" + e.getMessage());
            throw new RuntimeException("Error deleting user", e);
        }
    }

    private void slowType(WebElement element, String text) {
        try {
            for (char c : text.toCharArray()) {
                element.sendKeys(String.valueOf(c));
            }
        } catch (Exception e) {
            System.out.println("Error during slow typing: {}" + e.getMessage());
            throw new RuntimeException("Error during slow typing", e);
        }
    }
}
