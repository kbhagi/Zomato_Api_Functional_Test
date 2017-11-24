import io.restassured.RestAssured;
import io.restassured.http.ContentType;
import io.restassured.response.Response;
import org.testng.Assert;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;

import java.util.ArrayList;

import static io.restassured.RestAssured.*;
import static org.hamcrest.CoreMatchers.hasItems;
import static org.hamcrest.Matchers.*;

/**
 * Created by Ramanuja on 11/23/2017.
 */
public class FunctionalTestsCategories {

    @BeforeClass
    public static void setup() {
        String port = System.getProperty("server.port");
        if (port == null) {
            RestAssured.port = Integer.valueOf(443);
        }
        else{
            RestAssured.port = Integer.valueOf(port);
        }

        String basePath = System.getProperty("server.base");
        if(basePath==null){
            basePath = "/api/v2.1";
        }
        RestAssured.basePath=basePath;

        String rootPath = System.getProperty("server.root");
        RestAssured.rootPath = rootPath;
        if(rootPath==null){
            RestAssured.rootPath="/search";
        }


        String baseHost = System.getProperty("server.host");
        if(baseHost==null){
            baseHost = "https://developers.zomato.com";
        }
        RestAssured.baseURI = baseHost;

    }
    @Test
    public void statusCode_With_Authentication()
    {
        System.out.println(baseURI+basePath+rootPath);
       given().header("user-key","85d1d03caf544fa516d6e5098336ece9").get(baseURI+basePath+rootPath).then().statusCode(200);
    }
    @Test
    public void statusCode_Without_Authentication()
    {
        System.out.println(baseURI+basePath+rootPath);
        given().get(baseURI+basePath+rootPath).then().statusCode(403);
    }
    @Test // https://developers.zomato.com/api/v2.1/search?category_id=3&category_name="Delivery"&count=2 , verifying the length of restaurants in this category , I get two restaurants with category_name & category_id as input
    public void List_Of_All_Categories_With_Category_Id_Category_Name_As_Input() {
        ArrayList<Response> response = given().header("user-key","85d1d03caf544fa516d6e5098336ece9").param("category_id",3).param("count",2).param("category_name","Delivery").get(baseURI+basePath+rootPath).then().contentType(ContentType.JSON).extract().
                response().body().path("restaurants.restaurant");
        Assert.assertEquals(response.size(),2);

                ;
    }


}
