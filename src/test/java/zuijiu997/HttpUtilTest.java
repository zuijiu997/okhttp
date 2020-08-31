package zuijiu997;

import junit.framework.TestCase;
import okhttp3.Response;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

public class HttpUtilTest extends TestCase {

    public void testGet() throws IOException {
        String url = "https://localhost:9898/v2/pimPm/hisMetrics?NfvoId=BJ-NFVO-1&qType=pim";
        Map<String, String> headerMap = new HashMap<String, String>();
        headerMap.put("Content-Type", "aplication/json;charset=UTF-8");
        headerMap.put("X-Auth-Token", "gAAAAABc5gS7yNdZ3iewFK3vKeX8xwQHZLSqTI7I-HzH4TMbiypFvgQfRgRoIIj_jc49kXu6RvKUCq8w7mAoVsniJkjKwTjPQwZy9pSycq50pU-4uQg7RCgLkRTr1OQImPF0s8gb3qg7a4X85ZE676ut4qb1UH6pg5e1otjBzrqWV4JHCd-BNCg");

        Response response = HttpUtil.get(url, headerMap);
        assertEquals(200, response.code());
        System.out.println(response.body().string());

    }

    public void testPost() {
        String url = "https://localhost:9898/v3/auth/subnetlt2";
        Map<String, String> headerMap = new HashMap<String, String>();
        headerMap.put("Content-Type", "aplication/json;charset=UTF-8");
        headerMap.put("X-Auth-Token", "gAAAAABc5gS7yNdZ3iewFK3vKeX8xwQHZLSqTI7I-HzH4TMbiypFvgQfRgRoIIj_jc49kXu6RvKUCq8w7mAoVsniJkjKwTjPQwZy9pSycq50pU-4uQg7RCgLkRTr1OQImPF0s8gb3qg7a4X85ZE676ut4qb1UH6pg5e1otjBzrqWV4JHCd-BNCg");

        Response response = HttpUtil.post(url, headerMap, "{\"name\":\"ls\"}");
        assertEquals(200, response.code());
    }
}