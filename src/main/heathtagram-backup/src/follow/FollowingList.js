// FollowingList.js
import React, { useState, useEffect } from "react";
import axios from "axios";
import { useParams } from "react-router-dom";
import Sidebar from "../post/Sidebar"; // 사이드바 컴포넌트 임포트
import "../css/FollowingList.css";

const FollowingList = () => {
  const { nickname } = useParams();
  const storedNickname = localStorage.getItem("nickname");
  const [following, setFollowing] = useState([]);
  const [loading, setLoading] = useState(true);
  const [errorMessage, setErrorMessage] = useState(null);
  const [isAuthenticated, setIsAuthenticated] = useState(!!storedNickname);

  useEffect(() => {
    const fetchFollowing = async () => {
      try {
        const response = await axios.get("http://localhost:8080/api/followingList", {
          params: { nickname },
        });
        setFollowing(response.data);
      } catch (error) {
        setErrorMessage("팔로잉 목록을 불러오는 데 실패했습니다.");
        console.error(error);
      }
      setLoading(false);
    };

    fetchFollowing();
  }, [nickname]);

  if (loading) return <div>로딩 중...</div>;
  if (errorMessage) return <div>{errorMessage}</div>;

  return (
    <div className="following-list-container">
      <Sidebar isAuthenticated={isAuthenticated} /> {/* 사이드바 추가 */}
      <div className="following-content">
        <h1>{nickname}의 팔로잉</h1>
        {following.length === 0 ? (
          <p>팔로잉이 없습니다.</p>
        ) : (
          following.map((followedUser) => (
            <div key={followedUser.nickname} className="following-item">
              <img
                src={followedUser.profilePicture || "/default-profile.png"}
                alt={followedUser.nickname}
                className="following-profile-picture"
              />
              <p>{followedUser.nickname}</p>
            </div>
          ))
        )}
      </div>
    </div>
  );
};

export default FollowingList;
