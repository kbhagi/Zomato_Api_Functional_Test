import io.restassured.RestAssured;
import io.restassured.http.ContentType;
import io.restassured.response.Response;
import org.testng.Assert;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;

import java.util.ArrayList;
import java.util.Arrays;

import static io.restassured.RestAssured.*;

/**
 * Created by Ramanuja on 11/23/2017.
 */
public class FunctionalTestCities {
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
            RestAssured.rootPath="/cities";
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


    // https://developers.zomato.com/api/v2.1/cities?q="bangalore", giving q="bangalore" and check if name has "Bangalore" we get only one object : location_suggestions" which has name:Bangalore
    @Test
    public void Find_Zomato_ID_And_Other_Details_Using_CityName_As_Input() {
        ArrayList<Response> response = given().header("user-key","85d1d03caf544fa516d6e5098336ece9").param("q","bangalore").get(baseURI+basePath+rootPath).then().contentType(ContentType.JSON).extract().
                response().body().path("location_suggestions.name");
       Assert.assertEquals(response.size(),1);
       Assert.assertEquals(response.toString(),"[Bangalore]");
        String response_status = given().header("user-key","85d1d03caf544fa516d6e5098336ece9").param("q","bangalore").get(baseURI+basePath+rootPath).then().contentType(ContentType.JSON).extract().
                response().body().path("status");
       Assert.assertEquals(response_status,"success");
    }



    // lat and long as double datatype,  expecting name:"Bangalore" in response , got one object Bangalore
    @Test
    public void Find_Zomato_ID_And_Other_Details_Using_Lat_And_Long_As_Input() {
        ArrayList<Response> response = given().header("user-key","85d1d03caf544fa516d6e5098336ece9").param("lat",12.972442).param("lon",77.580643).get(baseURI+basePath+rootPath).then().contentType(ContentType.JSON).extract().
                response().body().path("location_suggestions.name");
        Assert.assertEquals(response.size(),1);
        Assert.assertEquals(response.toString(),"[Bangalore]");
    }

    // city_ids as string datatype,  expecting name:"Bangalore,Hyderabad,Pune" in response , got 3 objects Bangalore
    @Test
    public void Find_Zomato_ID_And_Other_Details_Using_CityId_As_Input() {
        ArrayList<Response> response = given().header("user-key","85d1d03caf544fa516d6e5098336ece9").param("city_ids","4,5,6").get(baseURI+basePath+rootPath).then().contentType(ContentType.JSON).extract().
                response().body().path("location_suggestions.name"); // returns a list of 3 names , store it in response ArrayList
       Assert.assertEquals(response.size(),3); // check if size of ArrayList is 3
       ArrayList<String> city_names_list = new ArrayList<String>(Arrays.asList("Bangalore", "Pune","Hyderabad")); // declare an arraylist of three city names we get as response at name object
       Assert.assertEquals(response,city_names_list); // check if two arraylists have equals values
    }


    //      ************************Negative Test Scenarios***************************


    // city_ids as string datatype, input invalid city_ids = */ expecting :"0" in response , got 0 objects
    @Test
    public void Find_Zomato_ID_And_Other_Details_Using_CityId_As_Invalid_Input()

    {
        ArrayList<Response> response = given().header("user-key","85d1d03caf544fa516d6e5098336ece9").param("city_ids","*/").get(baseURI+basePath+rootPath).then().contentType(ContentType.JSON).extract().
                response().body().path("location_suggestions"); // returns a list of 3 names , store it in response ArrayList
        Assert.assertNotEquals(response.size(),3); // check if size of ArrayList is 3
    }


   //  https://developers.zomato.com/api/v2.1/cities?q=nammooru
    @Test
    public void Find_Zomato_ID_And_Other_Details_Using_CityName_As_Invalid_Input() {
        ArrayList<Response> response = given().header("user-key","85d1d03caf544fa516d6e5098336ece9").param("q","nammooru").get(baseURI+basePath+rootPath).then().contentType(ContentType.JSON).extract().
                response().body().path("location_suggestions.name");
        Assert.assertEquals(response.size(),0);

    }


    //  https://developers.zomato.com/api/v2.1/cities?long=-600&lat=500
    @Test
    public void Find_Zomato_ID_And_Other_Details_Using_Lat_And_Long_As_Invalid_Input_Input() {
        ArrayList<Response> response = given().header("user-key","85d1d03caf544fa516d6e5098336ece9").param("lat",500).param("lon",-600).get(baseURI+basePath+rootPath).then().contentType(ContentType.JSON).extract().
                response().body().path("location_suggestions.name");
        Assert.assertEquals(response.size(),0);

    }



}
