import java.io.IOException;
import java.util.List;

import org.testng.annotations.AfterClass;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.DataProvider;
import org.testng.annotations.Test;

import com.example.testing.ExcelReader;
import com.example.testing.ExcelWriter;
import com.microsoft.playwright.Browser;
import com.microsoft.playwright.BrowserContext;
import com.microsoft.playwright.BrowserType;
import com.microsoft.playwright.Frame;
import com.microsoft.playwright.Page;
import com.microsoft.playwright.Playwright;
import com.microsoft.playwright.TimeoutError;
import com.microsoft.playwright.options.LoadState;

public class SignUpTestTestNG {
    private Playwright playwright;
    private Browser browser;
    private ExcelReader reader;
    private ExcelWriter writer;
    private static final String INPUT_FILE_PATH = "src/test/resources/input.xlsx";
    private static final String OUTPUT_FILE_PATH = "src/test/resources/output.xlsx";
    private static final String URL = "https://www.phptravels.net/signup";

    @BeforeClass
    public void setUp() {
        playwright = Playwright.create();
        browser = playwright.chromium().launch(new BrowserType.LaunchOptions().setHeadless(false));

        reader = new ExcelReader();
        writer = new ExcelWriter();
    }
    
    @DataProvider(name = "signUpData")
    public Object[][] signUpData() throws IOException {
        List<String[]> signUpData = reader.readSignUpData(INPUT_FILE_PATH, 1);

        Object[][] data = new Object[signUpData.size()][9]; // Tạo mảng dữ liệu cho DataProvider

        for (int i = 0; i < signUpData.size(); i++) {
            data[i] = signUpData.get(i);
        }
        return data;
    }

    @Test(dataProvider = "signUpData")
    public void signUpTest(String id, String description, String firtName, String lastName, String country, String phone, String email, String password, String expectedResult) throws IOException {
        BrowserContext context = browser.newContext();
        Page page = context.newPage();
        page.navigate(URL, new Page.NavigateOptions().setTimeout(6000)); // Tăng timeout nếu cần
        page.waitForLoadState(LoadState.DOMCONTENTLOADED);

        try {
            Thread.sleep(2000);
        } catch (InterruptedException e) {
            System.out.println("Error: " + e.getMessage());
        }

        page.fill("input[id='firstname']", firtName);
        page.fill("input[id='last_name']", lastName);

        page.locator("//button[div[div[div[text()='Select Country']]]]").click();
        page.locator("//input[@type='search' and @class='form-control']").fill(country);
        try {
            page.locator("//div[@id='bs-select-1']").click();
        } catch (TimeoutError e) {
            writer.writeResult(OUTPUT_FILE_PATH, id, description, expectedResult, "Fail", 1);
            context.close();
            return;
        }

        page.fill("input[id='phone']", phone);
        page.fill("input[id='user_email']", email);
        page.fill("input[id='password']", password);
        Frame frame = page.frames().get(0);
        frame.locator("//span[@id='recaptcha-anchor']").click();
        page.click("button[type='submit']");

        try {
            page.waitForURL("**/signup-success", new Page.WaitForURLOptions().setTimeout(6000));
            boolean isSignUpSuccess = page.url().contains("signup-success");
            String actualResult = isSignUpSuccess ? "Pass" : "Fail";
            writer.writeResult(OUTPUT_FILE_PATH, id, description, expectedResult, actualResult, 1);
            // assert actualResult.equals(expectedResult);
        } catch (TimeoutError e) {
            writer.writeResult(OUTPUT_FILE_PATH, id, description, expectedResult, "Timeout error", 1);
            // assert false;
        } finally {
            context.close();
        }
    }
    
    @AfterClass
    public void tearDown() {
        browser.close();
        playwright.close();
    }
}
