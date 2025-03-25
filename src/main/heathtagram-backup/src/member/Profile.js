import React, { useState, useEffect, useRef } from "react";
import { useNavigate, useParams } from "react-router-dom";
import axios from "axios";
import Sidebar from "../post/Sidebar";  // 사이드바 임포트
import "../css/Profile.css";

const Profile = () => {
  const { nickname } = useParams();  // URL에서 nickname을 가져옵니다.
  const storedNickname = localStorage.getItem("nickname");
  const [userProfile, setUserProfile] = useState(null);
  const [loading, setLoading] = useState(false);
  const [errorMessage, setErrorMessage] = useState(null);
  const [lastPostId, setLastPostId] = useState(Number.MAX_SAFE_INTEGER);
  const [hasMore, setHasMore] = useState(true);
  const observerRef = useRef(null);
  const navigate = useNavigate();

  useEffect(() => {
    console.log("닉네임 변경 감지:", nickname);

    if (!nickname) {
      setErrorMessage("잘못된 URL입니다.");
      return;
    }

    fetchUserProfile();  // nickname이 변경될 때마다 API를 호출합니다.
  }, [nickname]);  // nickname이 바뀔 때마다 실행되도록 의존성 배열에 추가

  useEffect(() => {
    console.log("nickname from URL:", nickname, storedNickname);

    if (!storedNickname) {
      setErrorMessage("유효한 닉네임이 필요합니다.");
      return;
    }

    fetchUserProfile();  // 최초 렌더링 시에도 호출
  }, [storedNickname, nickname]);  // storedNickname이 바뀔 때마다 실행되도록 설정

  const fetchUserProfile = async () => {
    if (loading || !hasMore) return;  // 이미 로딩 중이면 더 이상 요청하지 않음

    setLoading(true);  // 로딩 상태 설정
    setErrorMessage(null);  // 오류 메시지 초기화

    try {
      console.log("api 요청 시작:", nickname);
      const response = await axios.get(`http://localhost:8080/api/memberPage/${nickname}`, {
        params: { storedNickname: storedNickname, lastPostId: lastPostId },
      });

      if (!response.data || !response.data.nickname) {
        setErrorMessage("유저 정보를 불러올 수 없습니다.");
        return;
      }

      setUserProfile((prevProfile) => ({
        nickname: response.data.nickname,
        profilePicture: response.data.profilePicture || "/default-profile.png",
        filePath: [...(prevProfile?.filePath || []), ...response.data.filePath],
        postList: [...(prevProfile?.postList || []), ...response.data.postList],
        followState: response.data.followState || "unfollow",
        followerCount: response.data.follower,  // 팔로워 수
        followingCount: response.data.following, // 팔로잉 수
      }));

      if (response.data.postList.length === 0) {
        setHasMore(false);  // 더 이상 불러올 게시물이 없으면 hasMore를 false로 설정
      } else {
        const newPosts = response.data.postList;
        if (newPosts.length > 0) {
          setLastPostId(newPosts[newPosts.length - 1].id);  // 마지막 게시물 ID 업데이트
        }
      }
    } catch (error) {
      setErrorMessage("프로필을 가져오는 데 실패했습니다.");
      console.error(error);
    }

    setLoading(false);  // 로딩 상태 해제
  };

  useEffect(() => {
    const triggerElement = document.getElementById("profile-scroll-trigger");

    if (!triggerElement) return;

    const observer = new IntersectionObserver(
      (entries) => {
        if (entries[0].isIntersecting && hasMore && !loading) {
          fetchUserProfile();  // 스크롤이 내려가면 더 많은 프로필을 불러옵니다.
        }
      },
      { threshold: 1 }
    );

    observer.observe(triggerElement);

    return () => {
      observer.disconnect(); // 컴포넌트 언마운트 시 observer 해제
    };
  }, [hasMore, loading]);  // hasMore, loading 상태가 변경될 때마다 실행

  const handlePostClick = (id) => {
    if (!id) {
      console.error("postId가 없습니다!");
      return;
    }
    navigate(`/memberPage/${nickname}/post?lastPostId=${id}`);
  };

  const handleFollow = async () => {
    try {
      let url = "";
      let method = "";

      if (userProfile.followState === "me") {
        console.log("자기 자신을 팔로우하거나 언팔로우할 수 없습니다.");
        return;
      }

      if (userProfile.followState === "unfollow") {
        url = `http://localhost:8080/api/follow/${nickname}`;
        method = "post";
      } else {
        url = `http://localhost:8080/api/unfollow/${nickname}`;
        method = "delete";
      }

      await axios({
        method: method,
        url: url,
        params: { storedNickname },
      });

      setUserProfile((prevProfile) => ({
        ...prevProfile,
        followState: prevProfile.followState === "unfollow" ? "follow" : "unfollow",
      }));
    } catch (error) {
      console.error("팔로우 처리 실패", error);
    }
  };

  if (loading && !userProfile) return <div>로딩 중...</div>;
  if (errorMessage) return <div>{errorMessage}</div>;
  if (!userProfile) return <div>유저 프로필을 찾을 수 없습니다.</div>;

  return (
    <div className="profile-container">
      <Sidebar isAuthenticated={!!storedNickname} /> {/* 사이드바 추가 */}

      <div className="profile-content">
        <div className="profile-header">
          <img
            src={userProfile.profilePicture}
            alt="프로필"
            className="profile-picture"
          />
          <div className="profile-info">
            <h1 className="nickname">{userProfile.nickname}</h1>
            {userProfile.followState !== "me" && (
              <button className="follow-button" onClick={handleFollow}>
                {userProfile.followState === "unfollow" ? "팔로우" : "언팔로우"}
              </button>
            )}
          </div>
        </div>

        {/* 팔로워 및 팔로잉 숫자 배치 */}
        <div className="follow-info">
          <button
            className="follower-info"
            onClick={() => navigate(`/follower/${nickname}`)}
          >
            <p>팔로워</p>
            <p>{userProfile.followerCount}명</p>
          </button>
          <button
            className="following-info"
            onClick={() => navigate(`/following/${nickname}`)}
          >
            <p>팔로잉</p>
            <p>{userProfile.followingCount}명</p>
          </button>
        </div>

        {/* 게시물 그리드 */}
        <div className="post-grid">
          {userProfile.filePath.length <= 0 ? (
            <p>게시물이 없습니다.</p>
          ) : (
            userProfile.filePath.map((postUrl, index) => (
              <div
                key={index}
                className="post-item"
                onClick={() => handlePostClick(userProfile.postList[index]?.id + 1)}
              >
                <img
                  src={postUrl}
                  alt="게시물"
                  className="post-thumbnail"
                />
              </div>
            ))
          )}
        </div>

        <div id="profile-scroll-trigger" style={{ height: "10px" }}></div>
      </div>
    </div>
  );
};

export default Profile;
