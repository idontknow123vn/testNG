import com.microsoft.playwright.*;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.junit.jupiter.api.*;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;

import static org.junit.jupiter.api.Assertions.assertTrue;

public class LoginTestJUnit {
    private Playwright playwright;
    private Browser browser;
    private BrowserContext context;
    private static final String FILE_PATH = "src/test/resources/LoginTestCases.xlsx";

    @BeforeEach
    public void setUp() {
        playwright = Playwright.create();
        browser = playwright.chromium().launch(new BrowserType.LaunchOptions().setHeadless(false));
        context = browser.newContext();
    }

    @Test
    public void testLoginFromExcel() throws IOException {
        FileInputStream fileInputStream = new FileInputStream(FILE_PATH);
        Workbook workbook = new XSSFWorkbook(fileInputStream);
        Sheet sheet = workbook.getSheetAt(0);

        // Bắt đầu từ dòng 1 (dòng 0 là tiêu đề cột)
        for (int i = 1; i <= sheet.getLastRowNum(); i++) {
            Row row = sheet.getRow(i);
            String email = row.getCell(0).getStringCellValue();
            String password = row.getCell(1).getStringCellValue();
            String expectedResult = row.getCell(2).getStringCellValue();
            String actualResult = "Fail";

            // Thực thi kiểm thử
            Page page = context.newPage();
            page.navigate("https://www.phptravels.net/login");

            page.fill("input#email", email);
            page.fill("input#password", password);
            page.locator("button#submitBTN").click();

            // Xác minh kết quả
            try {
                page.waitForURL("**/dashboard", new Page.WaitForURLOptions().setTimeout(6000));
                if (page.url().contains("/dashboard")) {
                    actualResult = "Pass";
                }
            } catch (Exception e) {
                // Nếu không điều hướng được đến trang dashboard, actualResult giữ nguyên là "Fail"
            }
            page.close();

            // Ghi kết quả kiểm thử vào cột 4
            Cell resultCell = row.createCell(3);
            resultCell.setCellValue(actualResult);
            assertTrue(expectedResult.equals(actualResult),
                       "Test case thất bại với Email: " + email + " và Password: " + password);
        }

        // Đóng workbook
        fileInputStream.close();
        FileOutputStream fileOut = new FileOutputStream(FILE_PATH);
        workbook.write(fileOut);
        fileOut.close();
        workbook.close();
    }

    @AfterEach
    public void tearDown() {
        if (context != null) {
            context.close();
        }
        if (browser != null) {
            browser.close();
        }
        if (playwright != null) {
            playwright.close();
        }
    }
}





// import static org.junit.jupiter.api.Assertions.assertTrue;

// import static org.junit.jupiter.api.Assertions.assertFalse;

// import org.junit.jupiter.api.AfterEach;
// import org.junit.jupiter.api.BeforeEach;
// import org.junit.jupiter.api.Test;

// import com.microsoft.playwright.*;

// public class LoginTest {
//     private Playwright playwright;
//     private Browser browser;
//     private BrowserContext context;

//     @BeforeEach
//     public void setUp() {
//         playwright = Playwright.create();
//         browser = playwright.chromium().launch(new BrowserType.LaunchOptions().setHeadless(false));
//         context = browser.newContext();
//     }

//     @Test
//     public void testLogin1() {
//         boolean isLoginSuccess = false;
//         Page page = context.newPage();
//         page.navigate("https://www.phptravels.net/login", new Page.NavigateOptions().setTimeout(6000)); // Tăng timeout nếu cần

//         try {
//             Thread.sleep(2000);
//         } catch (InterruptedException e) {
//             System.out.println("Error: " + e.getMessage());
//         }

//         // Nhập email và mật khẩu
//         page.fill("input#email", "vuducnguyen000@gmail.com");
//         page.fill("input#password", "test123");

//         // Nhấn nút đăng nhập
//         page.locator("button#submitBTN").click();

//         // Đợi trang điều hướng đến trang dashboard hoặc URL mục tiêu
//         page.waitForURL("**/dashboard", new Page.WaitForURLOptions().setTimeout(6000));

//         // Kiểm tra xem trang đã chuyển hướng thành công hay chưa bằng cách xác minh URL hoặc một phần tử đặc trưng
//         isLoginSuccess = page.url().contains("/dashboard");
//         assertTrue(isLoginSuccess, "Đăng nhập thất bại: Không chuyển hướng đến dashboard.");
//     }
//     @Test
//     public void testLogin2() {
//         boolean isLoginSuccess = false;
//         Page page = context.newPage();
//         page.navigate("https://www.phptravels.net/login", new Page.NavigateOptions().setTimeout(6000)); // Tăng timeout nếu cần
//         try {
//             Thread.sleep(2000);
//             // Nhập email và mật khẩu
//             page.fill("input#email", "vuducnguyen000@gmail.com");
//             page.fill("input#password", "dfsdfsdf");

//             // Nhấn nút đăng nhập
//             page.locator("button#submitBTN").click();
        
//             // Đợi trang điều hướng đến trang dashboard hoặc URL mục tiêu
//             page.waitForURL("**/dashboard", new Page.WaitForURLOptions().setTimeout(6000));
//             isLoginSuccess = page.url().contains("/dashboard");
            
//         } catch (InterruptedException e) {
//             System.out.println("Error: " + e.getMessage());
//         }
//          catch (Exception e) {

//         }
//         assertFalse(isLoginSuccess, "Lỗi đăng nhập: sai tài khoản mật khẩu vẫn đăng nhập thành công"); 
//     }

//     @Test
//     public void testLogin3() {
//         boolean isLoginSuccess = false;
//         Page page = context.newPage();
//         page.navigate("https://www.phptravels.net/login", new Page.NavigateOptions().setTimeout(6000)); // Tăng timeout nếu cần
//         try {
//             Thread.sleep(2000);
//             // Nhập email và mật khẩu
//             page.fill("input#email", "vuducnguyen000@gmail.com");
//             page.fill("input#password", "");

//             // Nhấn nút đăng nhập
//             page.locator("button#submitBTN").click();
        
//             // Đợi trang điều hướng đến trang dashboard hoặc URL mục tiêu
//             page.waitForURL("**/dashboard", new Page.WaitForURLOptions().setTimeout(6000));
//             isLoginSuccess = page.url().contains("/dashboard");
            
//         } catch (InterruptedException e) {
//             System.out.println("Error: " + e.getMessage());
//         } catch (Exception e) {

//         }
//         assertFalse(isLoginSuccess, "Lỗi đăng nhập: sai tài khoản mật khẩu vẫn đăng nhập thành công"); 
//     }

//     @Test
//     public void testLogin4() {
//         boolean isLoginSuccess = false;
//         Page page = context.newPage();
//         page.navigate("https://www.phptravels.net/login", new Page.NavigateOptions().setTimeout(6000)); // Tăng timeout nếu cần
//         try{
//             Thread.sleep(2000);
//             // Nhập email và mật khẩu
//             page.fill("input#email", "");
//             page.fill("input#password", "");

//             // Nhấn nút đăng nhập
//             page.locator("button#submitBTN").click();

//             // Đợi trang điều hướng đến trang dashboard hoặc URL mục tiêu
//             page.waitForURL("**/dashboard", new Page.WaitForURLOptions().setTimeout(6000));
//             isLoginSuccess = page.url().contains("/dashboard");
//         } catch (InterruptedException e) {
//             System.out.println("Error: " + e.getMessage());
//         } catch (Exception e) {
            
//         }
//         assertFalse(isLoginSuccess, "Lỗi đăng nhập: sai tài khoản mật khẩu vẫn đăng nhập thành công"); 
//     }

//     @AfterEach
//     public void tearDown() {
//         if (context != null) {
//             context.close();
//         }
//         if (browser != null) {
//             browser.close();
//         }
//         if (playwright != null) {
//             playwright.close();
//         }
//     }
// }