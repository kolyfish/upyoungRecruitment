package com.upyoung.ApiTest;

import io.restassured.RestAssured;
import io.restassured.response.Response;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.Assertions;

import java.time.LocalDate;

public class OpenLibraryApiTest {
    private final String baseUrl = "https://openlibrary.org";

    // 1. Subjects API - 成功案例
    @Test
    void testSubjectsApi_Success() {
        long startTime = System.currentTimeMillis();
        String subject = "science";
        String yearRange = "2000-2010";
        String url = baseUrl + "/subjects/" + subject + ".json?published_in=" + yearRange;

        // 發送 GET 請求
        Response response = RestAssured.get(url);

        // 驗證
        Assertions.assertEquals(200, response.getStatusCode(), "預期狀態碼為 200");
        Assertions.assertTrue(response.jsonPath().getList("works").size() > 0, "應返回至少一筆資料");
        
        Object works = response.jsonPath().get("works");
        System.out.println("1. Subjects API - 成功案例 works 類型：" + works.getClass().getName());
        System.out.println("Subjects API - 成功案例 年期區間為"+yearRange+"驗證返回的"+subject+"主題資料中不為空 其值：" + works);
        System.out.println("Subjects API - 成功案例 執行時間: " + (System.currentTimeMillis() - startTime) + " ms");
    }

    // 2. Subjects API - 預期失敗案例
    @Test
    void testSubjectsApi_Failure() {
        long startTime = System.currentTimeMillis();
        String subject = "love";
        String yearRange = "1500-1600";
        String url = baseUrl + "/subjects/" + subject + ".json?published_in=" + yearRange;

        // 發送 GET 請求
        Response response = RestAssured.get(url);

        try {
            // 驗證返回的狀態碼
            Assertions.assertEquals(200, response.getStatusCode(), "應返回狀態碼 200");

            // 驗證 works 是否為空 ，預期為空為真，有值則測試失敗
            Assertions.assertTrue(response.jsonPath().getList("works").isEmpty(), "應返回空結果");
        } catch (AssertionError e) {
            // 捕捉驗證失敗時的日誌
            System.out.println("2. Subjects API - 預期失敗案例 Test Failed! 以下是詳細日誌：");
            System.out.println("請求 URL: " + url);
            System.out.println("實際返回狀態碼: " + response.getStatusCode());
            System.out.println("模擬測試主題" +subject+"返回測試年期為"+yearRange+"預期返回空但是有資料所以失敗，返回 JSON: " + response.getBody().asString());
            System.out.println("測試執行時間: " + (System.currentTimeMillis() - startTime) + " ms");
            // 如果 works 不為空，打印其類型和值
            try {
                Object works = response.jsonPath().get("works");
                System.out.println("works 類型：" + (works != null ? works.getClass().getName() : "null"));
               // System.out.println("works 值：" + works);
            } catch (Exception ex) {
                System.out.println("解析 works 時發生錯誤：" + ex.getMessage());
            }

            // 重新拋出異常讓測試標記為失敗
            throw e;
        }
        Object works = response.jsonPath().get("works");
        System.out.println("Subjects 預期失敗案例 works 類型：" + works.getClass().getName());
        //System.out.println("Subjects 預期失敗案例 works 值：" + works);
        System.out.println("Subjects 預期失敗案例 Subjects API 失敗案例執行時間: " + (System.currentTimeMillis() - startTime) + " ms");
    }

    // 3. Recent Changes API - 成功案例
    @Test  
    void testRecentChangesApi_Success() {
        long startTime = System.currentTimeMillis();
        //String date = LocalDate.now().minusDays(1).toString();
        String date = "2023/01/01";

        String url = baseUrl + "/recentchanges/" + date + ".json";

        // 發送 GET 請求
        Response response = RestAssured.get(url);

        // 驗證
        Assertions.assertEquals(200, response.getStatusCode(), "預期狀態碼為 200");
        Assertions.assertTrue(response.jsonPath().getList("$").size() > 0, "應返回至少一筆資料");
        System.out.println("3. RecentChangesApi_Success GET請求狀態碼: " + response.getStatusCode() + 
              "返回測試日期為"+date+"變更的非空資料, 回應body: " + response.asString() );
        System.out.println("Recent Changes API 成功案例執行時間: " + (System.currentTimeMillis() - startTime) + " ms");
    }

    // 4. Recent Changes API - 失敗案例
    @Test
    void testRecentChangesApi_Failure() {
        // 記錄測試開始時間
        long startTime = System.currentTimeMillis();
        String date = "2020/01/01";
        String url = baseUrl + "/recentchanges/2020/01/01.json";

        // 發送 GET 請求
        Response response = RestAssured.get(url);
        try {
            // 驗證狀態碼是否為 200
            Assertions.assertEquals(200, response.getStatusCode(), "預期失敗測試回狀態碼 200");
            // 模擬預期資料為空的場景
            boolean isResponseEmpty = response.jsonPath().getList("$").isEmpty();            
            // 此處預期失敗的原因是資料實際上不為空
            Assertions.assertTrue(isResponseEmpty, 
                "測試預期資料為空，但實際上返回了資料: " + response.jsonPath().getList("$"));
        } catch (AssertionError e) {
            // 打印詳細日誌
            System.out.println(" 4. Recent Changes API - 失敗案例 Test Failed! 以下是詳細日誌：");
            System.out.println("請求 URL: " + url);
            System.out.println("實際返回狀態碼: " + response.getStatusCode());
            System.out.println("返回測試日期為"+date+"測試預期資料為空，但有值實際上返回 JSON: " + response.getBody().asString());
            System.out.println("測試執行時間: " + (System.currentTimeMillis() - startTime) + " ms");

            // 重新拋出異常，讓測試標記為失敗
            throw e;
        }       
        // 輸出除錯資訊
        System.out.println("Test Recent ChangesAPI expect fail:" + response.jsonPath().getList("$"));
        System.out.println("print it status: " + response.getStatusCode());
        System.out.println("print it body as string: " + response.getBody().asString());
        System.out.println("Recent Changes API 失敗案例執行時間: " + (System.currentTimeMillis() - startTime) + " ms");
    }
}
