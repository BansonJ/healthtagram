<h3>
인스타 같은 sns를 생각하며 만든 개인 프로젝트입니다.</br>
docker-compose를 이용해 컨테이너화 해놓은 상태이고 grafana와 prometheus를 이용한 monitoring까지
구축해놓았습니다.<br>
백엔드의 기능은 전부 혼자 만들었고 프론트에 대한 부분은 제가 만든게 아니라 어느정도 이해만 하는 상태입니다.
</br>
</h3>

</br>

2024-08-03 : First commit. Implement signup and signin function.</br>
2024-08-07 : Add removing account and user profile function.</br>
2024-08-16 : Implement posting and homepage and follow function.</br>
2024-08-21 : Update homepage and follow function and implement like function</br>
2024-08-22 : Update like function and add search with tag function to post</br>
2024-08-26 : Implement reply function</br>
2024-09-12 : Update README.md file</br>
2024-10-11 : Implement real-time chatting function</br>
2024-10-18 : Implement unit test code</br>
2025-02-13 : Add monitoring function with grafana and prometheus</br>
2025-03-12 : Change DB from mysql to mongoDB and elasticsearch</br>
2025-03-25 : Implement frontend with react and update README.md</br>


</br>

<h2>DB</h2>
<h3>
mysql - member, follow, postImage<br>
mongoDB - post, reply, postHeart, replyHeart<br>
elasticsearch - tag
</h3>

</br>
</br>

<h2>api 명세(새로 작성 필요)</h2>
![image](https://github.com/user-attachments/assets/58d84014-6757-4bd2-82d1-dacad9e41fee)

![image](https://github.com/user-attachments/assets/e57bd4ba-8139-4ea9-b7ea-273eaf0d1949)
