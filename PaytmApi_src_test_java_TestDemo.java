import io.restassured.RestAssured;
import io.restassured.http.Method;
import io.restassured.response.Response;
import io.restassured.path.json.JsonPath;
import io.restassured.specification.RequestSpecification;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.xssf.usermodel.XSSFRow;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.junit.Assert;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.stream.Collectors;

import org.testng.asserts.SoftAssert;


public class TestDemo {
    List<String> contents = new ArrayList<String>();
    Date date = new Date();
    SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd");
    String strDate = formatter.format(date);


    public void getData() throws IOException, ParseException {
        date = formatter.parse(strDate);
        RestAssured.baseURI = "https://apiproxy.paytm.com/v2/movies/upcoming";
        RequestSpecification httprequest = RestAssured.given();
        httprequest.header("Content-Type", "application/json");
        Response response = httprequest.request(Method.GET);
        String responseBody = response.getBody().asString();
        int status = response.getStatusCode();
        System.out.println(status);
        Assert.assertEquals(200, status);

        SoftAssert softassert = new SoftAssert();


        List<Map<String, Object>> movieData = response.jsonPath().getList("upcomingMovieData");

        for (Map<String, Object> data : movieData) {

            for (Map.Entry<String, Object> entry : data.entrySet()) {

                if (entry.getKey().equals("releaseDate")) {

                    softassert.assertEquals(date(entry.getValue()), true, "Date is not upcoming");

                }
                else if (entry.getKey().equals("moviePosterUrl")) {
                    boolean url = entry.getValue().toString().contains(".jpg");
                    softassert.assertEquals(url, true, "Not JPG Format");
                }
                else if (entry.getKey().equals("paytmMovieCode")) {

                    int counter=0;
                    for (Map.Entry<String, Object> code : data.entrySet()) {

                        if (code.getKey().equals("paytmMovieCode"))
                        {
                            if(code.getValue().toString().equals(entry.getValue().toString()))
                                counter++;
                        }

                    }

                    softassert.assertEquals(counter, 1, "Code is not unique");

                }
                else if (entry.getKey().equals("language")) {
                    softassert.assertEquals((entry.getValue().toString().equals("English") || entry.getValue().toString().equals("Hindi") || entry.getValue().toString().equals("Kannada")), true, "Language does not match or Multiple Language Found");
                }
                else if (entry.getKey().equals("isContentAvailable")) {
                    if (entry.getValue().toString().equals("0")) {


                        for (Map.Entry<String, Object> map : data.entrySet()) {

                            if(map.getKey().equals("movie_name"))
                                contents.add(map.getValue().toString());


                        }

                    }


                }


            }


        }
        writeData();
        softassert.assertAll();

    }

    public static void main(String[] s) throws IOException, ParseException {
        TestDemo ob = new TestDemo();
        ob.getData();

    }

    public boolean date(Object strdate) throws ParseException {
        if (strdate == (null) || strdate.equals(" "))
            return false;

        SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd");
        Date date1 = formatter.parse(strdate.toString());


        if (date1.after(date))
            return true;

        return false;
    }

    public void writeData() throws IOException {
        FileOutputStream fos = new FileOutputStream(new File(".\\Content.xlsx"));
        XSSFWorkbook book = new XSSFWorkbook();
        XSSFSheet sheet = book.createSheet("ZeroContent");
        XSSFRow row;
        int rowid = 0;


        for (String content : contents) {
            int cellid = 0;
            row = sheet.createRow(rowid++);

            Cell cell = row.createCell(cellid++);
            cell.setCellValue(content);


        }


        book.write(fos);
        fos.close();
    }

}
