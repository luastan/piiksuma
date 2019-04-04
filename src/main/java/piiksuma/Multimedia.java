package piiksuma;

public class Multimedia {

    /* Attributes */

    private String hash;
    private String resolution;
    private String uri;


    /* Constructos */

    public Multimedia() {

    }

    public Multimedia(String hash, String resolution, String uri) {
        if( hash != null ) {
            this.hash = hash;
        } else {
            this.hash = "";
        }

        if(resolution != null) {
            this.resolution = resolution;
        } else {
            this.resolution = "";
        }

        if(uri != null) {
            this.uri = uri;
        } else {
            this.uri = "";
        }
    }


    /* Getters and setters */
    
    public String getHash() {
        return hash;
    }

    public void setHash(String hash) {
        this.hash = hash;
    }

    public String getResolution() {
        return resolution;
    }

    public void setResolution(String resolution) {
        this.resolution = resolution;
    }

    public String getUri() {
        return uri;
    }

    public void setUri(String uri) {
        this.uri = uri;
    }
}
