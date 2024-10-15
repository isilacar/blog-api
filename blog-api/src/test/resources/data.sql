INSERT INTO user_tbl(id,username,password,display_name)
VALUES (3,'username1','password1','display name');

INSERT INTO blog(id, title, text, user_id)
VALUES  (2, 'Test Title2','Test Text2', 3),
        (3, 'Test Title3','Test Title3', 3),
        (4, 'Test Title4','Test Title4', 3);

INSERT INTO tag(id,name)
VALUES (2,'Test Tag2'),
       (3,'Test Tag3'),
       (4,'Test Tag4');

INSERT INTO blog_tags(blog_id,tag_id)
VALUES (2,2),
       (2,3),
       (3,3),
       (3,4);

