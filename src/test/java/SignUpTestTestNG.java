import java.io.IOException;
import java.util.List;
import java.util.concurrent.TimeUnit;

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
import com.microsoft.playwright.FrameLocator;
import com.microsoft.playwright.Locator;
import com.microsoft.playwright.Page;
import com.microsoft.playwright.Playwright;
import com.microsoft.playwright.TimeoutError;
import com.microsoft.playwright.options.LoadState;

import okhttp3.*;

public class SignUpTestTestNG {
    private Playwright playwright;
    private Browser browser;
    private ExcelReader reader;
    private ExcelWriter writer;
    private static final String INPUT_FILE_PATH = "src/test/resources/input.xlsx";
    private static final String OUTPUT_FILE_PATH = "src/test/resources/output.xlsx";
    private static final String URL = "https://www.phptravels.net/signup";
    private String apiKey = "6LeIxAcTAAAAAGG-vFI1TnRWxMZNFuojJ4WifJWe"; // API key của 2Captcha
    private String siteKey = "6LdX3JoUAAAAAFCG5tm0MFJaCF3LKxUN4pVusJIF"; // Site key của reCaptcha trên trang web
    private String url = "https://example.com"; // URL của trang chứa reCaptcha checkbox


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
    public void signUpTest(String id, String description, String firtName, String lastName, String country, String phone, String email, String password, String expectedResult) throws Exception {
        BrowserContext context = browser.newContext();
        Page page = context.newPage();
        page.navigate(URL, new Page.NavigateOptions().setTimeout(90000)); // Tăng timeout nếu cần
        // page.navigate(URL);
        page.waitForLoadState(LoadState.DOMCONTENTLOADED);

        // // Request reCaptcha solution from 2Captcha
        // OkHttpClient client = new OkHttpClient();
        // Request request = new Request.Builder()
        //         .url("http://2captcha.com/in.php?key=" + apiKey + "&method=userrecaptcha&googlekey=" + siteKey + "&pageurl=" + url)
        //         .build();

        // Response response = client.newCall(request).execute();
        // String captchaId = response.body().string().split("\\|")[1];

        // // Poll for the solution
        // System.out.println("Waiting for 2Captcha to solve the reCaptcha...");
        // String tokenResponse;
        // do {
        //     TimeUnit.SECONDS.sleep(5);
        //     Request tokenRequest = new Request.Builder()
        //             .url("http://2captcha.com/res.php?key=" + apiKey + "&action=get&id=" + captchaId)
        //             .build();
        //     Response tokenResp = client.newCall(tokenRequest).execute();
        //     tokenResponse = tokenResp.body().string();
        // } while (tokenResponse.contains("NOT_READY"));

        // // Extract the token
        // String token = tokenResponse.split("\\|")[1];

        // // Locate the reCaptcha iframe
        // FrameLocator reCaptchaFrame = page.frameLocator("//iframe[@title='reCAPTCHA']");
        // if (reCaptchaFrame != null) {
        //     // Insert the token into the response field
        //     // Inject the token into the reCAPTCHA response field using JavaScript
        //     page.evaluate("document.querySelector('iframe[title=\"reCAPTCHA\"]').contentWindow.document.getElementById('g-recaptcha-response').innerHTML = '" + token + "';");
        //     page.evaluate("document.querySelector('iframe[title=\"reCAPTCHA\"]').contentWindow.document.getElementById('g-recaptcha-response').dispatchEvent(new Event('change'));");

        //     // Submit the form
        //     page.click("button[type=submit]");
        // } else {
        //     System.out.println("Could not locate reCaptcha iframe.");
        // }


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

        FrameLocator frame = page.frameLocator("//iframe[@title='reCAPTCHA']");
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
