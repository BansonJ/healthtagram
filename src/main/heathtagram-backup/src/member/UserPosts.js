import React, { useState, useEffect, useRef } from "react";
import { useParams, useNavigate } from "react-router-dom";
import axios from "axios";
import Sidebar from "../post/Sidebar";  // Sidebar 컴포넌트 추가
import styles from "../css/UserPosts.module.css";  // CSS 모듈 import

const UserPosts = () => {
  const { nickname } = useParams();  // URL에서 nickname을 받아옵니다.
  const queryParams = new URLSearchParams(window.location.search);
  const initialPostId = queryParams.get("lastPostId");
  const [posts, setPosts] = useState([]);
  const [loading, setLoading] = useState(false);
  const [errorMessage, setErrorMessage] = useState(null);
  const [lastPostId, setLastPostId] = useState(initialPostId || 100000000000000000);
  const [hasMore, setHasMore] = useState(true);
  const observerRef = useRef(null);  // 무한 스크롤 감지용 ref
  const isAuthenticated = Boolean(localStorage.getItem("jwt_token"));  // 로그인 여부 확인
  const navigate = useNavigate();

  // 유저 게시물 가져오기 (무한 스크롤 적용)
  const fetchUserPosts = async () => {
    if (loading || !hasMore) return;
    setLoading(true);
    setErrorMessage(null);
    const storedNickname = localStorage.getItem("nickname"); // nickname 가져오기
    console.log(storedNickname);

    try {
      const response = await axios.get(`http://localhost:8080/api/memberPage/${nickname}/post`, {
        params: { lastPostId, storedNickname },
        headers: { Authorization: localStorage.getItem("jwt_token") },
        withCredentials: true,
      });

      const newPosts = response.data;
      if (newPosts.length === 0) {
        setHasMore(false);
      } else {
        setPosts((prevPosts) => [...prevPosts, ...newPosts]);
        setLastPostId(newPosts[newPosts.length - 1].postId);
      }
    } catch (error) {
      setErrorMessage("게시물을 가져오는 데 실패했습니다.");
      console.error(error);
    }
    setLoading(false);
  };

  // 좋아요 토글 기능
  const toggleLike = async (postId, likeState) => {
    try {
      const token = localStorage.getItem("jwt_token");
      const storedNickname = localStorage.getItem("nickname"); // nickname 가져오기
      const url = likeState
        ? `http://localhost:8080/api/cancelLikePost/${postId}`
        : `http://localhost:8080/api/likePost/${postId}`;

      await axios.put(url, null, {
        headers: { Authorization: token },
        params: { storedNickname },  // nickname을 쿼리 파라미터로 전송
      });

      // 게시물의 좋아요 상태 업데이트
      setPosts((prevPosts) =>
        prevPosts.map((post) =>
          post.postId === postId
            ? {
                ...post,
                likeState: !likeState,
                heartCount: likeState ? post.heartCount - 1 : post.heartCount + 1
              }
            : post
        )
      );
    } catch (error) {
      console.error("좋아요 처리 실패", error);
    }
  };

  // 댓글 페이지로 이동
  const navigateToReplies = (postId) => {
    navigate(`/post/${postId}/reply`);
  };

  useEffect(() => {
    fetchUserPosts();
  }, [nickname]);

  // 무한 스크롤 감지
  useEffect(() => {
    observerRef.current = new IntersectionObserver(
      (entries) => {
        if (entries[0].isIntersecting && hasMore && !loading) {
          fetchUserPosts();
        }
      },
      { threshold: 1.0 }
    );

    if (observerRef.current && document.getElementById("scroll-trigger")) {
      observerRef.current.observe(document.getElementById("scroll-trigger"));
    }

    return () => observerRef.current?.disconnect();
  }, [hasMore, loading]);

  return (
    <div className={styles.userPostsContainer}>
      {/* 사이드바 추가 */}
      <Sidebar isAuthenticated={isAuthenticated} />

      {/* 게시물 섹션 */}
      <div className={styles.postsSection}>
        <h1>{nickname}의 게시물</h1>

        {posts.length === 0 ? (
          <p>게시물이 없습니다.</p>
        ) : (
          <div className={styles.postList}>
            {posts.map((post) => (
              <div key={post.postId} className={styles.postContainer}>
                <div className={styles.postHeader}>
                  <span className={styles.postNickname}>{nickname}</span>
                  <span className={styles.postTime}>{new Date(post.createdAt).toLocaleString()}</span>
                </div>

                <ImageSlider images={post.filePath} />

                <div className={styles.postLikeSection}>
                  <button className={styles.postLikeButton} onClick={() => toggleLike(post.postId, post.likeState)}>
                    {post.likeState ? "❤️" : "🤍"}
                  </button>
                  <span className={styles.postLikes}>{post.heartCount}</span>

                  <button className={styles.postCommentButton} onClick={() => navigateToReplies(post.postId)}>
                     💬
                  </button>
                </div>

                <div className={styles.postContent}>{post.content}</div>

                {/* 태그 표시 */}
                {post.tagList && post.tagList.trim().length > 0 && (
                  <div className={styles.postTags}>
                    {post.tagList.split(",").map((tag, index) => (
                      <span key={index} className={styles.postTag}>
                        {tag.startsWith("#") ? tag : `#${tag}`}
                      </span>
                    ))}
                  </div>
                )}
              </div>
            ))}
          </div>
        )}

        {/* 무한 스크롤 트리거 (마지막 부분) */}
        <div id="scroll-trigger" style={{ height: "10px" }}></div>

        {loading && <p>로딩 중...</p>}
        {errorMessage && <p className="error-message">{errorMessage}</p>}
      </div>
    </div>
  );
};

const ImageSlider = ({ images }) => {
  const [currentIndex, setCurrentIndex] = useState(0);

  if (!images || !Array.isArray(images) || images.length === 0) {
    return null;
  }

  const nextImage = () => {
    if (currentIndex < images.length - 1) {
      setCurrentIndex((prevIndex) => prevIndex + 1);
    }
  };

  const prevImage = () => {
    if (currentIndex > 0) {
      setCurrentIndex((prevIndex) => prevIndex - 1);
    }
  };

  return (
    <div className={styles.imageSlider}>
      <img src={images[currentIndex]} alt="Post" className={styles.postImage} />

      {images.length > 1 && (
        <div className={styles.sliderControls}>
          {currentIndex > 0 && <button className={styles.prevButton} onClick={prevImage}>◀</button>}
          {currentIndex < images.length - 1 && <button className={styles.nextButton} onClick={nextImage}>▶</button>}
        </div>
      )}
    </div>
  );
};

export default UserPosts;
