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
    hash       text primary key,
    resolution text not null,
    uri        varchar(10)
);

CREATE TABLE multimediaImage
(
    hash text not null primary key references multimedia (hash)
        on delete cascade on update cascade
);

CREATE TABLE multimediaVideo
(
    hash text not null primary key references multimedia (hash)
        on delete cascade on update cascade
);


/*
      Usuarios
*/

CREATE TABLE piiUser
(
    email              text primary key,
    name               varchar(50)  not null,
    pass               varchar(256) not null,
    gender             varchar(100) CHECK (gender IN ('hombre', 'mujer', 'helicoptero apache', 'ojalá',
                                                      'helicoptero estrellado')),
    description        varchar(256),
    home               varchar(256),
    postalCode         varchar(50),
    province           varchar(50),
    country            varchar(50),
    city               varchar(50),
    birthplace         varchar(50),
    birthdate          timestamp    not null,
    registrationDate   timestamp    not null default now(),
    deathdate          timestamp,
    religion           varchar(50),
    emotionalSituation varchar(50),
    job                varchar(50),

    -- Aqui hay que pensar como va a funcionar por defecto
    profilePicture     text references multimediaImage (id)
        on delete set null on update cascade
);

CREATE TABLE phone
(
    prefix varchar(3), -- todo actualizar los demás
    phone  text,
    usr    text,

    primary key (phone, usr, prefix),
    foreign key (usr) references piiUser (email)
        on delete cascade on update cascade
);

CREATE TABLE administrator
(
    id text primary key references piiUser (email)
        on delete set null on update cascade
);

CREATE TABLE associatedAccount
(
    id    text not null primary key,
    token text, -- todo vamos a meter el token al final?
    usr   text,

    foreign key (usr) references piiUser (email)
        on delete cascade on update cascade
);


/*
      Interacciones
*/

CREATE TABLE followUser
(
    followed text,
    follower text,

    primary key (followed, follower),
    foreign key (followed) references piiUser (email)
        on delete cascade on update cascade,
    foreign key (follower) references piiUser (email)
        on delete cascade on update cascade
);

CREATE TABLE silenceUser
(
    usr      text,
    silenced text,

    primary key (usr, silenced),
    foreign key (usr) references piiUser (email)
        on delete cascade on update cascade,
    foreign key (silenced) references piiUser (email)
        on delete cascade on update cascade
);

CREATE TABLE blockUser
(
    usr     text,
    blocked text,

    primary key (usr, blocked),
    foreign key (usr) references piiUser (email)
        on delete cascade on update cascade,
    foreign key (blocked) references piiUser (email)
        on delete cascade on update cascade
);

CREATE TABLE message
(
    id         text,
    sender     text,
    text       text      not null,
    date       timestamp not null default now(),
    multimedia text,
    ticket     text,

    primary key (sender, id),
    foreign key (sender) references piiUser (email)
        on delete cascade on update cascade,
    foreign key (multimedia) references multimedia (hash)
        on delete set null on update cascade,
    foreign key (ticket) references ticket (id)
);

CREATE TABLE receiveMessage
(
    message  text,
    sender   text,
    receiver text,

    primary key (message, sender, receiver),
    foreign key (message, sender) references message (id, sender)
        on delete cascade on update cascade,
    foreign key (receiver) references piiUser (email)
        on delete cascade on update cascade
);

CREATE TABLE post
(
    author          text,
    id              text,
    text            text                    not null,
    publicationDate timestamp default now() not null,
    sugarDaddy      text,
    authorDaddy     text,
    multimedia      text,

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
    name varchar(280) not null primary key
);


CREATE TABLE ownHashtag
(
    hashtag text,
    post    text,
    author  text,

    primary key (hashtag, post, author),
    foreign key (post, author) references post (id, author) on delete cascade on update cascade
);

CREATE TABLE followHashtag
(
    piiUser text,
    hashtag text,

    primary key (piiUser, hashtag),
    foreign key (piiUser) references piiUser (email)
        on delete cascade on delete cascade,
    foreign key (hashtag) references hashtag (name)
        on delete cascade on delete cascade
);


/*
      Reacciones y Repost
*/

CREATE TABLE react
(
    reactionType text not null,
    post         text,
    usr          text,
    author       text,

    primary key (author, post, usr),
    foreign key (post, author) references post (id, author) on delete cascade on update cascade,
    foreign key (usr) references piiUser (email) on delete cascade on update cascade
);

CREATE TABLE repost
(
    post   text,
    usr    text,
    author text,
    primary key (post, usr, author),
    foreign key (post, author) references post (id, author) on delete cascade on update cascade,
    foreign key (usr) references piiUser (email) on delete cascade on update cascade
);


/*
     Eventos
*/

CREATE TABLE event
(
    id          text,
    name        text not null default '',
    description text          default '',
    location    text          default '',
    date        timestamp,
    creatorUser text not null,

    primary key (id),
    foreign key (creatorUser) references piiUser (email) on delete cascade on update cascade
);

CREATE TABLE participateEvent
(
    event text,
    usr   text,

    primary key (event, usr),
    foreign key (event) references event (id) on delete cascade on update cascade,
    foreign key (usr) references piiUser (email) on delete cascade on update cascade
);


/*
      Logros
*/

CREATE TABLE achievement
(
    id          text primary key,
    name        varchar(25)  not null,
    description varchar(100) not null
);

CREATE TABLE ownAchievement
(
    logro           text,
    usr             text,
    acquisitionDate timestamp not null default now(),

    foreign key (logro) references achievement (id) ON DELETE CASCADE ON UPDATE CASCADE,
    foreign key (usr) references piiUser (email) ON DELETE CASCADE ON UPDATE CASCADE
);


/*
      Tickets
*/

CREATE TABLE ticket
(
    id           text,
    usr          text      not null,
    section      text      not null,
    text         text      not null,
    creationDate timestamp not null default now(),
    deadline     timestamp, -- todo añadir
    adminClosing text,
    primary key (id),
    foreign key (usr) references piiUser (email) on delete set null on update cascade,
    foreign key (adminClosing) references administrator (id) on delete set null on update cascade
);


/*
        Notifications
 */

CREATE TABLE notification
(
    id           text primary key,
    creationDate timestamp not null default now(),
    content      text      not null
);

CREATE TABLE haveNotification
(
    notification text,
    usr          text,

    primary key (notification, usr),
    foreign key (notification) references notification (id)
        on delete cascade on update cascade,
    foreign key (usr) references piiUser (email)
        on delete cascade on update cascade
);