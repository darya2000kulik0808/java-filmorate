MERGE INTO GENRE
    (GENRE_ID, GENRE_NAME)
    KEY (GENRE_ID)
    VALUES (1, 'Комедия'),
    (2, 'Драма'),
    (3, 'Мультфильм'),
    (4, 'Триллер'),
    (5, 'Документальный'),
    (6, 'Боевик');

MERGE INTO MPA
    (MPA_ID, MPA_NAME)
    KEY (MPA_ID)
    VALUES (1, 'G'),
    (2, 'PG'),
    (3, 'PG-13'),
    (4, 'R'),
    (5, 'NC-17');
