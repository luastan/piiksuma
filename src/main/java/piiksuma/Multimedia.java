package piiksuma;

import piiksuma.database.MapperColumn;
import piiksuma.database.MapperTable;

@MapperTable
public class Multimedia {

    /* Attributes */
    @MapperColumn(pkey = true)
    private String hash;
    @MapperColumn
    private String resolution;
    @MapperColumn
    private String uri;


    /* Constructos */

    public Multimedia() {

    }

    public Multimedia(String hash, String resolution, String uri) {
        if (hash != null) {
            this.hash = hash;
        } else {
            this.hash = "";
        }

        if (resolution != null) {
            this.resolution = resolution;
        } else {
            this.resolution = "";
        }

        if (uri != null) {
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

    public boolean checkPrimaryKey(){
        if(hash==null || hash.isEmpty()){
            return false;
        }
        return true;
    }

    public boolean equals(Object o) {
        if (this == o) return true;
        if(!(o instanceof Multimedia)) return false;

        Multimedia multimedia = (Multimedia) o;

        return hash.equals(multimedia.hash);

    }
}
