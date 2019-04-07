package piiksuma;

import piiksuma.database.MapperColumn;
import piiksuma.database.MapperTable;

@MapperTable
public class Reaction {
    @MapperColumn(columna = "usr")
    private User user;
    @MapperColumn(columna = "author")
    private User owner;
    @MapperColumn
    private Post post;
    @MapperColumn
    private ReactionType reactionType;

    public Reaction() {
    }

    public Reaction(User user, User owner, Post post, ReactionType reactionType) {
        this.user = user;
        this.owner = owner;
        this.post = post;
        this.reactionType = reactionType;
    }

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }

    public User getOwner() {
        return owner;
    }

    public void setOwner(User owner) {
        this.owner = owner;
    }

    public Post getPost() {
        return post;
    }

    public void setPost(Post post) {
        this.post = post;
    }

    public ReactionType getReactionType() {
        return reactionType;
    }

    public void setReactionType(ReactionType reactionType) {
        this.reactionType = reactionType;
    }
}
