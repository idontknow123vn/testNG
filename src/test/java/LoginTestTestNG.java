import static org.junit.jupiter.api.Assertions.assertTrue;

import java.io.IOException;
import java.util.List;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.testng.annotations.AfterClass;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.DataProvider;
import org.testng.annotations.Test;

import com.microsoft.playwright.Browser;
import com.microsoft.playwright.BrowserContext;
import com.microsoft.playwright.BrowserType;
import com.microsoft.playwright.Page;
import com.microsoft.playwright.Playwright;
import com.microsoft.playwright.TimeoutError;
import com.example.testing.ExcelReader;
import com.example.testing.ExcelWriter;

public class LoginTestTestNG {
    private Playwright playwright;
    private Browser browser;
    private ExcelReader reader;
    private ExcelWriter writer;
    private static final Logger logger = LogManager.getLogger(LoginTestTestNG.class);
    private static final String INPUT_FILE_PATH = "src/test/resources/input.xlsx";
    private static final String OUTPUT_FILE_PATH = "src/test/resources/output.xlsx";
    private static final String URL = "https://www.phptravels.net/login";

    @BeforeClass
    public void setUp() {
        playwright = Playwright.create();
        browser = playwright.chromium().launch(new BrowserType.LaunchOptions().setHeadless(false));

        reader = new ExcelReader();
        writer = new ExcelWriter();
    }

    @DataProvider(name = "loginData")
    public Object[][] loginData() throws IOException {
        List<String[]> loginData = reader.readLoginData(INPUT_FILE_PATH, 0);

        Object[][] data = new Object[loginData.size()][5]; // Tạo mảng dữ liệu cho DataProvider

        for (int i = 0; i < loginData.size(); i++) {
            data[i] = loginData.get(i);
        }
        return data;
    }

    @Test(dataProvider = "loginData")
    public void testLogin(String id, String description, String email, String password, String expectedResult) throws IOException {
        BrowserContext context = browser.newContext();
        Page page = context.newPage();
        page.navigate(URL, new Page.NavigateOptions().setTimeout(6000)); // Tăng timeout nếu cần

        try {
            Thread.sleep(2000);
        } catch (InterruptedException e) {
            System.out.println("Error: " + e.getMessage());
        }

        // Nhập email và mật khẩu
        page.fill("input#email", email);
        page.fill("input#password", password);

        // Nhấn nút đăng nhập
        page.locator("button#submitBTN").click();

        boolean isLoginSuccess = false;
        try {
            page.waitForURL("**/dashboard", new Page.WaitForURLOptions().setTimeout(3000));
            isLoginSuccess = page.url().contains("/dashboard");
        } catch (TimeoutError e) {
            System.out.println("Error: " + e.getMessage());
        } finally {
            context.close();
        }

        String actualResult = isLoginSuccess ? "Pass" : "Fail";

        logger.info("Test ID: " + id);
        logger.info("Description: " + description);
        logger.info("Expected Result: " + expectedResult);
        logger.info("Actual Result: " + actualResult);

        // So sánh Expected result với Actual result
        if (expectedResult.equals(actualResult)) {
            logger.info("Test Passed");
        } else {
            logger.info("Test Failed");
        }

        writer.writeResult(OUTPUT_FILE_PATH, id, description, expectedResult, actualResult, 0);
        assertTrue(expectedResult.equals(actualResult));
    }

    @AfterClass
    public void tearDown() {
        browser.close();
        playwright.close();
    }

}
