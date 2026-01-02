package org.example;

import com.fastcgi.FCGIInterface;

import java.io.*;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

public class Main {



    public static void main(String[] args) throws IOException {
        FCGIInterface fcgi = new FCGIInterface();
        while (fcgi.FCGIaccept() >= 0) {
            long startTime = System.nanoTime();
            String queryParams = System.getProperty("QUERY_STRING");

            Map<String, String> params = new HashMap<>();
            if (queryParams != null && !queryParams.isEmpty()){
                for (String pair : queryParams.split("&")) {
                    String[] kv = pair.split("=");
                    if (kv.length == 2) {
                        params.put(kv[0], kv[1]);
                    }
                }
            }

            try {
                int x = Integer.parseInt(params.get("x"));
                float y = Float.parseFloat(params.get("y"));
                int r = Integer.parseInt(params.get("r"));
                if (x < -4 | x > 4 | y < -3 | y > 5 | r < 1 | r > 5) {
                    System.out.println("Content-Type: text/plain\n");
                    System.out.println("Error: given arguments are incorrect");
                    continue;
                }

                boolean inRectangle = isInRectangle(0, r, -r/2, 0, x, y);
                boolean inTriangle = isInTriangle(0, r/2, 0, r, x, y);
                boolean inSector = isInSector(r, x, y);
                boolean inside = inRectangle | inSector | inTriangle;

                long executionTime = (System.nanoTime() - startTime);
                String serverTime = LocalDateTime.now().toString();

                String json = String.format(Locale.US,
                        "{ \"x\": %d, \"y\": %.2f, \"r\": %d, \"answer\": %b, " +
                                "\"executionTime\": %d, \"serverTime\": \"%s\" }",
                        x, y, r, inside, executionTime, serverTime);

                System.out.println("Content-Type: application/json\n");
                System.out.println(json);
            } catch (Exception e) {
                System.out.println("Content-Type: text/plain\n");
                System.out.println("Ошибка: " + e.getMessage());
            }
        }
    }

    private static boolean isInRectangle(int x1, int x2, int y1, float y2, int xR, float yR){
        return x1 <= xR & xR <= x2 & y1 <= yR & yR <= y2;
    }

    private static boolean isInTriangle(int x1, int x2, int y1, float y2, int xR, float yR){
        return isInRectangle(x1, x2, y1, y2, xR, yR) & yR / (x2 - xR) <= y2 / x2;
    }

    private static boolean isInSector(int r, int xR, float yR){
        return xR * xR + yR * yR <= r * r & xR <= 0 & yR <= 0;
    }
}
