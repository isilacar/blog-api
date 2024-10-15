# <span style="color:gold">BLOG API</span>

## <span style="color:hotpink">About this application </span>

- This application provides you to creating your own blogs with unique tags.
- You can update your own blogs and can add or remove tag from the specific blog. You can get all the blogs with
  their tags or you can get the simplified ones with blog's title and text.
- You also have chance to get all the blogs by specific tag name.
- With the help of  [Postman API](https://www.postman.com/) you can send a request to any end-point.

## <span style="color:lightgreen">  ***Note*** </span>

> This application built with Spring Boot 3.3.2 and Java 17.

## <span style="color:hotpink">Api End-Point Explanations</span>

### ⭐️ <span style="color:yellow">blog-controller</span> ⭐️️️

✨ <span style="color:lime">Posting new blog - http://localhost:8080/api/blogs</span>

- You can create your own blogs with this end-point. You can send a request with \*json object\* as I defined below.

                {
                    "title": "Test title",
                    "text": "Test text",
                    "tagDtoSet": [
                        {
                            "name": "test tag1"
                        },
                        {
                            "name": "test tag2"
                        }
                    ]
                }

    - Payload as an example

          {
            "id": 1,
            "title": "Test title",
            "text": "Test text",
            "tagDtoSet": [
              {
                "id": 1,
                "name": "test tag1"
              },
              {
                "id": 2,
                "name": "test tag2"
              }
            ]
          }

✨ <span style="color:lime">Getting all the blogs - http://localhost:8080/api/blogs</span>

- You can get all the blogs and their tags with the help of this specific end-point.
    - Payload as an example:

                [
                    {
                        "id": 1,
                        "title": "Test title",
                        "text": "Test text",  
                        "tagDtoSet": [
                            {
                                "id": 1,
                                "name": "test tag1"
                            },
                            {
                                "id": 2,
                                "name": "test tag2"
                            }
                        ]
                    }
                ]

✨ <span style="color:lime">Updating the blog - http://localhost:8080/api/blogs/{blogId}</span>

- You can update the existing blog's title and text.
  You have to define the \*blog id\* in the url which you want to update
  and can send request with json object as I defined below.

            {
              "title":"updated title",
              "text":"updated text"
            }

✨ <span style="color:lime">Get blogs by the specific tag name - http://localhost:8080/api/blogs/tagName/{tagName}</span>

- You can get all the blogs which have same tag name. You have to define the \*tag name\* in the url.
    - Let's say we want to get all the blogs with the tag name is **test tag2**
        - Payload as an example

                [
                  { 
                    "id": 2,
                    "title": "Test title1",
                    "text": "Test text1",
                    "tagDtoSet": [
                        {
                            "id": 4,
                            "name": "test tag1"
                        },
                        {
                            "id": 3,
                            "name": "test tag2"
                        }
                    ]
                  },
                  {
                    "id": 3,
                    "title": "Test title2",
                    "text": "Test text2",
                    "tagDtoSet": [
                        {
                            "id": 6,
                            "name": "test tag2"
                        },
                        {
                            "id": 5,
                            "name": "test tag3"
                        }
                    ]
                  }
                ]

✨ <span style="color:lime">Adding new Tag to the existing Blog - http://localhost:8080/api/blogs/{blogId}/tags</span>

- You can add new Tag to the existing blog with the help of this end-point.You have to define the \*blog id\* in
  the url and can add \*json object\* as I defined below.

        {
         "tagName":"new tag"
         }

✨ <span style="color:lime">Removing Tag from the existing
Blog - http://localhost:8080/api/blogs/{blogId}/tags/{tagId}</span>

- If you want to remove any Tag from specific Blog this end-point will help you. You have to define the \*blog id\* and
  \*tag id\*
  in the url.
- After sending the request, you can observe that specific tag does not belong to the specific blog anymore.

✨ <span style="color:lime">Get simplified blogs -http://localhost:8080/api/blogs/simplified</span>

- If you want to get all the blogs with title and text only, you can send request to this end-point.
    - Payload as an example

          [
              {
                  "title": "Test title1",
                  "text": "Test text1"
              },
              {
                  "title": "Test title2",
                  "text": "Test text2"
              }   
          ]
## <span style="color:hotpink">Run Application With SWAGGER</span>

- If you want to send request to any end-point you can also use [SWAGGER](http://localhost:8081/swagger-ui/index.html#/)

## <span style="color:hotpink">Run Application With DOCKER</span>

- If you want to run the application you can simply use this command
    - <span style="color:deepskyblue"> docker compose up -d</span>

        - -d means,run my application on background, do not affect my work.
    - After the containers are up and running you can send request to any end-point
      with the help of the POSTMAN or [SWAGGER](http://localhost:8081/swagger-ui/index.html#/)
    - But first do not forget
      to [register](http://localhost:8081/swagger-ui/index.html#/CRUD%20REST%20APIs%20for%20User%20Resource/register) to
      the system.

## 👩‍💻 <span style="color:hotpink">Dependencies I Use</span>

- Spring Web
- Lombok
- Maven
- H2 Database
- Swagger
- Docker


