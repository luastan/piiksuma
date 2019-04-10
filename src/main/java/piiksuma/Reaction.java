package piiksuma;

import piiksuma.database.MapperColumn;
import piiksuma.database.MapperTable;

@MapperTable
public class Reaction {
    @MapperColumn(columna = "usr", pkey = true)
    private User user;
    @MapperColumn(columna = "author", pkey = true)
    private User owner;
    @MapperColumn(pkey = true)
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

    /**
     * Function to check that the attributes with restriction 'not null' are not null
     *
     * @return the function return "true" if the attributes are not null, otherwise return "false"
     */
    public boolean checkNotNull() {
        // Check that the primary keys are not null
        if (!checkPrimaryKey()) {
            return false;
        }

        return true;
    }

    /**
     * Function to check that the primary keys are not null
     *
     * @return the function return "true" if the primary keys are not null, otherwise return "false"
     */
    public boolean checkPrimaryKey() {
        // Check that the primary keys are not null
        return getOwner() != null && getUser() != null && getPost() != null;

    }

    @Override
    public boolean equals(Object o){
        if (this == o) return true;
        if (!(o instanceof Reaction)) return false;

        Reaction reaction = (Reaction) o;

        return (reaction.getOwner().equals(getOwner()) && reaction.getPost().equals(getPost()) &&
                reaction.getUser().equals(getUser()));
    }
}
