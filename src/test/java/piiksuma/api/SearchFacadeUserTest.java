package piiksuma.api;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;
import piiksuma.*;
import piiksuma.exceptions.PiikDatabaseException;
import piiksuma.exceptions.PiikException;
import piiksuma.exceptions.PiikForbiddenException;
import piiksuma.exceptions.PiikInvalidParameters;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;

import static org.junit.Assert.*;
import static org.mockito.Mockito.*;

@RunWith(value = Parameterized.class)
public class SearchFacadeUserTest extends FacadeTest {

    private User current;
    private User subject;
    private Integer limiter;
    private Object exception;
    private static SearchFacade searchFacade = ApiFacade.getEntrypoint().getSearchFacade();

    public SearchFacadeUserTest(User current, User subject, Integer limiter, Object exception) {
        this.current = current;
        this.subject = subject;
        this.limiter = limiter;
        this.exception = exception;
    }

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
        if (subject != null && current != null) {
            when(mockedUserDao.getUserType(current.getEmail())).thenReturn(current.getType());
            when(mockedUserDao.getUserType(subject.getEmail())).thenReturn(subject.getType());
        }
    }

    @After
    public void tearDown() throws Exception {
        ApiFacade.getEntrypoint().setInteractionDao(oldInteractionDao);
        ApiFacade.getEntrypoint().setUserDao(oldUserDao);
        ApiFacade.getEntrypoint().setPostDao(oldPostDao);
        ApiFacade.getEntrypoint().setMultimediaDao(oldMultimediaDao);
        ApiFacade.getEntrypoint().setMessagesDao(oldMessagesDao);
    }


    @Parameterized.Parameters(name = "Expected: {3} - Subject :{0} - Current: {1} - Limit {2}")
    public static Collection<Object[]> data() {
        ArrayList<Object[]> tests = new ArrayList<>();
        for (Object user : new Object[]{normalUser, adminUser}) {
            tests.addAll(Arrays.asList(new Object[][]{
                    {null, null, 10, PiikInvalidParameters.class},
                    {null, user, 10, PiikInvalidParameters.class},
                    {user, null, 10, PiikInvalidParameters.class},
                    {user, user, -100, PiikInvalidParameters.class},
            }));
        }

        /*
        tests.addAll(Arrays.asList(new Object[][]{
                {normalUser, normalUser, 10, null},
                {normalUser, normalUser2, 10, null},
                {normalUser, adminUser, 10, null},
                {normalUser, adminUser2, 10, null},
                {normalUser, adminUser2, 10, null},
                {adminUser, normalUser, 10, null},
                {adminUser, normalUser2, 10, null},
                {adminUser, adminUser, 10, null},
                {adminUser, adminUser2, 10, null}
        }));
        */

        return tests;
    }

    @Test
    public void searchUser() throws PiikException {
        if (exception == null) {
            searchFacade.searchUser(subject, current, limiter);
            verify(mockedUserDao, atLeastOnce()).searchUser(subject, limiter);
        } else {
            try {
                searchFacade.searchUser(subject, current, limiter);
                fail();
            } catch (PiikInvalidParameters ignore) {}
        }
    }

    @Test
    public void getUser() throws PiikException {
        if (limiter < 0) {
            return;
        }

        if (exception == null) {
            searchFacade.getUser(subject, current);
            verify(mockedUserDao, atLeastOnce()).getUser(subject);
        } else {
            try {
                searchFacade.getUser(subject, current);
                fail();
            } catch (PiikInvalidParameters ignore) {}
        }
    }

    @Test
    public void getUserStatistics() throws PiikException {
        if (limiter < 0) {
            return;
        }

        if (exception == null) {
            searchFacade.getUserStatistics(subject, current);
            verify(mockedUserDao, atLeastOnce()).getUserStatistics(subject);
        } else {
            try {
                searchFacade.getUserStatistics(subject, current);
                fail();
            } catch (PiikInvalidParameters ignore) {}
        }
    }

    @Test
    public void getAchievement() throws PiikException {
        if (limiter < 0) {
            return;
        }

        if (exception == null) {
            searchFacade.getAchievement(subject, current);
            verify(mockedUserDao, atLeastOnce()).getAchievement(subject);
        } else {
            try {
                searchFacade.getAchievement(subject, current);
                fail();
            } catch (PiikInvalidParameters ignore) {}
        }
    }

    @Test
    public void existsMultimedia() throws PiikException {
        if (limiter < 0) {
            return;
        }
        Multimedia multimedia = new Multimedia();

        if (exception == null) {
            multimedia.setHash("NotNull");
            searchFacade.existsMultimedia(multimedia, current);
            verify(mockedMultimediaDao, atLeastOnce()).existsMultimedia(multimedia);
        } else {
            try {
                searchFacade.existsMultimedia(subject == null ? null : multimedia, current);
                fail();
            } catch (PiikInvalidParameters ignore) {}
        }
    }

    @Test
    public void numPostMultimedia() throws PiikException {
        if (limiter < 0) {
            return;
        }
        Multimedia multimedia = new Multimedia();
        if (exception == null) {
            multimedia.setHash("NotNull");
            searchFacade.numPostMultimedia(multimedia, current);
            verify(mockedMultimediaDao, atLeastOnce()).numPostMultimedia(multimedia);
        } else {
            try {
                searchFacade.numPostMultimedia(subject == null ? null : multimedia, current);
                fail();
            } catch (PiikInvalidParameters ignore) {}
        }
    }

    @Test
    public void postWithMultimedia() throws PiikException {
        if (limiter < 0) {
            return;
        }
        Multimedia multimedia = new Multimedia();
        if (exception == null) {
            multimedia.setHash("NotNull");
            searchFacade.postWithMultimedia(multimedia, current);
            verify(mockedMultimediaDao, atLeastOnce()).postWithMultimedia(multimedia);
        } else {
            try {
                searchFacade.postWithMultimedia(subject == null ? null : multimedia, current);
                fail();
            } catch (PiikInvalidParameters ignore) {}
        }
    }

    @Test
    public void getPost() throws PiikException {
        if (limiter < 0) {
            return;
        }
        Post post = new Post();
        if (exception == null) {
            post.setId("NotNull");
            post.setPostAuthor(subject.getEmail());
            searchFacade.getPost(post, current);
            verify(mockedPostDao, atLeastOnce()).getPost(post);
        } else {
            try {
                searchFacade.getPost(subject == null ? null : post, current);
                fail();
            } catch (PiikInvalidParameters ignore) {}
        }
    }

    @Test
    public void getPostsUser() throws PiikException {
        if (limiter < 0) {
            return;
        }

        if (exception == null) {
            searchFacade.getPost(subject, current);
            verify(mockedPostDao, atLeastOnce()).getPost(subject);
        } else {
            try {
                searchFacade.getPost(subject, current);
                fail();
            } catch (PiikInvalidParameters ignore) {}
        }
    }

    @Test
    public void getPostsHashtag() throws PiikException {
        if (limiter < 0) {
            return;
        }
        Hashtag hashtag = new Hashtag();
        if (exception == null) {
            hashtag.setName("NotNull");
            searchFacade.getPost(hashtag, current);
            verify(mockedPostDao, atLeastOnce()).getPost(hashtag);
        } else {
            try {
                searchFacade.getPost(hashtag, current);
                fail();
            } catch (PiikInvalidParameters ignore) {}
        }
    }

    @Test
    public void getHashtag() throws PiikException {
        if (limiter < 0) {
            return;
        }
        Hashtag hashtag = new Hashtag();
        if (exception == null) {
            hashtag.setName("NotNull");
            searchFacade.getHashtag(hashtag, current);
            verify(mockedPostDao, atLeastOnce()).getHashtag(hashtag);
        } else {
            try {
                searchFacade.getHashtag(hashtag, current);
                fail();
            } catch (PiikInvalidParameters ignore) {}
        }
    }

    @Test
    public void searchHashtag() throws PiikException {
        Hashtag hashtag = new Hashtag();
        if (exception == null) {
            hashtag.setName("NotNull");
            searchFacade.searchHashtag(hashtag, limiter, current);
            verify(mockedPostDao, atLeastOnce()).searchHashtag(hashtag, limiter);
        } else {
            try {
                searchFacade.searchHashtag(hashtag, limiter, current);
                fail();
            } catch (PiikInvalidParameters ignore) {}
        }
    }

    @Test
    public void searchByText() throws PiikException {
        String text = "QUEEERY";
        if (exception == null) {
            searchFacade.searchByText(text, limiter, current);
            verify(mockedPostDao, atLeastOnce()).searchByText(text, limiter);
        } else {
            try {
                searchFacade.searchByText(text, limiter, current);
                fail();
            } catch (PiikInvalidParameters ignore) {}
        }
    }

    @Test
    public void getFeed() throws PiikException {
        if (exception == null) {
            searchFacade.getFeed(subject, limiter, current);
            verify(mockedPostDao, atLeastOnce()).getFeed(subject, limiter);
        } else {
            try {
                searchFacade.getFeed(subject, limiter, current);
                fail();
            } catch (PiikInvalidParameters ignore) {}
        }
    }

    @Test
    public void getArchivedPosts() throws PiikException {
        if (limiter < 0) {
            return;
        }

        if (exception == null) {
            searchFacade.getArchivedPosts(subject, current);
            verify(mockedPostDao, atLeastOnce()).getArchivedPosts(subject);
        } else {
            try {
                searchFacade.getArchivedPosts(subject, current);
                fail();
            } catch (PiikInvalidParameters ignore) {}
        }
    }

    @Test
    public void getTrendingTopics() throws PiikException {
        if (exception == null) {
            searchFacade.getTrendingTopics(limiter, current);
            verify(mockedPostDao, atLeastOnce()).getTrendingTopics(limiter);
        } else {
            try {
                if (subject == null) {
                    return;
                }
                searchFacade.getTrendingTopics(limiter, current);
                fail();
            } catch (PiikInvalidParameters ignore) {}
        }
    }

    @Test
    public void getAdminTickets() throws PiikException {
        if (exception == null) {
            if (mockedUserDao.getUserType(current) == UserType.user) {
                try {
                    searchFacade.getAdminTickets(limiter, current);
                    fail();
                } catch (PiikForbiddenException ignore) {
                    return;
                }
            }
            searchFacade.getAdminTickets(limiter, current);
        } else {
            if (subject == null) {
                return;
            }
            if (current.getType() == UserType.user) {
                try {
                    searchFacade.getAdminTickets(limiter, current);
                    fail();
                } catch (PiikForbiddenException ignore) {
                }
                return;
            }
            try {
                searchFacade.getAdminTickets(limiter, current);
                fail();
            } catch (PiikInvalidParameters ignore) {
            }

        }
    }

    @Test
    public void getPostReaction() throws PiikException {
        if (limiter < 0) {
            return;
        }
        Post post = new Post();
        if (exception == null) {
            post.setId("NotNull");
            post.setPostAuthor(subject.getEmail());
            searchFacade.getPostReaction(post, current);
            verify(mockedInteractionDao, atLeastOnce()).getPostReaction(post);
        } else {
            try {
                searchFacade.getPostReaction(subject == null ? null : post, current);
                fail();
            } catch (PiikInvalidParameters ignore) {}
        }
    }

    @Test
    public void getNotifications() throws PiikException {
        if (limiter < 0) {
            return;
        }

        if (exception == null) {
            searchFacade.getNotifications(subject, current);
            verify(mockedInteractionDao, atLeastOnce()).getNotifications(subject);
        } else {
            try {
                searchFacade.getNotifications(subject, current);
                fail();
            } catch (PiikInvalidParameters ignore) {}
        }
    }
}