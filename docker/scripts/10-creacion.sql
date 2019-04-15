/*

Script de creación de tablas para Piiksuma

Authors:
    @luastan
    @danimf99
    @CardamaS99
    @alvrogd
    @Marcos-marpin
    @OswaldOswin1

Source Code:
      https://github.com/luastan/piiksuma
*/


/*
      Multimedia
*/

CREATE TABLE multimedia
(
    hash       varchar(256) primary key,
    resolution varchar(10) not null,
    uri        varchar(50)
);

CREATE TABLE multimediaImage
(
    hash varchar(256) not null primary key references multimedia (hash)
        on delete cascade on update cascade
);

CREATE TABLE multimediaVideo
(
    hash varchar(256) not null primary key references multimedia (hash)
        on delete cascade on update cascade
);


/*
      Usuarios
*/

CREATE TABLE piiUser
(
    id                 varchar(32)             not null unique,
    email              varchar(35) primary key,
    name               varchar(35)             not null,
    pass               varchar(256)            not null,
    gender             char(1) CHECK (gender IN ('H', 'M', 'O')),
    description        varchar(256),
    home               varchar(20),
    postalCode         varchar(5),
    province           varchar(30),
    country            varchar(30),
    city               varchar(30),
    birthplace         varchar(30),
    birthdate          timestamp               not null,
    registrationDate   timestamp default now() not null,
    deathdate          timestamp,
    religion           varchar(20),
    emotionalSituation varchar(20),
    job                varchar(35),

    -- Aqui hay que pensar como va a funcionar por defecto
    profilePicture     varchar(32) references multimediaImage (hash)
        on delete set null on update cascade
);

CREATE TABLE phone
(
    prefix varchar(3), -- todo actualizar los demás
    phone  varchar(9),
    usr    varchar(32),

    primary key (phone, usr, prefix),
    foreign key (usr) references piiUser (email)
        on delete cascade on update cascade
);

CREATE TABLE administrator
(
    id varchar(32) primary key references piiUser (email)
        on delete set null on update cascade
);

CREATE TABLE associatedAccount
(
    id    varchar(32) not null primary key,
    token varchar(128), -- todo vamos a meter el token al final?
    usr   varchar(32),

    foreign key (usr) references piiUser (email)
        on delete cascade on update cascade
);


/*
      Tickets
*/

CREATE TABLE ticket
(
    id           varchar(32),
    usr          varchar(32)  not null,
    section      varchar(20)  not null,
    text         varchar(200) not null,
    creationDate timestamp    not null default now(),
    closeDate    timestamp,
    adminClosing varchar(32),
    primary key (id),
    foreign key (usr) references piiUser (email) on delete set null on update cascade,
    foreign key (adminClosing) references administrator (id) on delete set null on update cascade
);


/*
      Interacciones
*/

CREATE TABLE followUser
(
    followed varchar(32),
    follower varchar(32),

    primary key (followed, follower),
    foreign key (followed) references piiUser (email)
        on delete cascade on update cascade,
    foreign key (follower) references piiUser (email)
        on delete cascade on update cascade
);

CREATE TABLE silenceUser
(
    usr      varchar(32),
    silenced varchar(32),

    primary key (usr, silenced),
    foreign key (usr) references piiUser (email)
        on delete cascade on update cascade,
    foreign key (silenced) references piiUser (email)
        on delete cascade on update cascade
);

CREATE TABLE blockUser
(
    usr     varchar(32),
    blocked varchar(32),

    primary key (usr, blocked),
    foreign key (usr) references piiUser (email)
        on delete cascade on update cascade,
    foreign key (blocked) references piiUser (email)
        on delete cascade on update cascade
);

CREATE TABLE message
(
    id         varchar(32),
    sender     varchar(32),
    text       varchar(200) not null,
    date       timestamp    not null default now(),
    multimedia varchar(32),
    ticket     varchar(32),

    primary key (sender, id),
    foreign key (sender) references piiUser (email)
        on delete cascade on update cascade,
    foreign key (multimedia) references multimedia (hash)
        on delete set null on update cascade,
    foreign key (ticket) references ticket (id)
);

CREATE TABLE receiveMessage
(
    message  varchar(32),
    sender   varchar(32),
    receiver varchar(32),

    primary key (message, sender, receiver),
    foreign key (message, sender) references message (id, sender)
        on delete cascade on update cascade,
    foreign key (receiver) references piiUser (email)
        on delete cascade on update cascade
);

CREATE TABLE post
(
    author          varchar(32),
    id              varchar(32),
    text            varchar(256)            not null,
    publicationDate timestamp default now() not null,
    sugarDaddy      varchar(32),
    authorDaddy     varchar(32),
    multimedia      varchar(32),

    primary key (id, author),
    foreign key (author) references piiUser (email),
    foreign key (sugarDaddy, authorDaddy) references post (id, author),
    foreign key (multimedia) references multimedia (hash)
);


/*
      Hashtags
*/

CREATE TABLE hashtag
(
    name varchar(256) not null primary key
);


CREATE TABLE ownHashtag
(
    hashtag varchar(256),
    post    varchar(32),
    author  varchar(32),

    primary key (hashtag, post, author),
    foreign key (post, author) references post (id, author) on delete cascade on update cascade
);

CREATE TABLE followHashtag
(
    piiUser varchar(32),
    hashtag varchar(256),

    primary key (piiUser, hashtag),
    foreign key (piiUser) references piiUser (email)
        on delete cascade on update cascade,
    foreign key (hashtag) references hashtag (name)
        on delete cascade on update cascade
);


/*
      Archivado, Reacciones y Repost
*/

CREATE TABLE archivePost
(
    post   varchar(32),
    usr    varchar(32),
    author varchar(32),

    primary key (author, post, usr),
    foreign key (post, author) references post (id, author) on delete cascade on update cascade,
    foreign key (usr) references piiUser (email) on delete cascade on update cascade
);

CREATE TABLE react
(
    reactionType varchar(8) not null,
    post         varchar(32),
    usr          varchar(32),
    author       varchar(32),

    primary key (author, post, usr),
    foreign key (post, author) references post (id, author) on delete cascade on update cascade,
    foreign key (usr) references piiUser (email) on delete cascade on update cascade
);

CREATE TABLE repost
(
    post   varchar(32),
    usr    varchar(32),
    author varchar(32),
    primary key (post, usr, author),
    foreign key (post, author) references post (id, author) on delete cascade on update cascade,
    foreign key (usr) references piiUser (email) on delete cascade on update cascade
);


/*
     Eventos
*/

CREATE TABLE event
(
    id          varchar(32),
    name        varchar(50) not null default '',
    description varchar(200)         default '',
    location    varchar(50)          default '',
    date        timestamp,
    creatorUser varchar(32) not null,

    primary key (id),
    foreign key (creatorUser) references piiUser (email) on delete cascade on update cascade
);

CREATE TABLE participateEvent
(
    event varchar(32),
    usr   varchar(32),

    primary key (event, usr),
    foreign key (event) references event (id) on delete cascade on update cascade,
    foreign key (usr) references piiUser (email) on delete cascade on update cascade
);


/*
      Logros
*/

CREATE TABLE achievement
(
    id          varchar(32) primary key,
    name        varchar(20) not null,
    description varchar(70) not null
);

CREATE TABLE ownAchievement
(
    achiev          varchar(32),
    usr             varchar(32),
    acquisitionDate timestamp not null default now(),

    primary key (achiev, usr),
    foreign key (achiev) references achievement (id)
        on delete cascade on update cascade,
    foreign key (usr) references piiUser (email)
        on delete cascade on update cascade
);


/*
        Notifications
 */

CREATE TABLE notification
(
    id           varchar(32) primary key,
    creationDate timestamp    not null default now(),
    content      varchar(200) not null
);

CREATE TABLE haveNotification
(
    notification varchar(32),
    usr          varchar(32),

    primary key (notification, usr),
    foreign key (notification) references notification (id)
        on delete cascade on update cascade,
    foreign key (usr) references piiUser (email)
        on delete cascade on update cascade
);