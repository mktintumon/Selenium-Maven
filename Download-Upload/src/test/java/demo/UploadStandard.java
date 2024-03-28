package demo;

import com.aventstack.extentreports.ExtentReports;
import com.aventstack.extentreports.ExtentTest;
import com.aventstack.extentreports.reporter.ExtentSparkReporter;
import org.openqa.selenium.*;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeOptions;
import org.openqa.selenium.interactions.Actions;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;
import org.testng.Assert;
import org.testng.annotations.AfterClass;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;

import java.io.File;
import java.time.Duration;

public class UploadStandard {

    WebDriver driver;
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

            ChromeOptions options = new ChromeOptions();
            options.setExperimentalOption("prefs", "{\"download.default_directory\":\"" + AppConstants.DOWNLOAD_DEFAULT_DIRECTORY + "\"}");
        } catch (Exception e) {
            throw new RuntimeException("WebDriver setup failed", e);
        }
    }

    @Test
    public void SuccessLogin() {
        actions = new Actions(driver);
        try {
            login();
            openStandardUploadSection();
            clickUploadButton();

        } catch (Exception e) {
            throw new RuntimeException("Error : ", e);
        }
    }

    @Test(dependsOnMethods = "SuccessLogin")
    public void DownloadExcelTemplate() {
        test = extent.createTest("Test-2 : Download Excel Template");

        try {
            WebElement clickDownloadButton = driver.findElement(By.id("download-excel-template"));
            clickDownloadButton.click();

            boolean isFileDownloaded = waitForFileDownload(AppConstants.DOWNLOAD_EXCEL_TEMPLATE_NAME);
            if (isFileDownloaded) {
                test.info(AppConstants.DOWNLOAD_EXCEL_TEMPLATE_NAME + " Downloaded Successfully");
                test.pass("Download Excel Template - Test case passed ✔");

                deleteDownloadedFile(AppConstants.DOWNLOAD_EXCEL_TEMPLATE_NAME);
            } else {
                test.fail("File not downloaded within the timeout period.");
            }
        } catch (NoSuchElementException e) {
            // Handle NoSuchElementException
            test.fail("Element not found: " + e.getMessage());
        } catch (Exception e) {
            // Handle other exceptions
            test.fail("Exception occurred: " + e.getMessage());
        }
    }


    @Test(dependsOnMethods = "DownloadExcelTemplate")
    public void DownloadOutputReport() {
        test = extent.createTest("Test-3 : Download Output Report");

        try {
            WebElement clickDownloadButton = driver.findElement(By.id("download-link"));
            clickDownloadButton.click();

            boolean isFileDownloaded = waitForFileDownload(AppConstants.DOWNLOAD_FILENAME);
            if (isFileDownloaded) {
                test.info(AppConstants.DOWNLOAD_FILENAME + " file downloaded successfully.");
                test.pass("Download Output Report - Test case passed ✔");

                deleteDownloadedFile(AppConstants.DOWNLOAD_FILENAME);
            } else {
                test.fail("File not downloaded within the timeout period.");
            }
        } catch (NoSuchElementException e) {
            // Handle NoSuchElementException
            test.fail("Element not found: " + e.getMessage());
        } catch (Exception e) {
            // Handle other exceptions
            test.fail("Exception occurred: " + e.getMessage());
        }
    }


    @Test(dependsOnMethods = "DownloadOutputReport")
    public void SearchBox() {
        test = extent.createTest("Test-4 : Search-Box Testing");

        try {
            WebElement searchInputBox = driver.findElement(By.className("search-input"));
            searchInputBox.sendKeys("Standard_Template (1).xlsx");

            String searchText = searchInputBox.getAttribute("value");
            Assert.assertEquals(searchText, "Standard_Template (1).xlsx", "Search text does not match");

            test.info("Search-Box Testing Success");
            test.pass("Search-Box Testing - Test case passed ✔");
        } catch (NoSuchElementException e) {
            // Handle NoSuchElementException
            test.fail("Element not found: " + e.getMessage());
        } catch (Exception e) {
            // Handle other exceptions
            test.fail("Exception occurred: " + e.getMessage());
        }
    }

    @Test(dependsOnMethods = "SearchBox")
    public void UploadFile(){
        test = extent.createTest("Test-5 : Upload File Testing");

        try {
            WebElement uploadButton = driver.findElement(By.id("upload-standard-btn"));
            uploadButton.click();

            driver.manage().timeouts().implicitlyWait(Duration.ofSeconds(10));

            WebElement dropzone = driver.findElement(By.xpath("//input[@type='file']"));
            dropzone.sendKeys(AppConstants.UPLOAD_FILE_PATH);

            driver.manage().timeouts().implicitlyWait(Duration.ofSeconds(10));

            WebElement uploadFileButton = driver.findElement(By.id("upload-control-validate-btn"));
            uploadFileButton.click();

            test.pass("Uploading is done");
        } catch (NoSuchElementException e) {
            // Handle NoSuchElementException
            test.fail("Element not found: " + e.getMessage());
        } catch (Exception e) {
            // Handle other exceptions
            test.fail("Exception occurred: " + e.getMessage());
        }
    }


    private void login() {
        try {
            test = extent.createTest("Test-1 : Successful Login");
            driver.get(AppConstants.WEBSITE_NAME);
            driver.manage().window().maximize();

            WebElement emailInput = driver.findElement(By.id("email"));
            emailInput.sendKeys(AppConstants.USER_NAME);

            WebElement passwordInput = driver.findElement(By.id("password"));
            passwordInput.sendKeys(AppConstants.USER_PASSWORD);

            WebElement loginButton = driver.findElement(By.xpath("//button[contains(@class, 'btn-primary')]"));
            loginButton.click();
            driver.manage().timeouts().implicitlyWait(Duration.ofSeconds(20));

            testToastMessage("Login Successful");
            test.info("Successful Login done");
            test.pass("Login Test case passed ✔");
        } catch (Exception e) {
            test.fail("Success Login failed: " + e.getMessage());
            throw new RuntimeException("Login failed", e);
        }
    }

    private void openStandardUploadSection() {
        try {
            WebElement openBar = driver.findElement(By.className("collapse-button-collapsed"));
            openBar.click();

            WebElement stdUploadBar = driver.findElement(By.xpath("//*[@id=\"root\"]/div[2]/div[3]/div[2]/a[6]/div/div/span"));
            stdUploadBar.click();
        } catch (Exception e) {
            throw new RuntimeException("Error opening user section", e);
        }
    }

    private void clickUploadButton() {
        try {
            WebElement uploadButton = driver.findElement(By.id("std-upload-btn"));
            uploadButton.click();
        } catch (Exception e) {
            throw new RuntimeException("Error opening Upload Button ", e);
        }
    }

    private void testToastMessage(String assertValue) {
        try {
            WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(5));
            //String toastifyContent = toastify.getText();

            WebElement toastify = wait.until(ExpectedConditions.presenceOfElementLocated(By.cssSelector(".Toastify__toast-body > div:nth-child(2)")));

            // Use JavaScript execution to retrieve the text content
            JavascriptExecutor js = (JavascriptExecutor) driver;
            String toastifyContent = (String) js.executeScript("return arguments[0].textContent;", toastify);

            wait.until(ExpectedConditions.invisibilityOf(toastify));

            System.out.println("Content inside the Toastify div: " + toastifyContent);
            Assert.assertEquals(toastifyContent, assertValue, "Test case failed: Unexpected toastify message");
        } catch (Exception e) {
            System.out.println("Error during testing toast message: {}" + e.getMessage());
            throw new RuntimeException("Toast message test failed", e);
        }
    }

    private static boolean waitForFileDownload(String fileName) {
        File dir = new File(AppConstants.DOWNLOAD_DEFAULT_DIRECTORY);

        int timeoutInSeconds = 10;
        int elapsedTime = 0;
        while (elapsedTime < timeoutInSeconds) {
            File[] dirContents = dir.listFiles();
            assert dirContents != null;
            for (File file : dirContents) {
                if (file.getName().equals(fileName)) {
                    return true;
                }
            }
            try {
                Thread.sleep(1000); // Check every second
                elapsedTime++;
            } catch (InterruptedException e) {
                return false;
            }
        }
        return false; // Timeout reached
    }


    private static void deleteDownloadedFile(String fileName) {
        File dir = new File(AppConstants.DOWNLOAD_DEFAULT_DIRECTORY);
        File fileToDelete = new File(dir, fileName);
        if (fileToDelete.exists()) {
            if (fileToDelete.delete()) {
                System.out.println("Deleted the downloaded file: " + fileName);
            } else {
                System.out.println("Failed to delete the downloaded file: " + fileName);
            }
        } else {
            System.out.println("Downloaded file not found: " + fileName);
        }
    }

    @AfterClass
    public void teardown() {
        extent.flush();
        try {
            if (driver != null) {
//                driver.quit();
            }
        } catch (Exception e) {
            System.out.println("Error during WebDriver teardown: {}" + e.getMessage());
        }
    }
}
