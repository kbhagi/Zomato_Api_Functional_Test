import io.restassured.RestAssured;
import io.restassured.http.ContentType;
import io.restassured.response.Response;
import org.hamcrest.Matchers;
import org.testng.Assert;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;

import java.util.ArrayList;
import java.util.Arrays;

import static io.restassured.RestAssured.*;

import static org.hamcrest.Matchers.*;
import static org.hamcrest.core.IsCollectionContaining.hasItem;

/**
 * Created by Ramanuja on 11/23/2017.
 */
public class FunctionalTestCuisines {
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
    @Test // https://developers.zomato.com/api/v2.1/cities with api_key
    public void statusCode_With_Authentication()
    {
        given().header("user-key","85d1d03caf544fa516d6e5098336ece9").when().get(baseURI+basePath+rootPath).then().statusCode(200);

    }
    @Test // https://developers.zomato.com/api/v2.1/cities without api_key
    public void statusCode_Without_Authentication()
    {
        given().get(baseURI+basePath+rootPath).then().statusCode(403);
    }

    @Test // https://developers.zomato.com/api/v2.1/cities invalid api_key
    public void statusCode_Invalid_Authentication()
    {
        given().header("user-key","pass1234").when().get(baseURI+basePath+rootPath).then().statusCode(403);
    }

        // lat and long as double datatype, count as no of results, see if lat and long of a city , here Bangalore , matches the restaurants location , using restaurants.restaurant.location.city ="Bangalore".
        @Test
        public void List_All_Cuisines_All_Restaurants_In_A_City_Using_Lat_And_Lon_As_Valid_Input() {
        ArrayList<Response> response = given().header("user-key","85d1d03caf544fa516d6e5098336ece9").param("lat",12.972442).param("lon",77.580643).param("count",1).get(baseURI+basePath+rootPath).then().contentType(ContentType.JSON).extract().
                response().body().path("restaurants.restaurant.location.city");
            System.out.println(response.size() + " " +response.toString());
        Assert.assertEquals(response.size(),1);
           Assert.assertEquals(response.toString(),"[Bangalore]");
    }

        // https://developers.zomato.com/api/v2.1/search?cuisine_id=4&city_id=4&count=1


        //City Id and Cuisine Id a String Input , expecting cuisine arrayList to contain Arabian , Instead cuisines arrayList has "[North Indian, European, Mediterranean, BBQ]"
        @Test
        public void List_All_Cuisines_All_Restaurants_In_A_City_Using_CityId_And_CusineId_As_Valid_Input() {
            ArrayList<Response> cuisines = given().header("user-key","85d1d03caf544fa516d6e5098336ece9").param("city_id","4").param("count","1").param("cuisine_id","4").get(baseURI+basePath+rootPath).then().contentType(ContentType.JSON).extract().
                response().body().path("restaurants.restaurant.cuisines"); // returns a list of 3 names , store it in response ArrayList
             Assert.assertEquals(cuisines.toString(), hasItem("Arabian"));
             // Expected :a collection containing "Arabian" Actual   :[North Indian, European, Mediterranean, BBQ] TestCase failed

    }


                                 //      ************************Negative Test Scenarios***************************









    // lat and long as double datatype, give invalid inputs and see if you get a 400 Invalid Request or Bad Request https://developers.zomato.com/api/v2.1/cuisines?lat=300&lon=400
    @Test
    public void List_All_Cuisines_All_Restaurants_In_A_City_Using_Lat_And_Lon_As_InValid_Input() {
        String baseURI="https://developers.zomato.com"; // redeclare baseURI, basePath, rootPath as for this endpoint the path is different
        String basePath="/api/v2.1";
        String rootPath="/cuisines";
       String response_status = given().header("user-key","85d1d03caf544fa516d6e5098336ece9").param("lat",-500).param("lon",600).get(baseURI+basePath+rootPath).then().contentType(ContentType.JSON).extract().
                response().body().path("status","message");
        Assert.assertEquals(response_status,"Bad Request");

    }

    // https://developers.zomato.com/api/v2.1/search?cuisine_id=4&city_id=4&count=1


    //City Id and Cuisine Id a String Input , expecting cuisine arrayList to contain Arabian , Instead cuisines arrayList has "[North Indian, European, Mediterranean, BBQ]"
    @Test
    public void List_All_Cuisines_All_Restaurants_In_A_City_Using_CityId_And_CusineId_As_InValid_Input() {
        ArrayList<Response> cuisines = given().header("user-key","85d1d03caf544fa516d6e5098336ece9").param("city_id","$delete").param("count","2").param("cuisine_id","/put").get(baseURI+basePath+rootPath).then().contentType(ContentType.JSON).extract().
                response().body().path("restaurants.restaurant.cuisines"); // returns a default list of restaurants for invalid inputs params
        Assert.assertNotEquals(cuisines.size(),0);

        // Expected :a collection containing "Arabian" Actual   :[North Indian, European, Mediterranean, BBQ] Test-Case Pass





    }}
