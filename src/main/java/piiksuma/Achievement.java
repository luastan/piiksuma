package piiksuma;

import piiksuma.database.MapperColumn;
import piiksuma.database.MapperTable;

@MapperTable
public class Achievement {
    @MapperColumn(pkey = true)
    private String id;
    @MapperColumn
    private String name;
    @MapperColumn
    private String description;

    public Achievement() {
    }

    public Achievement(String id, String name, String description) {
        this.id = id;
        this.name = name;
        this.description = description;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Achievement)) return false;

        Achievement achievement = (Achievement) o;

        return getId().equals(achievement.getId());

    }
}
