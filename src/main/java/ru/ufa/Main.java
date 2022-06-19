package ru.ufa;

import org.apache.http.HttpHeaders;
import org.apache.http.client.config.RequestConfig;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.entity.ContentType;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClientBuilder;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;

public class Main {

    public static final String URL = "https://api.nasa.gov/planetary/apod?api_key=UdV1W7XrpXtuD2ReghYmUWVUd174qRLhCcyNgn5d";
//    public static final ObjectMapper mapper = new ObjectMapper();

    public static void main(String[] args) throws IOException {

        CloseableHttpClient httpClient = HttpClientBuilder.create()
                .setDefaultRequestConfig(RequestConfig.custom()
                        .setConnectTimeout(5000)    // максимальное время ожидание подключения к серверу
                        .setSocketTimeout(30000)    // максимальное время ожидания получения данных
                        .setRedirectsEnabled(false) // возможность следовать редиректу в ответе
                        .build())
                .build();

        HttpGet request = new HttpGet(URL); //объект запроса
        request.setHeader(HttpHeaders.ACCEPT, ContentType.APPLICATION_JSON.getMimeType());

        CloseableHttpResponse response = httpClient.execute(request); //вызов удаленного сервиса
//        Arrays.stream(response.getAllHeaders()).forEach(System.out::println);

        String body = new String(response.getEntity().getContent().readAllBytes());
        System.out.println(body);

//        List<Nasa> nasaList = mapper.readValue(response.getEntity().getContent(), new TypeReference<List<Nasa>>() {});
//        nasaList.forEach(System.out::println);

//        Stream<Cats> stream = cats.stream();
//        stream.filter(value -> value.getUpvotes() > 0)
//                .forEach(System.out::println);

        fileCreate("jsonfile.json");
        fileWrite("jsonfile.json", body);

        String urlNasa = null;
        String dateNasa = null;
        String explanationNasa = null;
        String mediaTypeNasa = null;
        String serviceVersionNasa = null;
        String titleNasa = null;

        JSONParser parser = new JSONParser();
        try {
            Object obj = parser.parse(new FileReader("jsonfile.json"));
            JSONObject jsonObject = (JSONObject) obj;
            urlNasa = (String) jsonObject.get("url");
            dateNasa = (String) jsonObject.get("date");
            explanationNasa = (String) jsonObject.get("explanation");
            mediaTypeNasa = (String) jsonObject.get("media_type");
            serviceVersionNasa = (String) jsonObject.get("service_version");
            titleNasa = (String) jsonObject.get("title");
        } catch (ParseException e) {
            e.printStackTrace();
        }

        Nasa nasaObject = new Nasa(dateNasa, explanationNasa, mediaTypeNasa, serviceVersionNasa, titleNasa, urlNasa);

        System.out.println("json преобразованный в java-объект: " + nasaObject);
        System.out.println("\n URL из json ответа от сервера: " + urlNasa);


        HttpGet request2 = new HttpGet(urlNasa); // объект запроса
        request2.setHeader(HttpHeaders.ACCEPT, ContentType.APPLICATION_JSON.getMimeType());

        CloseableHttpResponse response2 = httpClient.execute(request2); //вызов удаленного сервиса
        String body2 = new String(response2.getEntity().getContent().readAllBytes());

        fileCreate("nasaResponse.txt");
        fileWrite("nasaResponse.txt", body2);
    }

    public static void fileCreate(String fileName) {
        File file = new File(fileName);
        try {
            if (file.createNewFile())
                System.out.println(file.getName() + " - Файл создан" + "\n");
        } catch (IOException ex) {
            System.out.println(ex.getMessage());
        }
    }

    public static void fileWrite(String fileName, String body) {
        try (FileWriter writer = new FileWriter(fileName, false)) {
//            writer.append('\n');
            writer.append(body);
            writer.flush();
        } catch (IOException ex) {
            System.out.println(ex.getMessage());
        }
    }
}