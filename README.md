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
2024-08-21 : Update homepage and follow function and implement like function.</br>
2024-08-22 : Update like function and add search with tag function to post.</br>
2024-08-26 : Implement reply function.</br>
2024-09-12 : Update README.md file.</br>
2024-10-11 : Implement real-time chatting function.</br>
2024-10-18 : Implement unit test code.</br>
2025-02-13 : Add monitoring function with grafana and prometheus.</br>
2025-03-12 : Change DB from mysql to mongoDB and elasticsearch.</br>
2025-03-25 : Implement frontend with react and update README.md.</br>
2025-03-26 : Fix all controller and remove unnecessary files.</br>

</br>

<h2>DB</h2>
<h3>
mysql - member, follow, postImage<br>
mongoDB - post, reply, postHeart, replyHeart<br>
elasticsearch - tag
</h3>

![new dbdiagram](https://github.com/user-attachments/assets/7af48dc2-2afc-4483-ac16-190b46e4c9e7)

</br>
</br>

<h2>api 명세서</h2>

![스크린샷 2025-03-25 201228](https://github.com/user-attachments/assets/4d910a59-7cca-4297-8ddf-edfa4fb7e3c9)
![스크린샷 2025-03-25 201240](https://github.com/user-attachments/assets/1c365428-33c1-417e-b4f8-8c49d416e4a3)
![스크린샷 2025-03-25 201249](https://github.com/user-attachments/assets/19d197f5-88bc-4aa4-aefb-f7e68a2ad504)
![스크린샷 2025-03-25 201307](https://github.com/user-attachments/assets/fe4bf495-90ac-4df7-bb68-b05efb401090)
![스크린샷 2025-03-25 201319](https://github.com/user-attachments/assets/a787385d-d672-4591-899a-b039e8da1279)

<h2>화면 첨부</h2>