// FollowersList.js
import React, { useState, useEffect } from "react";
import axios from "axios";
import { useParams } from "react-router-dom";
import Sidebar from "../post/Sidebar"; // 사이드바 컴포넌트 임포트
import "../css/FollowersList.css";

const FollowersList = () => {
  const { nickname } = useParams();
  const storedNickname = localStorage.getItem("nickname");
  const [followers, setFollowers] = useState([]);
  const [loading, setLoading] = useState(true);
  const [errorMessage, setErrorMessage] = useState(null);
  const [isAuthenticated, setIsAuthenticated] = useState(!!storedNickname);

  useEffect(() => {
    const fetchFollowers = async () => {
      try {
        const response = await axios.get("http://localhost:8080/api/followerList", {
          params: { nickname },
        });
        setFollowers(response.data);
      } catch (error) {
        setErrorMessage("팔로워 목록을 불러오는 데 실패했습니다.");
        console.error(error);
      }
      setLoading(false);
    };

    fetchFollowers();
  }, [nickname]);

  if (loading) return <div>로딩 중...</div>;
  if (errorMessage) return <div>{errorMessage}</div>;

  return (
    <div className="followers-list-container">
      <Sidebar isAuthenticated={isAuthenticated} /> {/* 사이드바 추가 */}
      <div className="followers-content">
        <h1>{nickname}의 팔로워</h1>
        {followers.length === 0 ? (
          <p>팔로워가 없습니다.</p>
        ) : (
          followers.map((follower) => (
            <div key={follower.nickname} className="follower-item">
              <img
                src={follower.profilePicture || "/default-profile.png"}
                alt={follower.nickname}
                className="follower-profile-picture"
              />
              <p>{follower.nickname}</p>
            </div>
          ))
        )}
      </div>
    </div>
  );
};

export default FollowersList;
