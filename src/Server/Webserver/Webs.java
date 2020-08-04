package Server.Webserver;

import java.io.*;
import java.util.Scanner;

public class Webs {

    String dir;

    public Webs(String dir) {
    this.dir = dir;
    }
    private String parse(String text, String word, String regex) {

        String parts[];
        parts = text.split(regex);

        for (int i = 0; i < parts.length; i++) {
            if (parts[i].equals(word)) {
                return parts[i + 1];
            }
        }
        return null;
    }

    private String parse(String text, String regex) {

        System.out.println("Parsing: " + text);
        String parts[];
        parts = text.split(regex);

        if (parts.length > 1) {
            return parts[1];
        } else {
            return null;
        }
    }


    private void sendResponse(Response response, PrintWriter out) {

        String header =
                "HTTP/1.0 " + response.getStatus() + "\r\n " +
                        "Content-Length: " + response.getLength() + "\n" +
                        "Content-Type: " + response.getFileType() + "\r\n\r\n\r\n";


        out.print(header + response.getBody() + "\n");

    }

    private void setFileContent(String fileName, Response response) throws FileNotFoundException {

        File file = new File(dir + fileName);

        if (!file.exists()) {
            file = new File(dir + "/404.html");
            response.setStatus("404 Not Found");
        } else {
            response.setStatus("200 OK");
        }

        response.setLength(file.length());

        String output = "";

        if (response.getFileType().equals("image/jpeg")) {
            System.out.println("Jpg");
            FileReader reader = new FileReader(file);
            char[] image = new char[(int) response.getLength()];
            try {
                reader.read(image);
            } catch (IOException e) {
                e.printStackTrace();
            }
            output = image.toString();
        } else {

            Scanner scanner = new Scanner(file);


            while (scanner.hasNextByte()) {
                System.out.println("BYTE");
            }

            while (scanner.hasNextLine()) {
                output += scanner.nextLine();
            }
        }

        response.setBody(output);
    }

    private String determineFileType(String resource) {
        String fileType = parse(resource, "\\.");

        if (fileType == null) {
            return null;
        }
        String headerFileType;
        switch (fileType) {
            case ("jpg"): {
                headerFileType = "image/jpeg";
                break;
            }
            case ("html"): {
                headerFileType = "text/html";
                break;
            }
            default: {
                headerFileType = null;
                break;
            }
        }
        return headerFileType;
    }
    public void parseRequest(String request, PrintWriter out) throws FileNotFoundException {

        System.out.println("Raw request: " + request);
        String resource = (parse(request, "GET", " "));

        if (resource == null) {
            System.out.println("Invalid request");
            return;
        }

        if (resource.equals("/")) {
            resource = "/index.html";
        }

        System.out.println("Received request: " + resource);

        Response response = new Response();

        response.setFileType(determineFileType(resource));
        if (response.getFileType() == null) {
            return;
        }
        setFileContent(resource, response);


        if (response.getBody() == null) {
            sendResponse(response, out);
        } else {
            sendResponse(response, out);
        }
    }
}

