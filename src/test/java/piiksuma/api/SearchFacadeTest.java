package piiksuma.api;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import static org.junit.Assert.*;

public class SearchFacadeTest extends FacadeTest {

    @Before
    public void setUp() throws Exception {
        oldUserDao = ApiFacade.getEntrypoint().getUserDao();
        oldPostDao = ApiFacade.getEntrypoint().getPostDao();
        oldInteractionDao = ApiFacade.getEntrypoint().getInteractionDao();
        oldMultimediaDao = ApiFacade.getEntrypoint().getMultimediaDao();
        oldMessagesDao = ApiFacade.getEntrypoint().getMessagesDao();

        ApiFacade.getEntrypoint().setInteractionDao(mockedInteractionDao);
        ApiFacade.getEntrypoint().setUserDao(mockedUserDao);
        ApiFacade.getEntrypoint().setPostDao(mockedPostDao);
        ApiFacade.getEntrypoint().setMultimediaDao(mockedMultimediaDao);
        ApiFacade.getEntrypoint().setMessagesDao(mockedMessagesDao);
    }

    @After
    public void tearDown() throws Exception {
        ApiFacade.getEntrypoint().setInteractionDao(oldInteractionDao);
        ApiFacade.getEntrypoint().setUserDao(oldUserDao);
        ApiFacade.getEntrypoint().setPostDao(oldPostDao);
        ApiFacade.getEntrypoint().setMultimediaDao(oldMultimediaDao);
        ApiFacade.getEntrypoint().setMessagesDao(oldMessagesDao);
    }


    @Test
    public void getConnection() {
    }

    @Test
    public void setConnection() {
    }

    @Test
    public void searchUser() {
    }

    @Test
    public void getUser() {
    }

    @Test
    public void getUserStatistics() {
    }

    @Test
    public void getAchievement() {
    }

    @Test
    public void existsMultimedia() {
    }

    @Test
    public void numPostMultimedia() {
    }

    @Test
    public void postWithMultimedia() {
    }

    @Test
    public void getPost() {
    }

    @Test
    public void getPost1() {
    }

    @Test
    public void getPost2() {
    }

    @Test
    public void getHashtag() {
    }

    @Test
    public void searchHashtag() {
    }

    @Test
    public void searchByText() {
    }

    @Test
    public void getFeed() {
    }

    @Test
    public void getArchivedPosts() {
    }

    @Test
    public void getTrendingTopics() {
    }

    @Test
    public void getAdminTickets() {
    }

    @Test
    public void getPostReaction() {
    }

    @Test
    public void getNotifications() {
    }
}