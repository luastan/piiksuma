-- ==================================================== DATA USERS =====================================================
-- Piiuser
INSERT INTO piiuser (id, email, name, pass, gender, description, home,
                     postalcode, province, country, city, birthdate, registrationdate)
VALUES ('usr1', 'usr1@gmail.com', 'user1',
        'LhRjG6dPpDNSOqBvE/+KTA==$4Ne3Q+T6DrXeIZCSAAo1GBeuot7+krcQjoC5cf2B4OfqAcwl5ne6tFJtKSMZiR4IqpnckHxw0vutdAokJVbGew==',
        'M', 'My bio is super  cool',
        'somewhere', '231', 'someProvince', 'someCountry', 'someCity', '2019-04-12', '2019-04-12');

INSERT INTO piiuser (id, email, name, pass, gender, description, home,
                     postalcode, province, country, city, birthdate, registrationdate)
VALUES ('usr2', 'usr2@gmail.com', 'user2',
        'LhRjG6dPpDNSOqBvE/+KTA==$4Ne3Q+T6DrXeIZCSAAo1GBeuot7+krcQjoC5cf2B4OfqAcwl5ne6tFJtKSMZiR4IqpnckHxw0vutdAokJVbGew==',
        'M', 'dasdasd',
        'somewhere', '231', 'someProvince', 'someCountry', 'someCity', '2019-04-12', '2019-04-12');

INSERT INTO piiuser (id, email, name, pass, gender, description, home,
                     postalcode, province, country, city, birthdate, registrationdate)
VALUES ('usr3', 'usr3@gmail.com', 'user3', 'user3', 'M', 'dasdasd',
        'somewhere', '231', 'someProvince', 'someCountry', 'someCity', '2019-04-12', '2019-04-12');

INSERT INTO piiuser (id, email, name, pass, gender, description, home,
                     postalcode, province, country, city, birthdate, registrationdate)
VALUES ('usr4', 'usr4@gmail.com', 'user4', 'user4', 'M', 'dasdasd',
        'somewhere', '231', 'someProvince', 'someCountry', 'someCity', '2019-04-12', '2019-04-12');

-- Admin
INSERT INTO administrator (id)
VALUES ('usr1');

-- Associated account
INSERT INTO associatedaccount (id, token, usr)
VALUES ('aaaa', 'tokenaaaa', 'usr1');

INSERT INTO associatedaccount (id, token, usr)
VALUES ('aaab', 'tokenaaaab', 'usr2');

INSERT INTO associatedaccount (id, token, usr)
VALUES ('aaac', 'tokenaaac', 'usr3');

INSERT INTO associatedaccount (id, token, usr)
VALUES ('aaad', 'tokenaaad', 'usr1');

-- Blocked Users
INSERT INTO blockuser (usr, blocked)
VALUES ('usr1', 'usr4');

-- Follow Users
INSERT INTO followuser (followed, follower)
VALUES ('usr2', 'usr1');

INSERT INTO followuser (followed, follower)
VALUES ('usr3', 'usr2');

INSERT INTO followuser (followed, follower)
VALUES ('usr1', 'usr3');

INSERT INTO followuser (followed, follower)
VALUES ('usr1', 'usr4');

-- Phone
INSERT INTO phone (prefix, phone, usr)
VALUES ('+34', '608564255', 'usr1');

INSERT INTO phone (prefix, phone, usr)
VALUES ('+34', '698281823', 'usr2');

INSERT INTO phone (prefix, phone, usr)
VALUES ('+34', '613224123', 'usr3');

INSERT INTO phone (prefix, phone, usr)
VALUES ('+34', '655110033', 'usr1');

-- Silence User
INSERT INTO silenceuser (usr, silenced)
VALUES ('usr1', 'usr4');
-- =====================================================================================================================

-- ==================================================== DATA POSTS =====================================================
-- Post
INSERT INTO post (author, text, publicationdate)
VALUES ('usr1', 'Hi! My name is... what?', '2019-05-12');

INSERT INTO post (author, text, publicationdate)
VALUES ('usr2', 'My name is... who?', '2019-05-12');

INSERT INTO post (author, text, publicationdate)
VALUES ('usr3', 'My name is... Slim Shady ?', '2019-05-12');

INSERT INTO post (author, text, publicationdate)
VALUES ('usr4',
        'Lorem ipsum dolor sit amet, consectetur adipiscing elit, sed do eiusmod tempor incididunt ut labore et ' ||
        'dolore magna aliqua. Ut enim ad minim veniam, quis nostrud exercitation ullamco laboris nisi ut aliquip ' ||
        'ex ea commodo consequat.',
        '2019-05-12');

INSERT INTO post (author, text, publicationdate)
VALUES ('usr4', 'Hi kids! Do you like violence?', '2019-05-12');

-- React to post
INSERT INTO react (reactiontype, post, usr, author)
SELECT 'likeit', id, 'usr2', author
FROM post
ORDER BY id
LIMIT 1 OFFSET 0;

INSERT INTO react (reactiontype, post, usr, author)
SELECT 'hateit', id, 'usr3', author
FROM post
ORDER BY id
LIMIT 1 OFFSET 1;

INSERT INTO react (reactiontype, post, usr, author)
SELECT 'makesmeangry', id, 'usr4', author
FROM post
ORDER BY id
LIMIT 1 OFFSET 2;

INSERT INTO react (reactiontype, post, usr, author)
SELECT 'loveit', id, 'usr4', author
FROM post
ORDER BY id
LIMIT 1 OFFSET 3;

INSERT INTO react (reactiontype, post, usr, author)
SELECT 'loveit', id, 'usr1', author
FROM post
ORDER BY id
LIMIT 1 OFFSET 4;


-- Repost
INSERT INTO repost (post, usr, author)
SELECT id, 'usr1', author
FROM post
ORDER BY id
LIMIT 1 OFFSET 0;


INSERT INTO repost (post, usr, author)
SELECT id, 'usr2', author
FROM post
ORDER BY id
LIMIT 1 OFFSET 1;


INSERT INTO repost (post, usr, author)
SELECT id, 'usr3', author
FROM post
ORDER BY id
LIMIT 1 OFFSET 2;


INSERT INTO repost (post, usr, author)
SELECT id, 'usr1', author
FROM post
ORDER BY id
LIMIT 1 OFFSET 3;


INSERT INTO repost (post, usr, author)
SELECT id, 'usr2', author
FROM post
ORDER BY id
LIMIT 1 OFFSET 4;

-- Archive post
INSERT INTO archivePost (post, usr, author)
SELECT id, 'usr4', author
FROM post
ORDER BY id
LIMIT 1 OFFSET 0;

INSERT INTO archivePost (post, usr, author)
SELECT id, 'usr2', author
FROM post
ORDER BY id
LIMIT 1 OFFSET 1;

INSERT INTO archivePost (post, usr, author)
SELECT id, 'usr1', author
FROM post
ORDER BY id
LIMIT 1 OFFSET 2;

INSERT INTO archivePost (post, usr, author)
SELECT id, 'usr3', author
FROM post
ORDER BY id
LIMIT 1 OFFSET 3;

INSERT INTO archivePost (post, usr, author)
SELECT id, 'usr2', author
FROM post
ORDER BY id
LIMIT 1 OFFSET 4;

-- =====================================================================================================================

-- =================================================== DATA EVENTS =====================================================
INSERT INTO event (name, description, location, date, author)
VALUES ('USC party', 'Party for all usc students', 'USC campus',
        '2019-04-23', 'usr1');

INSERT INTO event (name, description, location, date, author)
VALUES ('Suprise exam USC', 'Dont tell the teachers', 'USC campus',
        '2019-04-29', 'usr2');

INSERT INTO event (name, description, location, date, author)
VALUES ('Test1', 'Is late i dont have any more ideas', 'Home if where your heart is',
        '2019-06-29', 'usr2');

INSERT INTO event (name, description, location, date, author)
VALUES ('Test2', 'things', 'Santiago de Compostela',
        '2019-07-10', 'usr1');

-- Participate Event
INSERT INTO participateevent (event, eventauthor, usr)
SELECT id, author, 'usr1'
FROM event
ORDER BY id
LIMIT 1 OFFSET 0;

INSERT INTO participateevent (event, eventauthor, usr)
SELECT id, author, 'usr3'
FROM event
ORDER BY id
LIMIT 1 OFFSET 1;

INSERT INTO participateevent (event, eventauthor, usr)
SELECT id, author, 'usr1'
FROM event
ORDER BY id
LIMIT 1 OFFSET 2;

INSERT INTO participateevent (event, eventauthor, usr)
SELECT id, author, 'usr1'
FROM event
ORDER BY id
LIMIT 1 OFFSET 3;
-- =====================================================================================================================

-- ================================================ DATA ACHIVEMENTS ===================================================
-- Achievement
INSERT INTO achievement (name, description)
VALUES ('First follow', 'You have successfully follow someone');

INSERT INTO achievement (name, description)
VALUES ('First follower', 'You have been followed for the first time');

INSERT INTO achievement (name, description)
VALUES ('10 posts', 'you have published 10 posts');

INSERT INTO achievement (name, description)
VALUES ('100 posts', 'you have published 100 posts');

-- Own Achivement
INSERT INTO ownachievement (achiev, usr, acquisitiondate)
SELECT id, 'usr1', CURRENT_DATE
FROM achievement
ORDER BY id
LIMIT 1 OFFSET 0;

INSERT INTO ownachievement (achiev, usr, acquisitiondate)
SELECT id, 'usr2', CURRENT_DATE
FROM achievement
ORDER BY id
LIMIT 1 OFFSET 1;

INSERT INTO ownachievement (achiev, usr, acquisitiondate)
SELECT id, 'usr3', CURRENT_DATE
FROM achievement
ORDER BY id
LIMIT 1 OFFSET 2;


INSERT INTO ownachievement (achiev, usr, acquisitiondate)
SELECT id, 'usr1', CURRENT_DATE
FROM achievement
ORDER BY id
LIMIT 1 OFFSET 3;
-- =====================================================================================================================

-- ================================================= DATA HASHTAGS =====================================================
-- Hastag
INSERT INTO hashtag (name)
VALUES ('test');

INSERT INTO hashtag (name)
VALUES ('ThisProyectIsA10');

-- Own Hastag
INSERT INTO ownhashtag (hashtag, post, author)
SELECT 'ThisProyectIsA10', id, author
FROM post
ORDER BY id
LIMIT 1 OFFSET 0;

INSERT INTO ownhashtag (hashtag, post, author)
SELECT 'test', id, author
FROM post
ORDER BY id
LIMIT 1 OFFSET 1;


-- Follow Hashtag
INSERT INTO followhashtag (piiuser, hashtag)
VALUES ('usr1', 'test');

INSERT INTO followhashtag (piiuser, hashtag)
VALUES ('usr2', 'ThisProyectIsA10');

INSERT INTO followhashtag (piiuser, hashtag)
VALUES ('usr3', 'test');

INSERT INTO followhashtag (piiuser, hashtag)
VALUES ('usr4', 'ThisProyectIsA10');
-- =====================================================================================================================

-- ================================================ DATA MULTIMEDIA ====================================================
-- Multimedia
INSERT INTO multimedia (hash, resolution, uri)
VALUES ('aaaa', '1080p', 'here');

INSERT INTO multimedia (hash, resolution, uri)
VALUES ('aaab', '1080p', 'here');

INSERT INTO multimedia (hash, resolution, uri)
VALUES ('aaac', '1080p', 'here');

INSERT INTO multimedia (hash, resolution, uri)
VALUES ('aaad', '1080p', 'here');

-- Multimedia Image
INSERT INTO multimediaimage (hash)
VALUES ('aaaa');

INSERT INTO multimediaimage (hash)
VALUES ('aaab');

-- Multimedia Video
INSERT INTO multimediavideo (hash)
VALUES ('aaac');

INSERT INTO multimediavideo (hash)
VALUES ('aaad');
-- =====================================================================================================================

-- ================================================== DATA TICKET ======================================================
-- Ticket
INSERT INTO ticket (usr, section, text, creationdate)
VALUES ('usr1', 'easyGameEasyLife', 'dont know what to say', CURRENT_DATE);

-- =====================================================================================================================

-- ================================================= DATA MESSAGES =====================================================
-- Message
INSERT INTO message (author, text, date, multimedia)
VALUES ('usr4', 'Is this the real life?', '2019-04-10', 'aaaa');
INSERT INTO message (author, text, date, multimedia)
VALUES ('usr3', 'Is this the real life?', '2019-04-11', 'aaaa');
INSERT INTO message (author, text, date, multimedia, ticket)
VALUES ('usr2', 'Is this the real life?', '2019-04-12', 'aaaa', 1);
INSERT INTO message (author, text, date, multimedia)
VALUES ('usr1', 'Is this the real life?', '2019-04-13', 'aaad');

-- Recieve Message
INSERT INTO receivemessage (message, author, receiver)
SELECT id, author, 'usr1'
FROM message
ORDER BY message
LIMIT 1 OFFSET 0;

INSERT INTO receivemessage (message, author, receiver)
SELECT id, author, 'usr2'
FROM message
ORDER BY message
LIMIT 1 OFFSET 1;

INSERT INTO receivemessage (message, author, receiver)
SELECT id, author, 'usr3'
FROM message
ORDER BY message
LIMIT 1 OFFSET 2;

INSERT INTO receivemessage (message, author, receiver)
SELECT id, author, 'usr4'
FROM message
ORDER BY message
LIMIT 1 OFFSET 3;
-- =====================================================================================================================

-- =============================================== DATA NOTIFICATIONS ==================================================
-- Notification
INSERT INTO notification (creationdate, content)
VALUES ('2019-04-12', 'Viqueira sais: 4.9');

INSERT INTO notification (creationdate, content)
VALUES ('2019-04-11', 'Easy game easy life');

INSERT INTO notification (creationdate, content)
VALUES ('2019-04-10', 'Hi');

INSERT INTO notification (creationdate, content)
VALUES ('2019-04-10', 'No sleep');

-- Have Notification
INSERT INTO havenotification (notification, usr)
SELECT id, 'usr1'
FROM notification
ORDER BY id
LIMIT 1 OFFSET 0;

INSERT INTO havenotification (notification, usr)
SELECT id, 'usr2'
FROM notification
ORDER BY id
LIMIT 1 OFFSET 1;

INSERT INTO havenotification (notification, usr)
SELECT id, 'usr3'
FROM notification
ORDER BY id
LIMIT 1 OFFSET 2;

INSERT INTO havenotification (notification, usr)
SELECT id, 'usr4'
FROM notification
ORDER BY id
LIMIT 1 OFFSET 3;
-- =====================================================================================================================