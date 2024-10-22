INSERT INTO blog(id, title, text, user_id)
VALUES  (2, 'Test Title2','Test Text2', 1),
        (3, 'Test Title3','Test Title3', 1),
        (4, 'Test Title4','Test Title4', 1);

INSERT INTO tag(id,name)
VALUES (2,'Test Tag2'),
       (3,'Test Tag3'),
       (4,'Test Tag4');

INSERT INTO blog_tags(blog_id,tag_id)
VALUES (2,2),
       (2,3),
       (3,3),
       (3,4);

