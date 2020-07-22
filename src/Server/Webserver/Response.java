package Server.Webserver;

public class Response {

    private long length;
    private String header;
    private String body;
    private String fileType;
    private String status;

    public Response(String header, String body, String fileType, long length) {
        this.header = header;
        this.body = body;
        this.fileType = fileType;
        this.length = length;
    }

    public Response() {

    }

    public long getLength() {
        return length;
    }

    public void setLength(long length) {
        this.length = length;
    }

    public String getHeader() {
        return header;
    }

    public void setHeader(String header) {
        this.header = header;
    }

    public String getBody() {
        return body;
    }

    public void setBody(String body) {
        this.body = body;
    }

    public String getFileType() {
        return fileType;
    }

    public void setFileType(String fileType) {
        this.fileType = fileType;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }
}
