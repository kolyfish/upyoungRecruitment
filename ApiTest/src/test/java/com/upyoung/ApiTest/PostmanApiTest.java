package com.upyoung.ApiTest;

import io.restassured.RestAssured;
import io.restassured.response.Response;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.Assertions;


import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;


public class PostmanApiTest {
	  private final String baseUrl = "https://postman-echo.com";

	    // GET API - 成功案例
	    @Test
	    void testGetRequest_Success() {
	        long startTime = System.currentTimeMillis();

	        // 發送 GET 請求  在url 加入了一個查詢參數 param1，其值為 value1。
	        //https://postman-echo.com/get?param1=value1

	        Response response = RestAssured.given()
	                .queryParam("param1", "value1")
	                .get(baseUrl + "/get");

	        // 驗證
	        Assertions.assertEquals(200, response.getStatusCode());
	        //檢查回應中參數是否匹配。
	        Assertions.assertEquals("value1", response.jsonPath().getString("args.param1"));

	        long endTime = System.currentTimeMillis();
	        System.out.println("1. **GET成功案例**：");
	        System.out.println("GET Success Test Execution Time: " + (endTime - startTime) + " ms");
	        System.out.println("預期成功GET請求狀態碼:"+response.getStatusCode());
	        
	    }

	    // GET API - 失敗案例
	    @Test
	    void testGetRequest_Failure() {
	        long startTime = System.currentTimeMillis();
	        // 定義路徑
	        String path = "/nonexistent";
	        String fullUrl = baseUrl + path;
	        
	        // 發送 GET 請求到不存在的路徑
	        Response response = RestAssured.get(fullUrl);
	        

	        // 打印請求資訊
	        System.out.println("2. **GET失敗案例**：");
	        System.out.println("預期失敗GET請求狀態碼: " + response.getStatusCode() + 
	                           " 此刻的路徑: " + fullUrl + 
	                           " 測試為失敗因為路徑結尾為 " + path);
	        // 驗證
	        Assertions.assertEquals(404, response.getStatusCode(), "應返回狀態碼 404");
	       
	       
	        if ( response.getStatusCode()==404) {
                Assertions.fail("測試失敗：把此測試強制加入Failure 在CI/CD Pipeline中可以強調此測試失敗不可以被忽略 ");
	        }

	        long endTime = System.currentTimeMillis();
	        System.out.println("GET Failure Test Execution Time: " + (endTime - startTime) + " ms");	        
	    }

	    // 工具方法：動態創建測試文件
	    private File createTestFile(String content) throws IOException {
	        Path tempFile = Files.createTempFile("testFile", ".txt");
	        Files.writeString(tempFile, content); // 寫入文件內容
	        return tempFile.toFile();
	    }
	    
	    // POST Form Data - 成功案例
	    
	    @Test
	    void testPostFormData_Success() throws Exception {
	        long startTime = System.currentTimeMillis();

	       try {
	    	   
	    	   // 準備測試文件
	    	   File testFile = createTestFile("correct"); // 文件內容為 "correct"
	    	   
	    	   // 發送 POST 請求，multiPart包含 form-data 和文件上傳
	    	   Response response = RestAssured.given()
	    			   .multiPart("name", "John") // 添加文本字段
	    			   .multiPart("job", "Tester") // 添加文本字段
	    			   .multiPart("file", testFile) // 添加文件字段
	    			   .post(baseUrl + "/post");
	    	   
	    	   // 打印響應資訊
	    	   System.out.println("3. **POST成功案例**：");
	           System.out.println("Response Status Code: " + response.getStatusCode());
	           System.out.println("Response Body: " + response.getBody().asString());
	           System.out.println("Request Headers: " + response.getHeaders());

	 
	           // 驗證文件內容是否包含 "correct"
	           String fileContent = Files.readString(testFile.toPath());
               Assertions.assertTrue(fileContent.contains("correct"), "File content does not contain 'correct'");

	    	   Assertions.assertEquals(200, response.getStatusCode());
	    	   // 驗證伺服器返回的字段是否正確
	            Assertions.assertEquals("John", response.jsonPath().getString("form.name"));
	            Assertions.assertEquals("Tester", response.jsonPath().getString("form.job"));
 	    	   System.out.println("預期成功POST請求狀態碼:"+response.getStatusCode());
	    	

	        } catch (Exception e) {
	            System.err.println("Error occurred during API request: " + e.getMessage());
	            throw e; // 測試失敗，但記錄錯誤
	        }
	        long endTime = System.currentTimeMillis();
	        System.out.println("POST Form Data Test Execution Time: " + (endTime - startTime) + " ms");

	       
	    }
	    //POST Form Data - 失敗案例
	    @Test
	    void testPostFormData_Failure() throws Exception {
	        long startTime = System.currentTimeMillis();

	        // 準備測試文件（內容為 "wrong"）
	        File testFile = createTestFile("wrong");

	        try {
	            // 發送 POST 請求，包含 form-data 和文件上傳
	            Response response = RestAssured.given()
	                    .multiPart("name", "John") // 添加文本字段
	                    .multiPart("job", "Tester") // 添加文本字段
	                    .multiPart("file", testFile) // 添加文件字段
	                    .post(baseUrl + "/post");

	            // 打印響應資訊
	            System.out.println("4. **POST失敗案例**：");
	            System.out.println("Response Status Code: " + response.getStatusCode());
	            System.out.println("Response Body: " + response.getBody().asString());

	            // 驗證伺服器返回的狀態碼是否為 200
	            Assertions.assertEquals(200, response.getStatusCode(), "預期失敗測試回狀態碼 200");

	            // 讀取文件內容
	            String fileContent = Files.readString(testFile.toPath());
	            System.out.println("測試文件內容: " + fileContent);

	            // **如果文件內容不包含 "correct"，則應該失敗**
	            if (!fileContent.contains("correct")) {
	                                
	                // **強制測試失敗**
	                Assertions.fail("測試失敗：文件內容不符合預期！沒有包含 'correct'所以測試失敗，實際內容為: " + fileContent);
	            }

	        } catch (AssertionError e) {
	            // **當發生測試失敗時，輸出詳細日誌**
	            System.out.println("\n==== POST Form Data - 失敗案例 詳細日誌 ====");
	            System.out.println("請求 URL: " + baseUrl + "/post");
	            System.out.println("實際返回狀態碼: " + (e.getMessage().contains("200") ? "200" : "非 200 狀態碼"));
	            System.out.println("測試文件內容不符合預期，但實際返回 JSON: " + e.getMessage());
	            System.out.println("測試執行時間: " + (System.currentTimeMillis() - startTime) + " ms");
	            System.out.println("=========================================\n");

	            throw e; // **確保測試仍然 FAIL**
	        } finally {
	            long endTime = System.currentTimeMillis();
	            System.out.println("POST Form Data Test Execution Time: " + (endTime - startTime) + " ms");
	        }
	    }
}
